package com.thesubgraph.askstack.base.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.R
import com.thesubgraph.askstack.base.theme.Grey500
import com.thesubgraph.askstack.base.theme.Isabelline
import com.thesubgraph.askstack.base.theme.RichBlack
import com.thesubgraph.askstack.base.theme.TextStyle_Size12_Weight400
import com.thesubgraph.askstack.base.theme.TextStyle_Size16_Weight700
import com.thesubgraph.askstack.base.theme.TimberWolf


@Composable
internal fun SearchBar(
    searchQuery: State<String>,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(
        cursorColor = Grey500,
        focusedTextColor = Grey500,
        unfocusedTextColor = Grey500,
        unfocusedBorderColor = Color.Transparent,
        focusedBorderColor = Color.Transparent,
        disabledBorderColor = Color.Transparent,
        errorBorderColor = TimberWolf,
        focusedContainerColor = Isabelline,
        unfocusedContainerColor = Isabelline,
    ),
    placeholderText: String = "Search for questions",
    contentPadding: PaddingValues = PaddingValues(
        start = 16.dp,
        end = 16.dp,
        top = 16.dp,
//        bottom = 30.dp
    ),
    onValueChange: (String) -> Unit = {},
    leadingIcon: @Composable (() -> Unit) = {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = "search",
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(24.dp)
        )
    },
    trailingIcon: @Composable (() -> Unit) = {
        Icon(
            painter = painterResource(R.drawable.ic_circle_close),
            contentDescription = "search",
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(end = 16.dp)
                .size(16.dp)
                .clickable {
                    onValueChange("")
                }
        )
    },
    readOnly: Boolean = false,
    onClick: () -> Unit = {},
    shape: Shape = RoundedCornerShape(8.dp),
    keyboardActions: KeyboardActions = KeyboardActions(onDone = {}),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val modifier = if (readOnly) {
        Modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                onClick()
            }
    } else {
        Modifier
            .fillMaxWidth()
            .padding(contentPadding)
    }
    OutlinedTextField(
        modifier = modifier,
        textStyle = TextStyle_Size16_Weight700,
        value = searchQuery.value,
        onValueChange = onValueChange,
        leadingIcon = leadingIcon,
        placeholder = {
            Text(
                text = placeholderText,
                color = RichBlack,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle_Size12_Weight400.copy(lineHeight = 18.sp),
            )
        },
        keyboardActions = keyboardActions,
        trailingIcon = {
            if (searchQuery.value.isNotEmpty()) {
                trailingIcon()
            }
        },
        singleLine = true,
        colors = colors,
        shape = shape,
        readOnly = readOnly,
        interactionSource = interactionSource
    )
}

@Preview
@Composable
fun SearchBarPreview() {
    SearchBar(
        searchQuery = remember { androidx.compose.runtime.mutableStateOf("") },
        onValueChange = {},
        onClick = {},
        placeholderText = "Search for questions",
    )
}