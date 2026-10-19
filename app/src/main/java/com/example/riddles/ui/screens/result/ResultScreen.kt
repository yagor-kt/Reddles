package com.example.riddles.ui.screens.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.riddles.R
import com.example.riddles.ui.components.PrimaryButton
import com.example.riddles.ui.theme.CorrectGreen
import com.example.riddles.ui.theme.WrongRed

@Composable
fun ResultScreen(
    isWin: Boolean,
    score: Int,
    correctAnswer: String?,
    viewModel: ResultViewModel,
    onPlayAgain: () -> Unit,
    onGoHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.recordResult()
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    if (isWin) R.string.result_win_title else R.string.result_loss_title
                ),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = if (isWin) CorrectGreen else WrongRed,
                textAlign = TextAlign.Center
            )

            if (!isWin && !correctAnswer.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = stringResource(
                        R.string.result_correct_answer,
                        correctAnswer
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = stringResource(R.string.result_score, score),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )

            if (isWin && state.isNewRecord) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.result_new_record),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = CorrectGreen,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = stringResource(R.string.result_play_again),
                onClick = onPlayAgain
            )

            Spacer(modifier = Modifier.height(12.dp))

            PrimaryButton(
                text = stringResource(R.string.result_home),
                onClick = onGoHome
            )
        }
    }
}