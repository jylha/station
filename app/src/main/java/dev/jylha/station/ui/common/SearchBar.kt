package dev.jylha.station.ui.common

import android.content.res.Configuration
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.jylha.station.R
import dev.jylha.station.ui.theme.StationTheme

/**
 * A search bar composable.
 * @param text Current search text.
 * @param onValueChanged A callback that is called whenever search text changes.
 * @param modifier Modifier.
 * @param placeholderText A text that is shown in search field when search text is empty.
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
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.primary

    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = modifier,
        color = surfaceColor,
        contentColor = textColor,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onClose != null) {
                val closeSearchLabel = stringResource(R.string.accessibility_label_close_search)
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.KeyboardBackspace, contentDescription = closeSearchLabel)
                }
            }

            TextField(
                value = text,
                onValueChange = onValueChanged,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                placeholder = {
                    Text(
                        placeholderText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        onClose?.invoke()
                    },
                ),
                singleLine = true,
                maxLines = 1,
                colors = TextFieldDefaults.colors(
                    cursorColor = indicatorColor,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = indicatorColor,
                )
            )
        }
    }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
}

@Preview(name = "SearchBar with placeholder text", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "SearchBar with placeholder text", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun PreviewSearchBarWithPlaceholderText() {
    StationTheme {
        SearchBar(text = "", onValueChanged = {}, placeholderText = "Placeholder text")
    }
}

@Preview(name = "SearchBar with search text", uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "SearchBar with search text", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable private fun PreviewSearchBarWithInputText() {
    StationTheme {
        SearchBar(text = "Input text", onValueChanged = {})
    }
}
