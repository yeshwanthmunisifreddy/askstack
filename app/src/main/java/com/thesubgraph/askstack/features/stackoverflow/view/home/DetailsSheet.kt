package com.thesubgraph.askstack.features.stackoverflow.view.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.thesubgraph.askstack.R
import com.thesubgraph.askstack.base.components.HtmlText
import com.thesubgraph.askstack.base.theme.Night
import com.thesubgraph.askstack.base.theme.RoyalBlue
import com.thesubgraph.askstack.base.theme.TextStyle_Size14_Weight700
import com.thesubgraph.askstack.base.theme.TextStyle_Size16_Weight600
import com.thesubgraph.askstack.features.stackoverflow.domain.model.Question
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetContent(
    showBottomSheet: MutableState<Boolean>,
    selectedQuestion: State<Question?>
) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    if (showBottomSheet.value && selectedQuestion.value != null) {
        ModalBottomSheet(
            modifier = Modifier.systemBarsPadding(),
            sheetState = sheetState,
            onDismissRequest = { showBottomSheet.value = false },
            containerColor = Color.White
        ) {
            Column {
                Box(modifier = Modifier.fillMaxWidth()) {
                    BackIcon(onBackPressed = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                            }
                        }
                    })
                    Text(
                        text = "Question",
                        color = Color.Black,
                        style = TextStyle_Size14_Weight700,
                        modifier = Modifier.align(alignment = Alignment.Center)
                    )
                }

                LazyColumn(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                ) {
                    item {
                        QuestionDetailsContent(
                            title = selectedQuestion.value?.title.orEmpty(),
                            body = selectedQuestion.value?.body.orEmpty()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionDetailsContent(title: String, body: String) {
    Title(title = title)
    HtmlText(modifier = Modifier.padding(vertical = 10.dp), text = body)
}

@Composable
private fun Title(title: String) {
    Text(text = title, color = Night, style = TextStyle_Size14_Weight700)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TopBar(onBackPressed: () -> Unit) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Question",
                color = Color.Black,
                fontWeight = FontWeight.W600,
                style = TextStyle_Size16_Weight600
            )
        },
        navigationIcon = {
            BackIcon(onBackPressed = onBackPressed)
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = Color.Red,
        ),
    )
}

@Composable
private fun BackIcon(onBackPressed: () -> Unit) {
    IconButton(onClick = onBackPressed) {
        Icon(
            painterResource(R.drawable.ic_chevron_left_arrow),
            contentDescription = "Back",
            tint = RoyalBlue
        )
    }
}
