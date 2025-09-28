package com.github.joechung2008.diagnostics.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.serializer

@Serializable(with = ExtensionBaseSerializer::class)
sealed class ExtensionBase

object ExtensionBaseSerializer : KSerializer<ExtensionBase> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ExtensionBase") {
        element<String>("extensionName", isOptional = true)
        element<String>("lastError", isOptional = true)
    }

    override fun deserialize(decoder: Decoder): ExtensionBase {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement()
        if (element !is JsonObject) error("Expected JsonObject for ExtensionBase")
        return when {
            "extensionName" in element -> decoder.json.decodeFromJsonElement<ExtensionInfo>(element)
            "lastError" in element -> decoder.json.decodeFromJsonElement<ExtensionError>(element)
            else -> error("Cannot determine ExtensionBase subtype: missing 'extensionName' or 'lastError'")
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: ExtensionBase) {
        when (value) {
            is ExtensionInfo -> encoder.encodeSerializableValue(serializer<ExtensionInfo>(), value)
            is ExtensionError -> encoder.encodeSerializableValue(serializer<ExtensionError>(), value)
        }
    }
}
