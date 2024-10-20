package dev.jylha.station.ui.timetable

import android.content.res.Configuration
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import dev.jylha.station.R
import dev.jylha.station.data.stations.LocalizedStationNames
import dev.jylha.station.model.DelayCause
import dev.jylha.station.model.Station
import dev.jylha.station.model.Stop
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train
import dev.jylha.station.model.arrival
import dev.jylha.station.model.departure
import dev.jylha.station.ui.common.ActualTime
import dev.jylha.station.ui.common.CancelledArrival
import dev.jylha.station.ui.common.CancelledDeparture
import dev.jylha.station.ui.common.CauseCategoriesProvider
import dev.jylha.station.ui.common.EstimatedTime
import dev.jylha.station.ui.common.ScheduledTime
import dev.jylha.station.ui.common.StationNameProvider
import dev.jylha.station.ui.common.TrainRoute
import dev.jylha.station.ui.common.causeName
import dev.jylha.station.ui.common.heightFraction
import dev.jylha.station.ui.common.stationName
import dev.jylha.station.ui.preview.PreviewApplicationLocales
import dev.jylha.station.ui.theme.StationTheme
import dev.jylha.station.ui.timetable.ExpandableState.Collapsed
import dev.jylha.station.ui.timetable.ExpandableState.Expanded
import dev.jylha.station.ui.timetable.ExpandableState.Initial
import dev.jylha.station.util.insertSpaces
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Expandable states for a timetable entry. The [Initial] state is otherwise the same as the
 * [Collapsed] state, but it is used as an initial state to avoid animating transitions when
 * a new train is set to the timetable entry.
 */
private enum class ExpandableState { Initial, Expanded, Collapsed }

private fun Transition.Segment<ExpandableState>.collapsing(): Boolean =
    initialState == Expanded && targetState == Collapsed

private fun Transition.Segment<ExpandableState>.expanding(): Boolean =
    (initialState == Initial || initialState == Collapsed) &&
            targetState == ExpandableState.Expanded

/**
 * Timetable entry.
 * @param train The train information for the timetable entry.
 * @param stop A specific [Stop] shown in the entry.
 * @param onSelect A callback that is called when the entry is selected.
 * @param modifier An optional modifier for the entry.
 */
@Composable
fun TimetableEntry(
    train: Train,
    stop: Stop,
    onSelect: (Train) -> Unit,
    modifier: Modifier = Modifier
) {
    val delayCauses = remember(train) { train.delayCauses() }
    var expandableState by rememberSaveable(train.number, stop) { mutableStateOf(Initial) }
    val transition = updateTransition(expandableState, label = "Entry state")
    val delayCausesShown = remember(transition.isRunning, transition.currentState) {
        transition.isRunning || transition.currentState == Expanded
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
    ) { state -> if (state == Expanded) 0f else 1f }

    val contentAlpha by transition.animateFloat(
        label = "Content alpha",
        transitionSpec = {
            when {
                expanding() -> tween(200, 100)
                collapsing() -> tween(200)
                else -> snap()
            }
        }
    ) { state -> if (state == Expanded) 0.8f else 0f }

    val contentHeightFraction by transition.animateFloat(
        label = "Content height fraction",
        transitionSpec = { if (expanding() || collapsing()) tween(300) else snap() }
    ) { state -> if (state == Expanded) 1f else 0f }

    TimetableEntryBubble(onClick = { onSelect(train) }, modifier, statusColor(train, stop)) {
        Column {
            ConstraintLayout(Modifier.requiredHeight(72.dp).fillMaxWidth()) {
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
                    textStyle = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.constrainAs(routeRef) {
                        linkTo(parent.start, parent.end, startMargin = 40.dp, endMargin = 40.dp)
                        top.linkTo(parent.top)
                        width = Dimension.fillToConstraints
                    }
                )
                TrainTrack(stop.track(), Modifier.constrainAs(trackRef) {
                    centerHorizontallyTo(parent)
                    bottom.linkTo(parent.bottom, margin = 2.dp)
                })
                Arrival(stop.arrival, Modifier.constrainAs(arrivalRef) {
                    start.linkTo(identificationRef.end, margin = 8.dp)
                    end.linkTo(trackRef.start, margin = 8.dp)
                    bottom.linkTo(trackRef.bottom)
                })
                Departure(stop.departure, Modifier.constrainAs(departureRef) {
                    start.linkTo(trackRef.end, margin = 8.dp)
                    end.linkTo(parent.end, margin = 48.dp)
                    bottom.linkTo(trackRef.bottom)
                }, includeTrackLabel = stop.arrival == null)
                if (delayCauses.isNotEmpty()) {
                    ShowDelayCauseAction(
                        onClick = { expandableState = Expanded },
                        enabled = expandableState != Expanded,
                        modifier = Modifier
                            .constrainAs(showDelayRef) {
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                            }
                            .alpha(buttonAlpha),
                        color = if (delayCauses.isEmpty()) Color.Transparent
                        else StationTheme.colors.late
                    )
                }
            }
            if (delayCauses.isNotEmpty() && delayCausesShown) {
                DelayCauses(
                    delayCauses,
                    onClose = { expandableState = Collapsed },
                    modifier = Modifier
                        .heightFraction(contentHeightFraction)
                        .alpha(contentAlpha)
                )
            }
        }
    }
}

