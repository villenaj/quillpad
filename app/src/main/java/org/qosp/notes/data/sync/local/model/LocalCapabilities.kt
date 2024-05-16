package org.qosp.notes.data.sync.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LocalCapabilities(
    @SerialName("api_version")
    val apiVersion: List<String>,
    val version: String,
)
