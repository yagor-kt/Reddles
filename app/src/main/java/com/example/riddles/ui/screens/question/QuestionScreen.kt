package com.example.riddles.ui.screens.question

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.riddles.R
import com.example.riddles.ui.theme.CorrectGreen
import com.example.riddles.ui.theme.WrongRed
import kotlinx.coroutines.flow.collect

@Composable
fun QuestionScreen(
    level: String,
    viewModel: QuestionViewModel,
    onRoundFinished: (RoundResult) -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.results.collect { result ->
            onRoundFinished(result)
        }
    }

    val levelTitle = when (level) {
        "EASY" -> stringResource(R.string.level_easy_title)
        "MEDIUM" -> stringResource(R.string.level_medium_title)
        else -> stringResource(R.string.level_hard_title)
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = levelTitle,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.question_score, state.score),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(28.dp))

            when {
                state.isLoading -> CircularProgressIndicator()

                state.isEmpty -> Text(
                    text = stringResource(R.string.question_empty),
                    textAlign = TextAlign.Center
                )

                else -> {
                    Text(
                        text = state.currentRiddle?.question.orEmpty(),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    state.shuffledOptions.forEach { option ->
                        val buttonColor = when {
                            !state.answered -> MaterialTheme.colorScheme.primary
                            option == state.correctAnswer -> CorrectGreen
                            option == state.selectedAnswer -> WrongRed
                            else -> MaterialTheme.colorScheme.primary
                        }

                        OutlinedButton(
                            onClick = { viewModel.selectAnswer(option) },
                            enabled = !state.answered,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            border = BorderStroke(1.dp, buttonColor)
                        ) {
                            Text(
                                text = option,
                                color = buttonColor
                            )
                        }
                    }
                }
            }
        }
    }
}