package com.example.riddles.ui.navigation

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import com.example.riddles.R
import com.example.riddles.data.AppDatabase
import com.example.riddles.data.RiddleRepository
import com.example.riddles.data.UserRepository
import com.example.riddles.ui.screens.home.HomeScreen
import com.example.riddles.ui.screens.home.HomeViewModel
import com.example.riddles.ui.screens.login.LoginScreen
import com.example.riddles.ui.screens.question.QuestionScreen
import com.example.riddles.ui.screens.question.QuestionViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext
    val database = AppDatabase.getDatabase(context)
    val userRepository = UserRepository(database.userDao())
    val riddleRepository = RiddleRepository(database.riddleDao())

    NavHost(
        navController = navController,
        startDestination = Screen.Login
    ) {
        composable(Screen.Login) {
            LoginScreen(
                userRepository = userRepository,
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Login) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "home?chooseLevel={chooseLevel}",
            arguments = listOf(
                navArgument("chooseLevel") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val autoOpen = backStackEntry.arguments?.getBoolean("chooseLevel") ?: false
            val homeViewModel: HomeViewModel = viewModel()

            HomeScreen(
                viewModel = homeViewModel,
                autoOpenLevelDialog = autoOpen,
                onChooseLevel = { level ->
                    navController.navigate(Screen.questionRoute(level))
                },
                onOpenStatistics = {
                    navController.navigate(Screen.Stats)
                },
                onLogout = {
                    com.example.riddles.data.CurrentUserHolder.set(null)
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Home) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.Question,
            arguments = listOf(navArgument("level") { type = NavType.StringType })
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level").orEmpty()
            val factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return QuestionViewModel(level, riddleRepository) as T
                }
            }
            val questionViewModel: QuestionViewModel = viewModel(factory = factory)

            QuestionScreen(
                level = level,
                viewModel = questionViewModel,
                onRoundFinished = { result ->
                    navController.navigate(
                        Screen.resultRoute(
                            isWin = result.isWin,
                            score = result.score,
                            correctAnswer = result.correctAnswer
                        )
                    )
                }
            )
        }

        composable(
            route = Screen.Result,
            arguments = listOf(
                navArgument("isWin") { type = NavType.BoolType },
                navArgument("score") { type = NavType.IntType },
                navArgument("correctAnswer") { type = NavType.StringType }
            )
        ) {
            PlaceholderScreen(
                title = stringResource(R.string.result_score, 0)
            )
        }

        composable(Screen.Stats) {
            PlaceholderScreen(
                title = stringResource(R.string.stats_title)
            )
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(title)
    }
}