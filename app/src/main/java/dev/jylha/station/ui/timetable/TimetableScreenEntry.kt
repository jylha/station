package dev.jylha.station.ui.timetable

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.AmbientContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.DelayCause
import dev.jylha.station.model.Station
import dev.jylha.station.model.Stop
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.delayCauses
import dev.jylha.station.model.departure
import dev.jylha.station.model.isDeparted
import dev.jylha.station.model.isLongDistanceTrain
import dev.jylha.station.model.isNotDeparted
import dev.jylha.station.model.isNotReached
import dev.jylha.station.model.isReached
import dev.jylha.station.model.stopsAt
import dev.jylha.station.model.track
import dev.jylha.station.ui.common.ActualTime
import dev.jylha.station.ui.common.CancelledTime
import dev.jylha.station.ui.common.CauseCategoriesProvider
import dev.jylha.station.ui.common.EstimatedTime
import dev.jylha.station.ui.common.ScheduledTime
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.TrainRoute
import dev.jylha.station.ui.common.causeName
import dev.jylha.station.ui.common.heightFraction
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.util.insertSpaces
import java.time.ZonedDateTime
import java.util.Locale

/**
 * Expandable states for a timetable entry. The [Initial] state is otherwise the same as the
 * [Collapsed] state, but it is used as an initial state to avoid animating transitions when
 * a new train is set to the timetable entry.
 */
private enum class ExpandableState { Initial, Expanded, Collapsed }

private fun Transition.Segment<ExpandableState>.collapsing(): Boolean =
    initialState == ExpandableState.Expanded && targetState == ExpandableState.Collapsed

private fun Transition.Segment<ExpandableState>.expanding(): Boolean =
    (initialState == ExpandableState.Initial || initialState == ExpandableState.Collapsed) &&
            targetState == ExpandableState.Expanded

/**
 * Timetable entry.
 * @param train The train information for the timetable entry.
 * @param stop A specific [Stop] shown in the entry.
 * @param onSelect A callback that is called when the entry is selected.
 * @param modifier An optional modifier for the entry.
 */
