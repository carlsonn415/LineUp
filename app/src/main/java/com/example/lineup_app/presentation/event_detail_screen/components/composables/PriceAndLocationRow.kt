package com.example.lineup_app.presentation.event_detail_screen.components.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.lineup_app.R
import com.example.lineup_app.data.model.Event
import com.example.lineup_app.presentation.ui.theme.MyIcons

@Composable
fun PriceAndLocationRow(
    event: Event,
    distanceFromLocation: String,
    modifier: Modifier = Modifier
) {
    //---------------------------------------------------------------------------------------------- Event price and location row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            .horizontalScroll(rememberScrollState())
    ) {
        //------------------------------------------------------------------------------------------ Check if event has price ranges
        if (
            event.priceRanges != null
            && event.priceRanges.first().min != null
            && event.priceRanges.first().max != null
        ) {
            val startPrice = event.priceRanges.first().min?.toInt() ?: 0
            val endPrice = event.priceRanges.first().max?.toInt() ?: 0

            if (event.priceRanges.first().currency == "USD" || event.priceRanges.first().currency == null) {
                // USD
                Text(
                    text = if (startPrice > 0) "$$startPrice - $$endPrice" else stringResource(R.string.free),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else {
                Text(
                    text = startPrice.toString() + " " +
                            event.priceRanges.first().currency.toString() + " - " +
                            endPrice.toString() + " " +
                            event.priceRanges.first().currency.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            // Divider
            Box(
                modifier = modifier
                    .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    .size(height = 16.dp, width = 1.dp)
                    .background(color = MaterialTheme.colorScheme.onBackground)
            )
        }
        //------------------------------------------------------------------------------------------ Check if valid distance from location was provided
        if (distanceFromLocation.isNotEmpty()) {
            Text(
                text = distanceFromLocation,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
            )
            Box(
                modifier = modifier
                    .padding(horizontal = dimensionResource(R.dimen.padding_small))
                    .size(height = 16.dp, width = 1.dp)
                    .background(color = MaterialTheme.colorScheme.onBackground)
            )
        }
        //------------------------------------------------------------------------------------------ Check if event has location, if not use venue location
        AddressText(event)
    }
}

@Composable
fun AddressText(
    event: Event,
    modifier: Modifier = Modifier,
    clickable: Boolean = false,
) {
    if (
        event.place != null
        && event.place.address?.line1.toString() != "null"
        && event.place.city?.name.toString() != "null"
        && event.place.state?.name.toString() != "null"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            if (!clickable) {
                Icon(
                    imageVector = MyIcons.location,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = event.place.address?.line1 + ", " + event.place.city?.name + ", " + event.place.state,
                style = MaterialTheme.typography.bodyMedium,
                color = if (clickable) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (clickable) TextDecoration.Underline else TextDecoration.None,
                modifier = if (!clickable) Modifier.padding(start = dimensionResource(R.dimen.padding_extra_small)) else Modifier
            )
        }
    } else if (
        event.embedded?.venues != null
        && event.embedded.venues.first().address.toString() != "null"
        && event.embedded.venues.first().city.toString() != "null"
        && event.embedded.venues.first().state.toString() != "null"
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
        ) {
            if (!clickable) {
                Icon(
                    imageVector = MyIcons.location,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
            }
            Text(
                text = event.embedded.venues.first().address?.line1 + ", " + event.embedded.venues.first().city?.name + ", " + event.embedded.venues.first().state?.name,
                style = MaterialTheme.typography.bodyMedium,
                color = if (clickable) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (clickable) TextDecoration.Underline else TextDecoration.None,
                modifier = if (!clickable) Modifier.padding(start = dimensionResource(R.dimen.padding_extra_small)) else Modifier
            )
        }
    } else {
        Text(
            text = "Location not provided",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(start = dimensionResource(R.dimen.padding_extra_small))
        )
    }
}