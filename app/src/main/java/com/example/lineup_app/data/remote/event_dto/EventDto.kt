package com.example.lineup_app.data.remote.event_dto

import com.example.lineup_app.data.model.Event
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class EventDto(

    @SerializedName("_embedded")
    val embedded: EmbeddedEventData? = null, // Embedded data containing venues and attractions

    @SerializedName("id")
    val id: String? = null, // Unique identifier for the event

    @SerializedName("name")
    val name: String? = null, // Name of the event

    @SerializedName("location")
    val location: LocationData? = null, // Latitude and longitude

    @SerializedName("url")
    val url: String? = null, // URL for more information about the event

    @SerializedName("description")
    val description: String? = null, // Description of the event

    @SerializedName("additionalInfo")
    val additionalInfo: String? = null, // Additional information about the event

    @SerializedName("dates")
    val dates: DateData? = null, // Date and time information for the event

    @SerializedName("images")
    val images: List<EventImage>? = null, // List of image URLs for the event

    @SerializedName("info")
    val info: String? = null, // Information about the event

    @SerializedName("pleaseNote")
    val pleaseNote: String? = null, // Information to be displayed to the user

    @SerializedName("priceRanges")
    val priceRanges: List<PriceRange>? = null, // List of price ranges for the event

    @SerializedName("classifications")
    val classifications: List<Classification>? = null, // List of classifications for the event

    @SerializedName("place")
    val place: Place? = null, // Place information for the event, ONLY USE IF NO VENUES ARE PROVIDED
)


fun EventDto.toEvent(): Event {
    return Event(
        embedded = embedded,
        id = id,
        name = name,
        location = location,
        url = url,
        description = description,
        additionalInfo = additionalInfo,
        dates = dates,
        images = images,
        info = info,
        pleaseNote = pleaseNote,
        priceRanges = priceRanges,
        classifications = classifications,
        place = place,
    )
}


@Serializable
data class EmbeddedEventData(
    @SerializedName("venues") val venues: List<Venue>? = null,
    @SerializedName("attractions") val attractions: List<Attraction>? = null,
)

@Serializable
data class LocationData(
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null
)

@Serializable
data class PriceRange(
    @SerializedName("min") val min: Double? = null,
    @SerializedName("max") val max: Double? = null,
    @SerializedName("currency") val currency: String? = null
)