@Composable fun TimetableEntry(
    train: Train,
    stop: Stop,
    onSelect: (Train) -> Unit,
    modifier: Modifier = Modifier
) {
    val delayCauses = remember(train) { train.delayCauses() }
    var expandableState by savedInstanceState(train.number) { ExpandableState.Initial }
    val transition = updateTransition(expandableState)
    val delayCausesShown = remember(transition.isRunning, transition.currentState) {
        transition.isRunning || transition.currentState == ExpandableState.Expanded
    }

    val buttonAlpha by transition.animateFloat(
        label = "Button alpha",
        transitionSpec = {
            when {
                expanding() -> tween(300)
                collapsing() -> tween(300, 200)
                else -> snap()
            }
        }
    ) { state -> if (state == ExpandableState.Expanded) 0f else 1f }

    val contentAlpha by transition.animateFloat(
        label = "Content alpha",
        transitionSpec = {
            when {
                expanding() -> tween(200, 100)
                collapsing() -> tween(200)
                else -> snap()
            }
        }
    ) { state -> if (state == ExpandableState.Expanded) 0.8f else 0f }

    val contentHeightFraction by transition.animateFloat(
        label = "Content height fraction",
        transitionSpec = { if (expanding() || collapsing()) tween(300) else snap() }
    ) { state -> if (state == ExpandableState.Expanded) 1f else 0f }

    TimetableEntryBubble(onClick = { onSelect(train) }, modifier, statusColor(train, stop)) {
        Column {
            ConstraintLayout(Modifier.fillMaxWidth()) {
                val identificationRef = createRef()
                val routeRef = createRef()
                val showDelayRef = createRef()
                val arrivalRef = createRef()
                val departureRef = createRef()
                val trackRef = createRef()
                val identifierGuideline = createGuidelineFromStart(20.dp)

                TrainIdentification(train, Modifier.constrainAs(identificationRef) {
                    linkTo(top = parent.top, bottom = parent.bottom)
                    centerAround(identifierGuideline)
                })
                TrainRoute(
                    stationName(train.origin()) ?: "",
                    stationName(train.destination()) ?: "",
                    textStyle = MaterialTheme.typography.body2,
                    modifier = Modifier.constrainAs(routeRef) {
                        linkTo(parent.start, parent.end, startMargin = 40.dp, endMargin = 40.dp)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                )
                Arrival(stop.arrival, Modifier.constrainAs(arrivalRef) {
                    start.linkTo(identificationRef.end, margin = 8.dp)
                    end.linkTo(trackRef.start, margin = 8.dp)
                    top.linkTo(trackRef.top)
                    bottom.linkTo(trackRef.bottom)
                })
                TrainTrack(stop.track(), Modifier.constrainAs(trackRef) {
                    centerHorizontallyTo(parent)
                    top.linkTo(routeRef.bottom, margin = 4.dp)
                    bottom.linkTo(parent.bottom)
                })
                Departure(stop.departure, Modifier.constrainAs(departureRef) {
                    start.linkTo(trackRef.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 48.dp)
                    top.linkTo(trackRef.top)
                    bottom.linkTo(trackRef.bottom)
                }, includeTrackLabel = stop.arrival == null)
                if (delayCauses.isNotEmpty()) {
                    ShowDelayCauseAction(
                        onClick = { expandableState = ExpandableState.Expanded },
                        enabled = expandableState != ExpandableState.Expanded,
                        Modifier.constrainAs(showDelayRef) {
                            end.linkTo(parent.end)
                            top.linkTo(departureRef.top)
                            bottom.linkTo(departureRef.bottom)
                        },
                        color = if (delayCauses.isEmpty()) Color.Transparent
                        else StationTheme.colors.late.copy(
                            alpha = buttonAlpha
                        )
                    )
                }
            }
            if (delayCauses.isNotEmpty() && delayCausesShown) {
                DelayCauses(
                    delayCauses,
                    onClose = { expandableState = ExpandableState.Collapsed },
                    alpha = contentAlpha,
                    Modifier.heightFraction(contentHeightFraction)
                )
            }
        }
    }
}

@Composable private fun TrainIdentification(train: Train, modifier: Modifier = Modifier) {
    val label = trainIdentificationAccessibilityLabel(train)
    val accessibilityModifier = modifier.semantics { contentDescription = label }
    train.run {
        if (isLongDistanceTrain() || commuterLineId.isNullOrBlank()) {
            TrainTypeAndNumber(train.type, train.number, accessibilityModifier)
        } else {
            CommuterLineId(commuterLineId, accessibilityModifier)
        }
    }
}

@Composable private fun TrainTypeAndNumber(
    type: String,
    number: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            type,
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
        Text(
            number.toString(),
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable private fun CommuterLineId(lineId: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(36.dp)
            .background(color = MaterialTheme.colors.primary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            lineId,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Creates accessibility label for train identification.
 * Note: When the label includes train's type, spaces are inserted between the letters to make
 * the accessibility system read out each letter instead of interpreting the type as a word.
 */
@Composable private fun trainIdentificationAccessibilityLabel(train: Train): String {
    return train.run {
        if (isLongDistanceTrain()) {
            when (type) {
                "IC" -> stringResource(R.string.accessibility_label_intercity_train, number)
                "S" -> stringResource(R.string.accessibility_label_pendolino_train, number)
                else -> stringResource(
                    R.string.accessibility_label_long_distance_train,
                    type.insertSpaces(), number
                )
            }
        } else {
            if (commuterLineId.isNullOrBlank()) {
                stringResource(
                    R.string.accessibility_label_commuter_train,
                    type.insertSpaces(), number
                )
            } else {
                stringResource(R.string.accessibility_label_commuter_line, commuterLineId)
            }
        }
    }
}

@Composable private fun TrainTrack(track: String?, modifier: Modifier = Modifier) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (track?.isNotBlank() == true) {
            TrackLabel()
            Text(track, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable private fun TrackLabel() {
    val context = AmbientContext.current
    val label = remember {
        context.getString(R.string.label_track).toUpperCase(Locale.getDefault())
    }
    Text(
        text = label,
        style = MaterialTheme.typography.caption,
        color = Color.Gray
    )
}

@Composable private fun Arrival(arrival: TimetableRow?, modifier: Modifier = Modifier) {
    arrival?.run {
        when {
            cancelled -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = { CancelledTime(type = TimetableRow.Type.Arrival) },
                modifier
            )
            actualTime != null -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrived)) },
                time = {
                    ActualTime(
                        actualTime, differenceInMinutes, TimetableRow.Type.Arrival, track = track
                    )
                },
                modifier
            )
            estimatedTime != null && differenceInMinutes != 0 -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = {
                    EstimatedTime(
                        scheduledTime, estimatedTime, TimetableRow.Type.Arrival, track = track
                    )
                },
                modifier
            )
            else -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = { ScheduledTime(scheduledTime, TimetableRow.Type.Arrival, track = track) },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable private fun Departure(
    departure: TimetableRow?, modifier: Modifier = Modifier,
    includeTrackLabel: Boolean = false
) {
    departure?.run {
        val trackLabel = if (includeTrackLabel) track else null
        when {
            cancelled -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = { CancelledTime(type = TimetableRow.Type.Departure) },
                modifier
            )
            actualTime != null -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departed)) },
                time = {
                    ActualTime(
                        actualTime, differenceInMinutes, TimetableRow.Type.Departure,
                        track = trackLabel
                    )
                },
                modifier
            )
            estimatedTime != null && differenceInMinutes != 0 -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = {
                    EstimatedTime(
                        scheduledTime, estimatedTime, TimetableRow.Type.Departure,
                        track = trackLabel
                    )
                },
                modifier
            )
            else -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = {
                    ScheduledTime(
                        scheduledTime, TimetableRow.Type.Departure, track = trackLabel
                    )
                },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable private fun TimeLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label.toUpperCase(Locale.getDefault()),
        modifier = modifier,
        style = MaterialTheme.typography.caption,
        color = Color.Gray
    )
}

