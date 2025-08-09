package com.thesubgraph.askstack.features.stackoverflow.view.common.components

import android.text.TextUtils
import android.text.util.Linkify
import android.widget.TextView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import com.thesubgraph.askstack.base.components.HtmlText
import com.thesubgraph.askstack.base.theme.Night
import com.thesubgraph.askstack.base.theme.PrimarySolidBlue
import com.thesubgraph.askstack.base.theme.SlateGrey
import com.thesubgraph.askstack.base.theme.TextStyle_Size12_Weight400
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight700
import com.thesubgraph.askstack.base.utils.DateUtils
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Owner
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@Composable
fun QuestionItem(
    modifier: Modifier = Modifier,
    question: Question,
    onClick: () -> Unit
) {
    Column(modifier = modifier.clickable { onClick() }) {
        Title(question.title)
        Details(
            score = question.score,
            answerCount = question.answerCount,
            tags = question.tags,
            question.body
        )
        Link(question.link)
        UserAndCreatedDate(
            userName = question.owner?.displayName ?: "Unknown User",
            createdDate = question.creationDate
        )
    }
}

@Composable
private fun UserAndCreatedDate(userName: String, createdDate: LocalDateTime) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        UserName(userName = userName)
        CreatedDate(createdDate = createdDate)
    }
}

@Composable
private fun RowScope.UserName(userName: String) {
    Text(
        userName, color = SlateGrey, style = TextStyle_Size12_Weight400,
        modifier = Modifier.weight(1f)
    )
}

@Composable
private fun CreatedDate(createdDate: LocalDateTime) {
    Text(
        text = "Created " + DateUtils.formatDateTime(createdDate),
        color = SlateGrey,
        style = TextStyle_Size12_Weight400
    )
}

@Composable
private fun Link(link: String) {
    Text(
        text = "Link : $link",
        color = PrimarySolidBlue,
        style = TextStyle_Size12_Weight400,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}

@Composable
private fun Details(score: Int, answerCount: Int, tags: List<String>, body: String?) {
    Text(
        text = getAnnotatedString(score = score, answerCount = answerCount, tags = tags),
        lineHeight = 16.sp
    )
    body?.let { html ->
        HtmlText(text = html, maxLines = 5)
    }
}

@Composable
private fun Title(title: String) {
    Text(text = title, color = Night, style = TextStyle_Size14_Weight700)
}

@Composable
private fun getAnnotatedString(score: Int, answerCount: Int, tags: List<String>): AnnotatedString =
    buildAnnotatedString {
        val textStyle = SpanStyle(
            fontSize = 10.sp, fontWeight = FontWeight.W400, color = SlateGrey,
        )
        val separatorStyle = SpanStyle(
            fontWeight = FontWeight.W700, color = SlateGrey, baselineShift = BaselineShift(-0.2f)
        )

        withStyle(textStyle) {
            append("$score votes")
        }
        withStyle(separatorStyle) {
            append(" • ")
        }
        withStyle(textStyle) {
            append("$answerCount answers")
        }
        withStyle(separatorStyle) {
            append(" • ")
        }
        withStyle(textStyle) {
            append(tags.joinToString(", "))
        }
    }

@OptIn(ExperimentalTime::class)
@Preview
@Composable
fun QuestionItemPreview() {
    val localDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    QuestionItem(
        question = Question(
            id = 1,
            title = "Sample Question Title",
            body = "This is a sample question body with some <b>HTML</b> content.",
            creationDate = localDateTime,
            lastActivityDate = localDateTime,
            lastEditDate = localDateTime,
            score = 10,
            answerCount = 2,
            viewCount = 100,
            link = "https://example.com/question/1",
            isAnswered = true,
            tags = listOf("Kotlin", "Compose"),
            owner = Owner(
                id = 1,
                accountId = 0L,
                displayName = "John Doe",
                reputation = 1000,
                userType = "registered",
                profileImage = "https://example.com/profile.jpg",
                link = "https://example.com/user/1"
            )
        ), onClick = {})
}
