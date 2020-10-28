package com.example.station.ui.common

import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.accessibilityLabel
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.station.R

/**
 *  A composable for train route that displays the given origin and the destination stations.
 *  @param origin Name of train's origin station.
 *  @param destination Name of train's destination station.
 *  @param modifier Modifier.
 *  @param textStyle Text style used for station name texts.
 *  @param fontWeight Font weight used for station names texts.
 */
@Composable fun TrainRoute(
    origin: String,
    destination: String,
    modifier: Modifier = Modifier,
    textStyle: TextStyle = MaterialTheme.typography.subtitle1,
    fontWeight: FontWeight = FontWeight.Bold,
) {
    val fromStation = stringResource(R.string.accessibility_label_from_station, origin)
    val toStation = stringResource(R.string.accessibility_label_to_station, destination)

    ConstraintLayout(modifier) {
        val originRef = createRef()
        val destinationRef = createRef()
        val iconRef = createRef()

        Icon(
            asset = Icons.Rounded.ArrowRightAlt,
            modifier = Modifier.padding(horizontal = 4.dp).constrainAs(iconRef) {
                centerTo(parent)
            }
        )
        Text(
            text = origin,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.End,
            modifier = Modifier
                .semantics { accessibilityLabel = fromStation }
                .constrainAs(originRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = parent.start, end = iconRef.start, bias = 1f)
                    width = Dimension.preferredWrapContent
                }
        )
        Text(
            text = destination,
            style = textStyle,
            fontWeight = fontWeight,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .semantics { accessibilityLabel = toStation }
                .constrainAs(destinationRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = iconRef.end, end = parent.end, bias = 0f)
                    width = Dimension.preferredWrapContent
                }
        )
    }
}
