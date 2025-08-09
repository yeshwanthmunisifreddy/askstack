package com.thesubgraph.askstack.base.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.base.theme.PrimarySolidGreen
import com.thesubgraph.askstack.base.theme.TextStyle_Size16_Weight700

@Composable
fun AskStackButton(
    text: String, onRetry: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = PrimarySolidGreen,
        contentColor = Color.White
    )
) {
    Button(
        onClick = onRetry,
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier = Modifier.width(200.dp),
        colors = colors
    ) {
        Text(
            text = text,
            style = TextStyle_Size16_Weight700,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
    }
}