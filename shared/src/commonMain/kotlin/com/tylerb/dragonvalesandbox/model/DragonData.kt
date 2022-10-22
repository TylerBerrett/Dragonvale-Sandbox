package com.tylerb.dragonvalesandbox.model

import com.tylerb.dragonvalesandbox.util.jsonIgnore
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*


@Serializable
data class DragonResponse(
    val dragons: List<DragonData>
)

object DragonResponseSerializer : KSerializer<DragonResponse> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Test") {
        element<JsonObject>("dragons")

    }

    override fun deserialize(decoder: Decoder): DragonResponse {
        // Cast to JSON-specific interface
        val jsonInput = decoder as? JsonDecoder ?: error("Can be deserialized only by JSON")
        // Read the whole content as JSON
        val json = jsonInput.decodeJsonElement().jsonObject
        // Extract and remove name property
        val details = json.toMutableMap()

        val dragons = details.values.map {
            jsonIgnore.decodeFromJsonElement<DragonData>(it)
        }

        return DragonResponse(dragons)
    }

    override fun serialize(encoder: Encoder, value: DragonResponse) {
        error("Serialization is not supported")
    }
}


@Serializable
data class DragonData(
    val available: String,
    @SerialName("breed_key")
    val breedKey: String,
    @SerialName("breed_note")
    val breedNote: String? = null,
    @SerialName("egg")
    val eggIcon: String,
    val elements: List<String>? = null,
    val evolved: String? = null,
    val events: String? = null,
    val habitats: List<String>,
    val image: String,
    val latent: List<String>? = null,
    val level: String? = null,
    val name: String,
    val opposite: String? = null,
    val primary: String? = null,
    val rarity: String,
    val reqs: List<List<String>>,
    val rifty: String? = null,
    val sell: String,
    val time: Double,
    val type: String,
    val weight: DragonWeight? = null,
    @SerialName("x-breed_note")
    val xBreedNote: String? = null
)

@Serializable
data class DragonWeight(
    val breed: Double? = null,
    @SerialName("clone_both")
    val cloneBoth: Double? = null,
    @SerialName("clone_both_rift")
    val cloneBothRift: Double? = null,
    @SerialName("clone_both_social")
    val cloneBothSocial: Double? = null,
    @SerialName("clone_single")
    val cloneSingle: Double? = null,
    @SerialName("clone_single_rift")
    val cloneSingleRift: Double? = null,
    @SerialName("clone_single_social")
    val cloneSingleSocial: Double? = null
)