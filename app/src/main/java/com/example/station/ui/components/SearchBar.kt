package com.example.station.ui.components

import androidx.compose.foundation.Icon
import androidx.compose.foundation.Text
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.ui.tooling.preview.Preview
import com.example.station.ui.theme.StationTheme

/**
 * A search bar composable.
 * @param text Current search text.
 * @param onValueChanged A callback that is called whenever search text changes.
 * @param modifier Modifier.
 * @param placeholderText A text that is shown in search field when it is empty.
 * @param onClose A callback that is called when user clicks either the Done button on keyboard
 * or the BackSpace button beside the search field.
 */
@Composable
fun SearchBar(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    onClose: (() -> Unit)? = {}
) {
    var active by remember { mutableStateOf(false) }

    var textColor = if (MaterialTheme.colors.isLight) MaterialTheme.colors.onPrimary
    else MaterialTheme.colors.onBackground

    Surface(
        modifier,
        color = if (MaterialTheme.colors.isLight) {
            MaterialTheme.colors.primary
        } else {
            MaterialTheme.colors.surface
        },
        elevation = 4.dp
    ) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onClose != null) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardBackspace)
                }
            }
            TextField(
                value = text,
                onValueChange = onValueChanged,
                label = { if (!active) Text(placeholderText) },
                placeholder = { Text(placeholderText) },
                onTextInputStarted = { active = true },
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color.Transparent,
                activeColor = textColor,
                inactiveColor = textColor,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Done,
                onImeActionPerformed = { _, kb ->
                    kb?.hideSoftwareKeyboard()
                    onClose?.invoke()
                }
            )
        }
    }
}

@Preview(showBackground = true, name = "Light SearchBar with placeholder text")
@Composable private fun PreviewLightSearchBar() {
    StationTheme {
        SearchBar(text = "", onValueChanged = {}, placeholderText = "Search")
    }
}

@Preview(showBackground = true, name = "Dark SearchBar with search text")
@Composable private fun PreviewDarkSearchBar() {
    StationTheme(darkTheme = true) {
        SearchBar(text = "Helsinki", onValueChanged = {})
    }
}
