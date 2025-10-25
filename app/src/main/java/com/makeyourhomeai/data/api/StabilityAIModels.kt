package com.makeyourhomeai.data.api

import com.google.gson.annotations.SerializedName

data class StabilityAIResponse(
    @SerializedName("artifacts")
    val artifacts: List<Artifact>
)

data class Artifact(
    @SerializedName("base64")
    val base64: String,
    @SerializedName("finishReason")
    val finishReason: String
)
