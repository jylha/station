package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowRightAlt
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.ui.theme.StationTheme

/**
 *  A composable that displays given origin and destination station for a train route.
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
            imageVector = Icons.Rounded.ArrowRightAlt, contentDescription = null,
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
                .semantics { contentDescription = fromStation }
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
                .semantics { contentDescription = toStation }
                .constrainAs(destinationRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    linkTo(start = iconRef.end, end = parent.end, bias = 0f)
                    width = Dimension.preferredWrapContent
                }
        )
    }
}

@Preview(name = "Light theme", showBackground = true)
@Composable
private fun PreviewLightTrainRoute() {
    StationTheme(darkTheme = false) {
        Surface(Modifier.fillMaxWidth()) {
            TrainRoute(origin = "Origin", destination = "Destination")
        }
    }
}

@Preview(name = "Dark theme", showBackground = true)
@Composable
private fun PreviewDarkTrainRoute() {
    StationTheme(darkTheme = true) {
        Surface(Modifier.fillMaxWidth()) {
            TrainRoute(origin = "Origin", destination = "Destination")
        }
    }
}
