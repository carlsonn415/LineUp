package com.example.lineup_app.data.local.entities.event_entities.pojos

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lineup_app.data.local.entities.event_entities.VenueEntity
import com.example.lineup_app.data.local.entities.event_entities.VenueImageEntity
import com.example.lineup_app.data.remote.event_dto.Address
import com.example.lineup_app.data.remote.event_dto.City
import com.example.lineup_app.data.remote.event_dto.GeneralInfo
import com.example.lineup_app.data.remote.event_dto.LocationData
import com.example.lineup_app.data.remote.event_dto.State
import com.example.lineup_app.data.remote.event_dto.Venue

data class VenueWithAllDetails(
    @Embedded val venue: VenueEntity,

    @Relation(
        parentColumn = "venueId",
        entityColumn = "venueId"
    )
    val images: List<VenueImageEntity>
) {
    fun toVenue(): Venue {
        return Venue(
            id = venue.venueId,
            name = venue.name,
            description = venue.description,
            address = Address(venue.address),
            images = images.map { it.toVenueImage() },
            parkingDetail = venue.parkingDetail,
            generalInfo = GeneralInfo(
                generalRule = venue.generalRule,
                childRule = venue.childRule,
            ),
            location = LocationData(
                latitude = venue.location?.lat,
                longitude = venue.location?.lon
            ),
            city = City(venue.city),
            state = State(venue.state),
            url = venue.url,
        )
    }
}