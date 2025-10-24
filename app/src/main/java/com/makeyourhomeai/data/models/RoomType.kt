package com.makeyourhomeai.data.models

enum class RoomType(val displayName: String, val promptPrefix: String) {
    LIVING_ROOM("Soggiorno", "a modern living room with"),
    KITCHEN("Cucina", "a modern kitchen with"),
    BEDROOM("Camera da Letto", "a modern bedroom with"),
    BATHROOM("Bagno", "a modern bathroom with"),
    GARDEN("Giardino", "a beautiful garden with"),
    OFFICE("Studio", "a modern home office with");

    companion object {
        fun fromString(value: String): RoomType {
            return values().find { it.name == value } ?: LIVING_ROOM
        }
    }
}
