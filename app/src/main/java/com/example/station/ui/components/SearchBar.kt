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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * A search bar composable.
 * @param text Current search text.
 * @param onValueChanged A callback that is called whenever search text changes.
 * @param modifier Modifier.
 * @param placeholderText A text that is shown in search field when it is empty.
 * @param onClose a Callback that is called user clicks either Done button on keyboard
 * or the back button beside the search field..
 */
@Composable
fun SearchBar(
    text: String,
    onValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholderText: String = "",
    onClose: (() -> Unit)?
) {
    var active by remember { mutableStateOf(false) }

    Surface(modifier, color = MaterialTheme.colors.surface, elevation = 4.dp) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalGravity = Alignment.CenterVertically
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
                backgroundColor = MaterialTheme.colors.surface,
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
