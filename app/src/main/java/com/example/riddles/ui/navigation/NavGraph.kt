package com.example.riddles.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.riddles.data.AppDatabase
import com.example.riddles.data.CurrentUserHolder
import com.example.riddles.data.RiddleRepository
import com.example.riddles.data.UserRepository
import com.example.riddles.ui.screens.home.HomeScreen
import com.example.riddles.ui.screens.home.HomeViewModel
import com.example.riddles.ui.screens.login.LoginScreen
import com.example.riddles.ui.screens.question.QuestionScreen
import com.example.riddles.ui.screens.question.QuestionViewModel
import com.example.riddles.ui.screens.result.ResultScreen
import com.example.riddles.ui.screens.result.ResultViewModel
import com.example.riddles.ui.screens.stats.StatsScreen

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
                        popUpTo(Screen.Login) {
                            inclusive = true
                        }
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
            val autoOpenLevelDialog =
                backStackEntry.arguments?.getBoolean("chooseLevel") ?: false
            val homeViewModel: HomeViewModel = viewModel()

            HomeScreen(
                viewModel = homeViewModel,
                autoOpenLevelDialog = autoOpenLevelDialog,
                onChooseLevel = { level ->
                    navController.navigate(Screen.questionRoute(level))
                },
                onOpenStatistics = {
                    navController.navigate(Screen.Stats)
                },
                onLogout = {
                    CurrentUserHolder.set(null)
                    navController.navigate(Screen.Login) {
                        popUpTo(Screen.Home) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(
            route = Screen.Question,
            arguments = listOf(
                navArgument("level") {
                    type = NavType.StringType
                }
            )
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
                navArgument("isWin") {
                    type = NavType.BoolType
                },
                navArgument("score") {
                    type = NavType.IntType
                },
                navArgument("correctAnswer") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val isWin = backStackEntry.arguments?.getBoolean("isWin") ?: false
            val score = backStackEntry.arguments?.getInt("score") ?: 0
            val answerArgument =
                backStackEntry.arguments?.getString("correctAnswer")
            val correctAnswer = answerArgument
                ?.takeUnless { it == "-" }

            val factory = object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ResultViewModel(
                        userRepository = userRepository,
                        score = score,
                        isWin = isWin
                    ) as T
                }
            }
            val resultViewModel: ResultViewModel = viewModel(factory = factory)

            ResultScreen(
                isWin = isWin,
                score = score,
                correctAnswer = correctAnswer,
                viewModel = resultViewModel,
                onPlayAgain = {
                    navController.navigate("home?chooseLevel=true") {
                        popUpTo(Screen.Home) {
                            inclusive = true
                        }
                    }
                },
                onGoHome = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Home) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Stats) {
            StatsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}