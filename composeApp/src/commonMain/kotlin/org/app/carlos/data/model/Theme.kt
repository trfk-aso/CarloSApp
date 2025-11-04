package org.app.carlos.data.model

data class Theme(
    val id: String,
    val name: String,
    val isPurchased: Boolean,
    val type: String,
    val previewRes: String,
    val primaryColor: Long,
    val splashText: String,
    val price: Double? = null
)