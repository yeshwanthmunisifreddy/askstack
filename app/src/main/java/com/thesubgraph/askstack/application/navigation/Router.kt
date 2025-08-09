package com.thesubgraph.askstack.application.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.thesubgraph.askstack.base.theme.AskStackTheme
import com.thesubgraph.askstack.base.utils.noRippleClickable
import com.thesubgraph.askstack.features.stackoverflow.view.home.HomeScreen
import com.thesubgraph.askstack.features.stackoverflow.viewmodel.HomeViewModel

class Router(val navController: NavHostController) {
    @Composable
    fun ComposeRouter(destination: Destination) {
        val focusManager = LocalFocusManager.current
        val controller = remember { navController }
        AskStackTheme() {
            Box(modifier = Modifier.noRippleClickable { focusManager.clearFocus() }) {
                NavHost(
                    navController = controller,
                    startDestination = destination,
                    enterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    },
                    exitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(500)
                        )
                    },
                    popEnterTransition = {
                        slideIntoContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    },
                    popExitTransition = {
                        slideOutOfContainer(
                            AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(500)
                        )
                    }
                ) {
                    composable<Destination.Home> {
                        val viewModel: HomeViewModel = hiltViewModel()
                        HomeScreen(router = this@Router, viewModel = viewModel)
                    }
                }
            }
        }
    }

    fun navigateTo(destination: Destination) {
        navController.navigate(destination)
    }

    fun navigateUp() {
        navController.navigateUp()
    }
}
