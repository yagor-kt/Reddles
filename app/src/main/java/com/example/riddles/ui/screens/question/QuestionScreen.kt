package com.example.riddles.ui.screens.question

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.riddles.R
import com.example.riddles.ui.theme.CorrectGreen
import com.example.riddles.ui.theme.EasyColor
import com.example.riddles.ui.theme.LightBackground
import com.example.riddles.ui.theme.LightSurface
import com.example.riddles.ui.theme.Primary
import com.example.riddles.ui.theme.TextDark
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

    val timerColor = if (state.timeLeft <= 5) WrongRed else Primary

    Scaffold(
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = LightSurface),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = levelTitle,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.question_score,
                                state.score
                            ),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDark
                        )

                        if (state.totalQuestions > 0) {
                            Text(
                                text = stringResource(
                                    R.string.question_counter,
                                    state.questionNumber,
                                    state.totalQuestions
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(
                                R.string.question_attempts_left,
                                state.attemptsLeft
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextDark
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            repeat(3) { index ->
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .background(
                                            color = if (index < state.attemptsLeft) {
                                                EasyColor
                                            } else {
                                                MaterialTheme.colorScheme.surfaceVariant
                                            },
                                            shape = CircleShape
                                        )
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(
                            R.string.question_time_left,
                            state.timeLeft
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.End,
                        color = timerColor
                    )

                    LinearProgressIndicator(
                        progress = state.timeLeft / 15f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp),
                        color = timerColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = Primary
                        )
                    }
                }

                state.isEmpty -> {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.question_empty),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = LightSurface
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = state.currentRiddle?.question.orEmpty(),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.SemiBold,
                                textAlign = TextAlign.Center,
                                color = TextDark
                            )

                            if (state.answered) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = stringResource(
                                        when {
                                            state.timedOut ->
                                                R.string.question_timeout_feedback
                                            state.selectedAnswer == state.correctAnswer ->
                                                R.string.question_correct_feedback
                                            else ->
                                                R.string.question_wrong_feedback
                                        }
                                    ),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    color = if (
                                        state.selectedAnswer == state.correctAnswer &&
                                        !state.timedOut
                                    ) {
                                        CorrectGreen
                                    } else {
                                        WrongRed
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        state.shuffledOptions.forEach { option ->
                            val isCorrectOption =
                                state.answered && option == state.correctAnswer
                            val isWrongSelection =
                                state.answered &&
                                        !state.timedOut &&
                                        option == state.selectedAnswer &&
                                        option != state.correctAnswer

                            val buttonColor = when {
                                isCorrectOption -> CorrectGreen
                                isWrongSelection -> WrongRed
                                else -> LightSurface
                            }

                            val contentColor = when {
                                isCorrectOption || isWrongSelection -> Color.White
                                else -> TextDark
                            }

                            OutlinedButton(
                                onClick = { viewModel.selectAnswer(option) },
                                enabled = !state.answered && !state.isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp),
                                shape = RoundedCornerShape(18.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = buttonColor,
                                    contentColor = contentColor,
                                    disabledContainerColor = buttonColor,
                                    disabledContentColor = contentColor
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (isCorrectOption || isWrongSelection) {
                                        2.dp
                                    } else {
                                        1.dp
                                    },
                                    color = when {
                                        isCorrectOption -> CorrectGreen
                                        isWrongSelection -> WrongRed
                                        else -> Primary.copy(alpha = 0.55f)
                                    }
                                )
                            ) {
                                Text(
                                    text = option,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = if (
                                        isCorrectOption || isWrongSelection
                                    ) {
                                        FontWeight.Bold
                                    } else {
                                        FontWeight.Medium
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}