@Composable
private fun TrainIdentification(train: Train, modifier: Modifier = Modifier) {
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

@Composable
private fun TrainTypeAndNumber(
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
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            number.toString(),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun CommuterLineId(lineId: String, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(36.dp)
            .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            lineId,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Creates accessibility label for train identification.
 * Note: When the label includes train's type, spaces are inserted between the letters to make
 * the accessibility system read out each letter instead of interpreting the type as a word.
 */
@ReadOnlyComposable
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

@Composable
private fun TrainTrack(track: String?, modifier: Modifier = Modifier) {
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

@Composable
private fun TrackLabel() {
    Text(
        text = stringResource(R.string.label_track).uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray
    )
}

@Composable
private fun Arrival(arrival: TimetableRow?, modifier: Modifier = Modifier) {
    arrival?.run {
        when {
            cancelled -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = { CancelledArrival() },
                modifier
            )
            actualTime != null -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrived)) },
                time = {
                    ActualTime(
                        actualTime = actualTime,
                        differenceInMinutes = differenceInMinutes,
                        type = TimetableRow.Type.Arrival,
                        track = track
                    )
                },
                modifier
            )
            estimatedTime != null && differenceInMinutes != 0 -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = {
                    EstimatedTime(
                        scheduledTime = scheduledTime,
                        estimatedTime = estimatedTime,
                        type = TimetableRow.Type.Arrival,
                        track = track
                    )
                },
                modifier
            )
            else -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_arrives)) },
                time = {
                    ScheduledTime(
                        scheduledTime = scheduledTime,
                        type = TimetableRow.Type.Arrival,
                        track = track
                    )
                },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable
private fun Departure(
    departure: TimetableRow?, modifier: Modifier = Modifier,
    includeTrackLabel: Boolean = false
) {
    departure?.run {
        val trackLabel = if (includeTrackLabel) track else null
        when {
            cancelled -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = { CancelledDeparture() },
                modifier
            )
            actualTime != null -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departed)) },
                time = {
                    ActualTime(
                        actualTime = actualTime,
                        differenceInMinutes = differenceInMinutes,
                        type = TimetableRow.Type.Departure,
                        track = trackLabel
                    )
                },
                modifier
            )
            estimatedTime != null && differenceInMinutes != 0 -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = {
                    EstimatedTime(
                        scheduledTime = scheduledTime,
                        estimatedTime = estimatedTime,
                        TimetableRow.Type.Departure,
                        track = trackLabel
                    )
                },
                modifier
            )
            else -> LabeledTimeField(
                label = { TimeLabel(stringResource(R.string.label_departs)) },
                time = {
                    ScheduledTime(
                        scheduledTime = scheduledTime,
                        type = TimetableRow.Type.Departure,
                        track = trackLabel
                    )
                },
                modifier
            )
        }
    } ?: Box(modifier)
}

@Composable
private fun TimeLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label.uppercase(),
        modifier = modifier,
        style = MaterialTheme.typography.labelSmall,
        color = Color.Gray
    )
}

@Composable
private fun LabeledTimeField(
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

@Composable
private fun ShowDelayCauseAction(
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

@Composable
private fun DelayCauses(
    delayCauses: ImmutableList<DelayCause>,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val delayCauseNames = delayCauses.map { cause -> causeName(cause) }.distinct()
    val contentColor = MaterialTheme.colorScheme.onSurface
    val labelColor = contentColor.copy(alpha = 0.7f)
        .compositeOver(MaterialTheme.colorScheme.surface)
    val dividerColor = contentColor.copy(alpha = 0.5f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    Column(
        modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        HorizontalDivider(color = dividerColor)
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Column(Modifier.weight(1f)) {
                val delayCausesLabel = stringResource(R.string.label_delay_causes)
                Text(
                    delayCausesLabel.uppercase(),
                    Modifier.semantics { contentDescription = delayCausesLabel },
                    color = labelColor,
                    style = MaterialTheme.typography.labelSmall,
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

@Composable
private fun DelayCauseName(
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

@Composable
private fun HideDelayCauseAction(
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

@ReadOnlyComposable
@Composable
private fun statusColor(train: Train, stop: Stop): Color? {
    return when {
        train.hasReachedDestination() -> StationTheme.colors.trainReachedDestination
        train.isNotReady() -> StationTheme.colors.trainIsNotReady
        train.isRunning && stop.isNotReached() -> StationTheme.colors.trainOnRouteToStation
        stop.isReached() && stop.isNotDeparted() -> StationTheme.colors.trainOnStation
        stop.isDeparted() -> StationTheme.colors.trainHasDepartedStation
        else -> null
    }
}

@Composable
private fun TimetableEntryBubble(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    indicatorColor: Color? = null,
    indicatorWidth: Dp = 8.dp,
    contentPadding: PaddingValues = PaddingValues(8.dp),
    content: @Composable BoxScope.() -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        shadowElevation = 2.dp,
    ) {
        Box(
            modifier = Modifier
                .drawWithCache {
                    val indicatorSize = Size(indicatorWidth.toPx(), size.height)
                    onDrawBehind {
                        if (indicatorColor != null)
                            drawRect(indicatorColor, size = indicatorSize)
                    }
                }
                .padding(start = indicatorWidth)
                .padding(contentPadding),
            content = content
        )
    }
}


@Preview(name = "Dark", uiMode = Configuration.UI_MODE_NIGHT_YES)
@PreviewApplicationLocales
@Composable
private fun TimetableEntryPreview() {
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
            departure(123, "1", Clock.System.now()),
            arrival(
                555, "3", Clock.System.now().plus(60.minutes),
                actualTime = Clock.System.now().plus(64.minutes),
                differenceInMinutes = 4, causes = listOf(DelayCause(1))
            ),
            departure(555, "3", Clock.System.now().plus(1.hours)),
            arrival(456, "2", Clock.System.now().plus(2.hours))
        )
    )
    val stop = train.stopsAt(555).first()

    StationTheme {
        CauseCategoriesProvider(causeCategories = null) {
            StationNameProvider(
                LocalizedStationNames.from(listOf(origin, somewhere, destination))
            ) {
                TimetableEntry(train, stop, {})
            }
        }
    }
}
