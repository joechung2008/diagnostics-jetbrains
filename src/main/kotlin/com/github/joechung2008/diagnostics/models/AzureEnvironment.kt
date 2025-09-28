package com.github.joechung2008.diagnostics.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class AzureEnvironment {
    @SerialName("Public")
    Public,
    @SerialName("Fairfax")
    Fairfax,
    @SerialName("Mooncake")
    Mooncake;

    fun getDiagnosticsApiUrl(): String = when (this) {
        Public -> "https://hosting.portal.azure.net/api/diagnostics"
        Fairfax -> "https://hosting.azureportal.usgovcloudapi.net/api/diagnostics"
        Mooncake -> "https://hosting.azureportal.chinacloudapi.cn/api/diagnostics"
    }
}
