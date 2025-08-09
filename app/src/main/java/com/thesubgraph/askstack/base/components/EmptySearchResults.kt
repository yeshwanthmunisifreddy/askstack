package com.thesubgraph.askstack.base.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thesubgraph.askstack.R
import com.thesubgraph.askstack.base.theme.DavysGray200
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight400
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight700

@Composable
fun EmptySearchResults(
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            painter = painterResource(R.drawable.ic_empty_serach),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Text(
            text = "No results found",
            style = TextStyle_Size14_Weight700,
            lineHeight = 18.sp,
            color = DavysGray200,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Try adjusting your search\n" +
                    "to find what you are looking for",
            style = TextStyle_Size14_Weight400,
            color = DavysGray200,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
    }
}

@Preview
@Composable
fun EmptySearchResultsPreview() {
    EmptySearchResults()
}