@Composable private fun LabeledTimeField(
    label: @Composable () -> Unit,
    time: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        label()
        time()
    }
}

@Composable private fun ShowDelayCauseAction(
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    color: Color
) {
    Box(
        modifier,
        contentAlignment = Alignment.CenterEnd
    ) {
        val description = stringResource(R.string.accessibility_label_show_delay_causes)
        IconButton(onClick, Modifier.size(48.dp), enabled = enabled) {
            Icon(Icons.Outlined.Info, contentDescription = description, tint = color)
        }
    }
}

@Composable private fun DelayCauses(
    delayCauses: List<DelayCause>,
    onClose: () -> Unit,
    alpha: Float,
    modifier: Modifier = Modifier
) {
    val delayCauseNames = delayCauses.map { cause -> causeName(cause) }.distinct()
    val contentColor = MaterialTheme.colors.onSurface.copy(alpha = alpha)

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Divider(color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f * alpha))
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f)) {
                val delayCausesLabel = stringResource(R.string.label_delay_causes)
                Text(
                    delayCausesLabel.toUpperCase(Locale.getDefault()),
                    Modifier.semantics { contentDescription = delayCausesLabel },
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f * alpha),
                    style = MaterialTheme.typography.caption
                )
                delayCauseNames.forEach { causeName -> DelayCauseName(causeName, contentColor) }
            }
            HideDelayCauseAction(
                onClick = onClose,
                color = contentColor,
                modifier = Modifier.wrapContentWidth(align = Alignment.End)
            )
        }
    }
}

@Composable private fun DelayCauseName(
    causeName: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Text(
        causeName,
        modifier
            .padding(top = 8.dp)
            .semantics { contentDescription = causeName },
        color = color
    )
}

