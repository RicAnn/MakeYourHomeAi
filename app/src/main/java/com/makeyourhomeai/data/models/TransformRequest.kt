package com.makeyourhomeai.data.models

data class TransformRequest(
    val roomType: RoomType,
    val style: DesignStyle,
    val imageUri: String
)

data class TransformResult(
    val originalImageUri: String,
    val transformedImageUrl: String,
    val roomType: RoomType,
    val style: DesignStyle,
    val timestamp: Long = System.currentTimeMillis()
)
