package dev.jylha.station.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardBackspace
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
    val (textColor, surfaceColor) = with(MaterialTheme.colors) {
        if (isLight) Pair(onPrimary, primary) else Pair(onSurface, surface)
    }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    Surface(
        modifier,
        color = surfaceColor,
        elevation = 4.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
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
                placeholder = { Text(placeholderText, color = MaterialTheme.colors.onPrimary) },
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
                colors = TextFieldDefaults.textFieldColors(
                    textColor = textColor,
                    backgroundColor = Color.Transparent
                )
            )
        }
    }
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }
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
