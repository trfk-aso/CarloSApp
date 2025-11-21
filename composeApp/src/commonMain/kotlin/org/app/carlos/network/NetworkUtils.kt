package org.app.carlos.network

import kotlinx.serialization.Serializable

@Serializable
data class InstallResponse(
    val url: String? = null
)