@Composable private fun HideDelayCauseAction(
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier,
        contentAlignment = Alignment.Center
    ) {
        val description = stringResource(R.string.accessibility_label_hide_delay_causes)
        IconButton(onClick, Modifier.size(48.dp)) {
            Icon(Icons.Rounded.ExpandLess, contentDescription = description, tint = color)
        }
    }
}

@Composable private fun statusColor(train: Train, stop: Stop): Color? {
    return when {
        train.hasReachedDestination() -> StationTheme.colors.trainReachedDestination
        train.isNotReady() -> StationTheme.colors.trainIsNotReady
        train.isRunning && stop.isNotReached() -> StationTheme.colors.trainOnRouteToStation
        stop.isReached() && stop.isNotDeparted() -> StationTheme.colors.trainOnStation
        stop.isDeparted() -> StationTheme.colors.trainHasDepartedStation
        else -> null
    }
}

@Composable private fun TimetableEntryBubble(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color? = null,
    content: @Composable () -> Unit
) {
    Surface(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = 2.dp,
        shape = RoundedCornerShape(4.dp)
    ) {
        ConstraintLayout {
            val indicatorRef = createRef()
            val contentRef = createRef()
            val contentMargin = 8.dp

            StatusIndicatorStripe(
                Modifier.constrainAs(indicatorRef) {
                    start.linkTo(parent.start)
                    end.linkTo(contentRef.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    width = Dimension.value(8.dp)
                    height = Dimension.fillToConstraints
                },
                color = indicatorColor
            )
            Box(
                Modifier.constrainAs(contentRef) {
                    start.linkTo(indicatorRef.end, contentMargin)
                    top.linkTo(parent.top, contentMargin)
                    bottom.linkTo(parent.bottom, contentMargin)
                    end.linkTo(parent.end, contentMargin)
                    width = Dimension.fillToConstraints
                }
            ) {
                content()
            }
        }
    }
}

@Composable
private fun StatusIndicatorStripe(modifier: Modifier = Modifier, color: Color? = null) {
    Box(
        modifier
            .fillMaxSize()
            .background(color ?: Color.Transparent)
    )
}

@Preview(name = "TimetableEntry - Dark", group = "TimetableEntry")
@Composable private fun PreviewDarkTimetableEntry() {
    StationTheme(darkTheme = true) {
        PreviewTimetableEntry()
    }
}

@Preview(name = "TimetableEntry - Light", group = "TimetableEntry")
@Composable private fun PreviewLightTimetableEntry() {
    StationTheme(darkTheme = false) {
        PreviewTimetableEntry()
    }
}

@Composable private fun PreviewTimetableEntry() {
    val origin = Station(
        true, Station.Type.Station, "Here", "H",
        123, "FI", 100.0, 50.0
    )
    val somewhere = Station(
        true, Station.Type.StoppingPoint, "Somewhere", "S",
        555, "FI", 50.0, 100.0
    )
    val destination = Station(
        true, Station.Type.StoppingPoint, "There", "H",
        456, "FI", 50.0, 100.0
    )
    val train = Train(
        1, "IC", Train.Category.LongDistance, timetable = listOf(
            departure(123, "1", ZonedDateTime.now()),
            arrival(
                555, "3", ZonedDateTime.now().plusMinutes(60),
                actualTime = ZonedDateTime.now().plusMinutes(64),
                differenceInMinutes = 4, causes = listOf(DelayCause(1))
            ),
            departure(555, "3", ZonedDateTime.now().plusHours(1)),
            arrival(456, "2", ZonedDateTime.now().plusHours(2))
        )
    )
    val stop = train.stopsAt(555).first()

    CauseCategoriesProvider(causeCategories = null) {
        StationNameProvider(LocalizedStationNames.from(listOf(origin, somewhere, destination))) {
            TimetableEntry(train, stop, {})
        }
    }
}
