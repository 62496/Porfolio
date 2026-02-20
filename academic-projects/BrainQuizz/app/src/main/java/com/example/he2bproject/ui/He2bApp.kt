@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.he2bproject.ui


import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.he2bproject.ui.about.AboutScreen
import com.example.he2bproject.ui.game.QuizGameScreen
import com.example.he2bproject.ui.login.LoginScreen
import com.example.he2bproject.ui.homeScreen.HomeScreen
import com.example.he2bproject.ui.game.QuizGameViewModel
import com.example.he2bproject.ui.history.HistoryScreen
import com.example.he2bproject.ui.quiz.QuizDetailScreen
import com.example.he2bproject.ui.quiz.QuizListScreen
import com.example.he2bproject.ui.random.RandomConfigScreen
import com.example.he2bproject.ui.result.QuizResultScreen

/**
 * Root composable of the application.
 * Handles navigation, top bar, bottom bar and screen transitions.
 */
@Composable
fun He2bApp(
    navController: NavHostController = rememberNavController(),
) {
    // Determine current screen based on navigation route
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val currentScreen = He2bScreen.entries.find {
        it.name == currentRoute
    } ?: He2bScreen.Login
    /**
     * Shared ViewModel used to manage the quiz game state in Random Mode and Local Mode.
     * It is created at the app level to survive navigation between screens
     * (QuizGame -> QuizResult).
     */
    val quizGameViewModel: QuizGameViewModel = viewModel()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,

        topBar = {
            // Back Button is hidden on authentication and game-related screens
            val hideBackButton = currentScreen == He2bScreen.QuizGame || currentScreen == He2bScreen.QuizResult
            He2bAppBar(
                currentScreen = currentScreen,
                canNavigateBack = !hideBackButton && navController.previousBackStackEntry != null,
                navigateUp = { navController.navigateUp() }
            )
        },
        bottomBar = {
            // Bottom bar is hidden on authentication and game-related screens && setUp-related screen
            val showBottomBar = currentScreen != He2bScreen.Login &&
                    currentScreen != He2bScreen.QuizDetail &&
                    currentScreen != He2bScreen.QuizGame &&
                    currentScreen != He2bScreen.QuizResult &&
                    currentScreen != He2bScreen.RandomConfig
            if (showBottomBar) {
                He2bBottomBar(currentRoute = currentRoute, navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = He2bScreen.Login.name,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(He2bScreen.Login.name) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(He2bScreen.Home.name) {
                            popUpTo(He2bScreen.Login.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(He2bScreen.Home.name) {
                HomeScreen(
                    onLocalClick = {
                        navController.navigate(He2bScreen.QuizList.name)
                    },
                    onRandomClick = {
                        navController.navigate(He2bScreen.RandomConfig.name) {
                        }
                    }
                )
            }
            composable(He2bScreen.About.name) {
                AboutScreen()
            }
            composable(He2bScreen.History.name) {
                HistoryScreen()

            }

            composable(He2bScreen.QuizList.name) {
                QuizListScreen(
                    onQuizClick = { quiz ->
                        navController.currentBackStackEntry
                            ?.savedStateHandle
                            ?.set("quizId", quiz.id)

                        navController.navigate(He2bScreen.QuizDetail.name)
                    }
                )
            }
            /**
             * The quizId is passed using SavedStateHandle to avoid
             * putting complex objects in navigation routes.
             */
            composable(He2bScreen.QuizDetail.name) {
                val quizId =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Long>("quizId")
                quizId?.let { id ->
                    QuizDetailScreen(
                        quizId = id,
                        onStartQuiz = { quizId ->
                            navController.currentBackStackEntry
                                ?.savedStateHandle
                                ?.set("quizId", quizId)

                            quizGameViewModel.startLocalGame(quizId)
                            navController.navigate(He2bScreen.QuizGame.name)
                        }
                    )
                }
            }
            composable(He2bScreen.QuizGame.name) {
                QuizGameScreen(
                    viewModel = quizGameViewModel,
                    // When leaving the game (save or cancel), always return to Home and clear the back stack
                    // to avoid navigating back into an old game screen.
                    onQuitGame = {
                        // Remove game screens from the back stack so the user can't return to the game by pressing back
                        navController.navigate(He2bScreen.Home.name) {
                            // Pop everything above Home so the user can't go back to the game
                            popUpTo(He2bScreen.Home.name) { inclusive = false }
                            launchSingleTop = true
                        }
                    },                    
                    onGameFinished = { score, total, title ->
                        // Passing results to result screen
                        navController.currentBackStackEntry?.savedStateHandle?.apply {
                            set("score", score)
                            set("questions", total)
                            set("quizTitle", title)
                        }
                        navController.navigate(He2bScreen.QuizResult.name)
                    }
                )
            }
            /**
             * Game results are passed via SavedStateHandle to keep navigation simple
             * and avoid creating a dedicated result object.
             */
            composable(He2bScreen.QuizResult.name) {
                val score =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Int>("score") ?: 0

                val title =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<String>("quizTitle") ?: ""

                val questions =
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.get<Int>("questions") ?: 0

                QuizResultScreen(
                    quizTitle = title,
                    score = score,
                    totalQuestions = questions,
                    onBackToMenu = {
                        navController.navigate(He2bScreen.Home.name) {
                            popUpTo(He2bScreen.Home.name) { inclusive = true }
                        }
                    }
                )
            }
            composable(He2bScreen.RandomConfig.name) {
                RandomConfigScreen(
                    quizGameViewModel = quizGameViewModel,
                    navController = navController
                )
            }


        }
    }
}

@Composable
fun He2bAppBar(
    currentScreen: He2bScreen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(currentScreen.title),
                style = MaterialTheme.typography.titleLarge
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        }
    )
}

@Composable
fun He2bBottomBar(
    currentRoute: String?,
    navController: NavHostController
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        NavigationBarItem(
            selected = currentRoute == He2bScreen.Home.name,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.surface
            ),
            onClick = {
                navController.navigate(He2bScreen.Home.name) {
                    launchSingleTop = true
                }
            },
            label = { Text(stringResource(com.example.he2bproject.R.string.home_screen)) },
            icon = { Icon(Icons.Default.Home, null) }

        )

        NavigationBarItem(
            selected = currentRoute == He2bScreen.History.name,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.surface
            ),
            onClick = {
                navController.navigate(He2bScreen.History.name) {
                    launchSingleTop = true
                }
            },
            label = { Text(stringResource(com.example.he2bproject.R.string.history_screen)) },
            icon = { Icon(Icons.Default.Info, null) }
        )

        NavigationBarItem(
            selected = currentRoute == He2bScreen.About.name,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                indicatorColor = MaterialTheme.colorScheme.surface
            ),
            onClick = {
                navController.navigate(He2bScreen.About.name) {
                    launchSingleTop = true
                }
            },
            label = { Text(stringResource(com.example.he2bproject.R.string.about_screen)) },
            icon = { Icon(Icons.Default.Info, null) }
        )
    }
}


