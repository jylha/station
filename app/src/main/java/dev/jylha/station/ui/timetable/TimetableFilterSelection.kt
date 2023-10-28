package dev.jylha.station.ui.timetable

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.model.TimetableRow
import dev.jylha.station.model.Train.Category
import dev.jylha.station.ui.theme.StationTheme

/**
 * Timetable filters selection displays selection buttons for selecting the timetable type
 * and train categories. The states are given with [timetableTypes] and [trainCategories] parameters
 * and the filter selection component notifies of changes by calling the [onTimetableTypesChanged]
 * and [onTrainCategoriesChanged] functions respectively.
 */
@Composable
fun TimetableFilterSelection(
    timetableTypes: TimetableTypes,
    onTimetableTypesChanged: (TimetableTypes) -> Unit,
    trainCategories: TrainCategories,
    onTrainCategoriesChanged: (TrainCategories) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shadowElevation = 4.dp
    ) {
        BoxWithConstraints(modifier.padding(8.dp)) {
            if (maxWidth > 700.dp) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimetableTypeSelection(
                        timetableTypes,
                        onTimetableTypesChanged,
                        Modifier.weight(1f)
                    )
                    CategorySelection(
                        trainCategories,
                        onTrainCategoriesChanged,
                        Modifier.weight(1f)
                    )
                }
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TimetableTypeSelection(timetableTypes, onTimetableTypesChanged)
                    CategorySelection(trainCategories, onTrainCategoriesChanged)
                }
            }
        }
    }
}

private val SelectionButtonContentPadding = 10.dp
private val SelectionButtonIconSize = 24.dp
private val SelectionButtonHeight = SelectionButtonContentPadding * 2 + SelectionButtonIconSize

@Composable
private fun TimetableTypeSelection(
    timetableTypes: TimetableTypes,
    onTimetableTypesChanged: (TimetableTypes) -> Unit,
    modifier: Modifier = Modifier
) {
    val timetableTypeSelected: (TimetableRow.Type) -> Unit = { type ->
        val updatedTypes =
            if (timetableTypes.contains(type)) {
                if (type == TimetableRow.Type.Arrival) {
                    setOf(TimetableRow.Type.Departure)
                } else {
                    setOf(TimetableRow.Type.Arrival)
                }
            } else {
                timetableTypes + type
            }
        onTimetableTypesChanged(TimetableTypes(updatedTypes))
    }

    val arrivingLabel = if (timetableTypes.contains(TimetableRow.Type.Arrival))
        stringResource(R.string.accessibility_label_hide_arriving_trains)
    else
        stringResource(R.string.accessibility_label_show_arriving_trains)

    val departingLabel = if (timetableTypes.contains(TimetableRow.Type.Departure))
        stringResource(R.string.accessibility_label_hide_departing_trains)
    else
        stringResource(R.string.accessibility_label_show_departing_trains)

    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Arrival) },
            selected = timetableTypes.contains(TimetableRow.Type.Arrival),
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = arrivingLabel }
        ) {
            Icon(
                painterResource(R.drawable.ic_arrival),
                contentDescription = null,
                Modifier.size(SelectionButtonIconSize)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.timetable_type_arriving), maxLines = 1)
        }
        Spacer(modifier = Modifier.width(8.dp))
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Departure) },
            selected = timetableTypes.contains(TimetableRow.Type.Departure),
            modifier = Modifier
                .weight(1f)
                .semantics { contentDescription = departingLabel }
        ) {
            Text(stringResource(R.string.timetable_type_departing), maxLines = 1)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painterResource(R.drawable.ic_departure),
                contentDescription = null,
                Modifier.size(SelectionButtonIconSize)
            )
        }
    }
}

@Composable
private fun CategorySelection(
    categories: TrainCategories,
    onCategoriesChanged: (TrainCategories) -> Unit,
    modifier: Modifier = Modifier
) {
    val categorySelected: (Category) -> Unit = { category ->
        val updatedCategories =
            if (categories.contains(category)) {
                if (category == Category.LongDistance) {
                    setOf(Category.Commuter)
                } else {
                    setOf(Category.LongDistance)
                }
            } else {
                categories + category
            }
        onCategoriesChanged(TrainCategories(updatedCategories))
    }

    val image = remember { Icons.Rounded.Train }
    val longDistanceLabel = if (categories.contains(Category.LongDistance))
        stringResource(R.string.accessibility_label_hide_long_distance_trains)
    else
        stringResource(R.string.accessibility_label_show_long_distance_trains)

    val commuterLabel = if (categories.contains(Category.Commuter))
        stringResource(R.string.accessibility_label_hide_commuter_trains)
    else
        stringResource(R.string.accessibility_label_show_commuter_trains)

    Row(modifier.fillMaxWidth()) {
        SelectionButton(
            onClick = { categorySelected(Category.LongDistance) },
            selected = categories.contains(Category.LongDistance),
            Modifier.weight(1f).semantics { contentDescription = longDistanceLabel }
        ) {
            Icon(image, contentDescription = null, Modifier.size(SelectionButtonIconSize))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_long_distance_trains), maxLines = 1)
        }
        Spacer(Modifier.width(8.dp))
        SelectionButton(
            onClick = { categorySelected(Category.Commuter) },
            selected = categories.contains(Category.Commuter),
            Modifier.weight(1f).semantics { contentDescription = commuterLabel }
        ) {
            Icon(image, contentDescription = null, Modifier.size(SelectionButtonIconSize))
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_commuter_trains), maxLines = 1)
        }
    }
}

@Composable
private fun SelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val selectionButtonModifier = modifier
        .sizeIn(minHeight = SelectionButtonHeight, maxHeight = SelectionButtonHeight)
    if (isSystemInDarkTheme()) {
       DarkSelectionButton(onClick, selected, selectionButtonModifier, content)
    } else {
        LightSelectionButton(onClick, selected, selectionButtonModifier, content)
    }
}

@Composable
private fun LightSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val (containerColor, contentColor) = with(MaterialTheme.colorScheme) {
        if (selected) secondary to onSecondary
        else secondary.copy(alpha = 0.5f) to onSecondary.copy(alpha = 0.8f)
    }

    Button(
        onClick,
        modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        contentPadding = PaddingValues(SelectionButtonContentPadding),
        content = content
    )
}

@Composable
private fun DarkSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    val (containerColor, contentColor) = with(MaterialTheme.colorScheme) {
        if (selected) secondary to onSecondary
        else surfaceVariant to outline
    }
    val outlineColor = MaterialTheme.colorScheme.outline

    OutlinedButton(
        onClick,
        modifier,
        border = BorderStroke(2.dp, outlineColor),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        contentPadding = PaddingValues(SelectionButtonContentPadding),
        content = content
    )
}

@Preview(
    name = "TimetableFilterSelection - light",
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "TimetableFilterSelection - dark, width=700",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 700
)
@Preview(
    name = "TimetableFilterSelection - dark, width=720",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    widthDp = 720
)
@Composable
private fun PreviewTimetableFilterSelection() {
    StationTheme {
        TimetableFilterSelection(
            timetableTypes = TimetableTypes(TimetableRow.Type.Arrival),
            onTimetableTypesChanged = {},
            trainCategories = TrainCategories(Category.LongDistance),
            onTrainCategoriesChanged = {},
        )
    }
}
