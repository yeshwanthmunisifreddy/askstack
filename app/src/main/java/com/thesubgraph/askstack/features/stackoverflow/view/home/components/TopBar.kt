package com.thesubgraph.askstack.features.stackoverflow.view.home.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.thesubgraph.askstack.base.theme.TextStyle_Size16_Weight600

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "AskStack",
                color = Color.Black,
                fontWeight = FontWeight.W600,
                style = TextStyle_Size16_Weight600
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.White,
        ),
    )
}
