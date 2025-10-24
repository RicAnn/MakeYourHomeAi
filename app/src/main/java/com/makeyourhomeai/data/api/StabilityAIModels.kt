package com.makeyourhomeai.data.api

import com.google.gson.annotations.SerializedName

// Request models
data class ImageToImageRequest(
    @SerializedName("init_image")
    val initImage: String, // Base64 encoded image
    
    @SerializedName("text_prompts")
    val textPrompts: List<TextPrompt>,
    
    @SerializedName("cfg_scale")
    val cfgScale: Double = 7.0,
    
    @SerializedName("samples")
    val samples: Int = 1,
    
    @SerializedName("steps")
    val steps: Int = 30,
    
    @SerializedName("image_strength")
    val imageStrength: Double = 0.35
)

data class TextPrompt(
    @SerializedName("text")
    val text: String,
    
    @SerializedName("weight")
    val weight: Double = 1.0
)

// Response models
data class StabilityResponse(
    @SerializedName("artifacts")
    val artifacts: List<Artifact>
)

data class Artifact(
    @SerializedName("base64")
    val base64: String,
    
    @SerializedName("seed")
    val seed: Long,
    
    @SerializedName("finishReason")
    val finishReason: String
)

// Error models
data class StabilityError(
    @SerializedName("id")
    val id: String?,
    
    @SerializedName("message")
    val message: String,
    
    @SerializedName("name")
    val name: String?
)
