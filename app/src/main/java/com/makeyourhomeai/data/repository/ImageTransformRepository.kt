package com.makeyourhomeai.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.TimeUnit
import kotlin.math.min

class ImageTransformRepository {
    // Client con timeout aumentati per API lente
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)    // 2 minuti per la risposta
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val apiKey = "sk-GmrhCzfBj8FQPICcsC7VJckv49PttRT4Z3mNuNzBpf5t5gaZ"

    // Dimensioni supportate dall'API Stability AI
    private val allowedDimensions = listOf(
        1024 to 1024,
        1152 to 896,
        1216 to 832,
        1344 to 768,
        1536 to 640,
        640 to 1536,
        768 to 1344,
        832 to 1216,
        896 to 1152
    )

    suspend fun transformImage(
        context: Context,
        imageUri: Uri,
        prompt: String
    ): String = withContext(Dispatchers.IO) {
        // Leggi l'immagine
        val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Correggi l'orientamento dell'immagine usando EXIF
        val correctedBitmap = fixImageOrientation(context, imageUri, originalBitmap)

        // Trova le dimensioni migliori
        val (targetWidth, targetHeight) = findBestAllowedDimensions(
            correctedBitmap.width,
            correctedBitmap.height
        )

        // Ridimensiona e adatta l'immagine
        val resizedBitmap = resizeAndFitBitmap(correctedBitmap, targetWidth, targetHeight)

        // Comprimi sotto 5MB
        val imageBytes = compressBitmap(resizedBitmap)

        // Costruisci la richiesta multipart
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "image.jpg",
                imageBytes.toRequestBody("image/jpeg".toMediaType())
            )
            .addFormDataPart("prompt", prompt)
            .addFormDataPart("output_format", "jpeg")
            .addFormDataPart("mode", "image-to-image")
            .addFormDataPart("seed", "0")
            .addFormDataPart("cfg_scale", "7")
            .addFormDataPart("samples", "1")
            .addFormDataPart("steps", "40")
            .addFormDataPart("strength", "0.50")
            .build()

        val request = Request.Builder()
            .url("https://api.stability.ai/v2beta/stable-image/generate/sd3")
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Accept", "image/*")
            .post(requestBody)
            .build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            val errorBody = response.body?.string() ?: "Unknown error"
            throw Exception("API Error ${response.code}: $errorBody")
        }

        // Salva l'immagine risultante
        val resultBytes = response.body?.bytes() ?: throw Exception("Empty response")
        val resultBitmap = BitmapFactory.decodeByteArray(resultBytes, 0, resultBytes.size)

        // Salva in cache e ritorna l'URI
        val outputStream = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        val file = java.io.File(context.cacheDir, "transformed_${System.currentTimeMillis()}.jpg")
        file.writeBytes(outputStream.toByteArray())

        file.absolutePath
    }

    private fun fixImageOrientation(context: Context, imageUri: Uri, bitmap: Bitmap): Bitmap {
        try {
            val inputStream = context.contentResolver.openInputStream(imageUri) ?: return bitmap
            val exif = ExifInterface(inputStream)
            inputStream.close()

            val orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )

            val matrix = Matrix()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
                ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
                else -> return bitmap // Nessuna rotazione necessaria
            }

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } catch (e: Exception) {
            e.printStackTrace()
            return bitmap // In caso di errore, ritorna l'immagine originale
        }
    }

    private fun findBestAllowedDimensions(width: Int, height: Int): Pair<Int, Int> {
        val aspectRatio = width.toFloat() / height.toFloat()

        return allowedDimensions.minByOrNull { (w, h) ->
            val allowedRatio = w.toFloat() / h.toFloat()
            kotlin.math.abs(aspectRatio - allowedRatio)
        } ?: (1024 to 1024)
    }

    private fun resizeAndFitBitmap(source: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        val sourceRatio = source.width.toFloat() / source.height.toFloat()
        val targetRatio = targetWidth.toFloat() / targetHeight.toFloat()

        val scale: Float
        val dx: Float
        val dy: Float

        if (sourceRatio > targetRatio) {
            // Immagine più larga, scala in base all'altezza
            scale = targetHeight.toFloat() / source.height.toFloat()
            dx = (targetWidth - source.width * scale) / 2f
            dy = 0f
        } else {
            // Immagine più alta, scala in base alla larghezza
            scale = targetWidth.toFloat() / source.width.toFloat()
            dx = 0f
            dy = (targetHeight - source.height * scale) / 2f
        }

        val matrix = Matrix().apply {
            setScale(scale, scale)
            postTranslate(dx, dy)
        }

        val result = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(source, matrix, null)

        return result
    }

    private fun compressBitmap(bitmap: Bitmap): ByteArray {
        var quality = 90
        var outputStream = ByteArrayOutputStream()

        do {
            outputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 10
        } while (outputStream.size() > 5 * 1024 * 1024 && quality > 10)

        return outputStream.toByteArray()
    }
}