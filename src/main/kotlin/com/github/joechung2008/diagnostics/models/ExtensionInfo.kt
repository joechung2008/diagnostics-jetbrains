package com.github.joechung2008.diagnostics.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("ExtensionInfo")
data class ExtensionInfo(
    @SerialName("extensionName")
    val extensionName: String? = null,
    @SerialName("manageSdpEnabled")
    val manageSdpEnabled: Boolean = false,
    @SerialName("config")
    val config: Map<String, String>? = null,
    @SerialName("stageDefinition")
    val stageDefinition: Map<String, List<String>>? = null
) : ExtensionBase()
