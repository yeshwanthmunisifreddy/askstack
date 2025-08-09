package com.thesubgraph.askstack.base.components

import android.text.TextUtils
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.thesubgraph.askstack.base.theme.PrimarySolidBlue

@Composable
fun HtmlText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = Int.MAX_VALUE,
    color: Color = PrimarySolidBlue
) {
    val spannedText by remember { mutableStateOf(HtmlCompat.fromHtml(text, 0)) }
    AndroidView(
        modifier = modifier,
        factory = {
            TextView(it).apply {
                autoLinkMask = Linkify.WEB_URLS
                linksClickable = true
                setLinkTextColor(color.toArgb())
                ellipsize = TextUtils.TruncateAt.END
            }
        },
        update = {
            it.maxLines = maxLines
            it.text = spannedText
        }
    )
}