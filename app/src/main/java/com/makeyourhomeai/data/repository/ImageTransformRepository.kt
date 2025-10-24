package com.makeyourhomeai.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import com.makeyourhomeai.data.api.ImageToImageRequest
import com.makeyourhomeai.data.api.StabilityAIService
import com.makeyourhomeai.data.api.TextPrompt
import com.makeyourhomeai.data.models.DesignStyle
import com.makeyourhomeai.data.models.RoomType
import com.makeyourhomeai.data.models.TransformResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageTransformRepository(
    private val context: Context,
    private val apiKey: String
) {
    private val service = StabilityAIService.create(apiKey)
    
    sealed class Result<out T> {
        data class Success<T>(val data: T) : Result<T>()
        data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
        object Loading : Result<Nothing>()
    }
    
    suspend fun transformImage(
        imageUri: Uri,
        roomType: RoomType,
        style: DesignStyle
    ): Result<TransformResult> = withContext(Dispatchers.IO) {
        try {
            // 1. Carica e prepara l'immagine
            val bitmap = loadAndResizeBitmap(imageUri)
            val base64Image = bitmapToBase64(bitmap)
            
            // 2. Crea il prompt
            val prompt = buildPrompt(roomType, style)
            
            // 3. Chiama l'API
            val request = ImageToImageRequest(
                initImage = base64Image,
                textPrompts = listOf(
                    TextPrompt(text = prompt, weight = 1.0),
                    TextPrompt(text = "blurry, bad quality, distorted", weight = -1.0)
                ),
                cfgScale = 7.0,
                samples = 1,
                steps = 30,
                imageStrength = 0.35
            )
            
            val response = service.imageToImage(
                authorization = "Bearer $apiKey",
                request = request
            )
            
            if (response.isSuccessful && response.body() != null) {
                val artifact = response.body()!!.artifacts.firstOrNull()
                if (artifact != null) {
                    // 4. Salva l'immagine trasformata
                    val savedUri = saveBase64Image(artifact.base64)
                    
                    Result.Success(
                        TransformResult(
                            originalImageUri = imageUri.toString(),
                            transformedImageUrl = savedUri.toString(),
                            roomType = roomType,
                            style = style
                        )
                    )
                } else {
                    Result.Error("Nessuna immagine generata")
                }
            } else {
                Result.Error("Errore API: ${response.code()} - ${response.message()}")
            }
        } catch (e: Exception) {
            Result.Error("Errore durante la trasformazione: ${e.message}", e)
        }
    }
    
    private fun buildPrompt(roomType: RoomType, style: DesignStyle): String {
        return "${roomType.promptPrefix} ${style.description}, " +
                "professional interior design, high quality, detailed, " +
                "architectural photography, bright natural lighting"
    }
    
    private fun loadAndResizeBitmap(uri: Uri): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream?.close()
        
        // Calcola il sample size per ridimensionare a max 1024x1024
        val maxSize = 1024
        var inSampleSize = 1
        
        if (options.outHeight > maxSize || options.outWidth > maxSize) {
            val halfHeight = options.outHeight / 2
            val halfWidth = options.outWidth / 2
            
            while (halfHeight / inSampleSize >= maxSize && halfWidth / inSampleSize >= maxSize) {
                inSampleSize *= 2
            }
        }
        
        val decodingOptions = BitmapFactory.Options().apply {
            this.inSampleSize = inSampleSize
        }
        
        val newInputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(newInputStream, null, decodingOptions)
        newInputStream?.close()
        
        return bitmap ?: throw Exception("Impossibile caricare l'immagine")
    }
    
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }
    
    private fun saveBase64Image(base64: String): Uri {
        val imageBytes = Base64.decode(base64, Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        
        val filename = "transformed_${System.currentTimeMillis()}.png"
        val file = File(context.getExternalFilesDir(null), filename)
        
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        
        return Uri.fromFile(file)
    }
}
