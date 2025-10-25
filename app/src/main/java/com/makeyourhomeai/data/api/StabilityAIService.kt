package com.makeyourhomeai.data.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface StabilityAIService {
    @Multipart
    @POST("v1/generation/stable-diffusion-xl-1024-v1-0/image-to-image")
    suspend fun transformImage(
        @Header("Authorization") authorization: String,
        @Part("init_image_mode") initImageMode: RequestBody,
        @Part init_image: MultipartBody.Part,
        @Part("text_prompts[0][text]") textPrompt: RequestBody,
        @Part("text_prompts[0][weight]") textPromptWeight: RequestBody,
        @Part("cfg_scale") cfgScale: RequestBody,
        @Part("samples") samples: RequestBody,
        @Part("steps") steps: RequestBody,
        @Part("image_strength") imageStrength: RequestBody
    ): Response<StabilityAIResponse>
}
