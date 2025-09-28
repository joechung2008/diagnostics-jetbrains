package com.github.joechung2008.diagnostics.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Diagnostics(
    @SerialName("extensions")
    val extensions: Map<String, ExtensionBase> = emptyMap()
)
