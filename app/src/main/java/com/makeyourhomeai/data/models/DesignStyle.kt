package com.makeyourhomeai.data.models

enum class DesignStyle(val displayName: String, val description: String) {
    MODERN(
        "Moderno",
        "clean lines, neutral colors, minimalist furniture, contemporary design"
    ),
    CLASSIC(
        "Classico",
        "elegant furniture, warm colors, traditional design, ornate details"
    ),
    MINIMALIST(
        "Minimalista",
        "simple design, white and neutral colors, essential furniture, clean space"
    ),
    RUSTIC(
        "Rustico",
        "natural wood, warm tones, vintage elements, cozy atmosphere"
    ),
    INDUSTRIAL(
        "Industriale",
        "exposed brick, metal elements, concrete, urban style"
    ),
    SCANDINAVIAN(
        "Scandinavo",
        "light wood, white walls, natural light, functional design, hygge atmosphere"
    );

    companion object {
        fun fromString(value: String): DesignStyle {
            return values().find { it.name == value } ?: MODERN
        }
    }
}
