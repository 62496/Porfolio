package com.example.he2bproject.ui

import androidx.annotation.StringRes
import com.example.he2bproject.R

/**
 * Represents all navigation destinations of the application.
 * Each screen is associated with a title resource.
 */
enum class He2bScreen(@StringRes val title: Int) {
    Login(title = R.string.login_screen),
    Home(title = R.string.home_screen ),
    About(title = R.string.about_screen ),
    History(title = R.string.history_screen ),
    QuizList(title = R.string.quiz_list_screen),
    QuizDetail(title = R.string.quiz_detail_screen),
    QuizGame(title = R.string.quiz_game_screen),
    QuizResult(title = R.string.quiz_result_screen),

    RandomConfig(title = R.string.random_config_screen)


}