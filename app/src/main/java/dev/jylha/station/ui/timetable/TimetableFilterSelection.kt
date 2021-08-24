package dev.jylha.station.ui.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Train
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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


@Composable
fun TimetableFilterSelection(
    timetableTypes: Set<TimetableRow.Type>,
    onTimetableTypesChanged: (Set<TimetableRow.Type>) -> Unit,
    categories: Set<Category>,
    onCategoriesChanged: (Set<Category>) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier.fillMaxWidth(), elevation = 2.dp) {
        Column(Modifier.padding(8.dp)) {
            TimetableTypeSelection(timetableTypes, onTimetableTypesChanged)
            Spacer(modifier = Modifier.height(8.dp))
            CategorySelection(categories, onCategoriesChanged)
        }
    }
}

@Composable
private fun TimetableTypeSelection(
    timetableTypes: Set<TimetableRow.Type>,
    onTimetableTypesChanged: (Set<TimetableRow.Type>) -> Unit,
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
        onTimetableTypesChanged(updatedTypes)
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
            Modifier.weight(1f).semantics { contentDescription = arrivingLabel }
        ) {
            Icon(
                painterResource(R.drawable.ic_arrival),
                contentDescription = null,
                Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.timetable_type_arriving))
        }
        Spacer(modifier = Modifier.width(8.dp))
        SelectionButton(
            onClick = { timetableTypeSelected(TimetableRow.Type.Departure) },
            selected = timetableTypes.contains(TimetableRow.Type.Departure),
            Modifier.weight(1f).semantics { contentDescription = departingLabel }
        ) {
            Text(stringResource(R.string.timetable_type_departing))
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painterResource(R.drawable.ic_departure),
                contentDescription = null,
                Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun CategorySelection(
    categories: Set<Category>,
    onCategoriesChanged: (Set<Category>) -> Unit,
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
        onCategoriesChanged(updatedCategories)
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
            Icon(image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_long_distance_trains))
        }
        Spacer(Modifier.width(8.dp))
        SelectionButton(
            onClick = { categorySelected(Category.Commuter) },
            selected = categories.contains(Category.Commuter),
            Modifier.weight(1f).semantics { contentDescription = commuterLabel }
        ) {
            Icon(image, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(stringResource(R.string.category_commuter_trains))
        }
    }
}

@Composable private fun SelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (MaterialTheme.colors.isLight) {
        LightSelectionButton(onClick, selected, modifier, content)
    } else {
        DarkSelectionButton(onClick, selected, modifier, content)
    }
}

@Composable private fun LightSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Button(
        onClick,
        modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = if (selected) MaterialTheme.colors.primaryVariant else Color.Gray,
            contentColor = MaterialTheme.colors.onPrimary
        )
    ) { content() }
}

@Composable private fun DarkSelectionButton(
    onClick: () -> Unit,
    selected: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val color = if (selected) MaterialTheme.colors.primaryVariant.copy(alpha = 0.9f)
    else Color.Gray.copy(alpha = 0.7f)

    OutlinedButton(
        onClick,
        modifier,
        border = BorderStroke(2.dp, color),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Transparent,
            contentColor = color,
        ),
    ) { content() }
}

@Preview(name = "TimetableFilterSelection - light", "Filter selection")
@Composable
private fun PreviewLightFilterSelection() {
    StationTheme(darkTheme = false) {
        TimetableFilterSelection(
            setOf(TimetableRow.Type.Arrival), {},
            setOf(Category.LongDistance), {}
        )
    }
}

@Preview(name = "TimetableFilterSelection - dark", "Filter selection")
@Composable
private fun PreviewDarkFilterSelection() {
    StationTheme(darkTheme = true) {
        TimetableFilterSelection(
            setOf(TimetableRow.Type.Arrival), {},
            setOf(Category.LongDistance), {})
    }
}
