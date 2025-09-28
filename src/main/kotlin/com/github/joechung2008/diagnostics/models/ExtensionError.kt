package com.github.joechung2008.diagnostics.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ExtensionError")
data class ExtensionError(
    @SerialName("lastError")
    val lastError: ExtensionLastError? = null
) : ExtensionBase()

@Serializable
data class ExtensionLastError(
    @SerialName("errorMessage")
    val errorMessage: String? = null,
    @SerialName("time")
    val time: String? = null
)
