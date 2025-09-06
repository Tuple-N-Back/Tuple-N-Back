package org.squidfish.tuplenback.presentation.util

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.squidfish.tuplenback.presentation.navigation.ScreenDestination

inline fun <reified T: ScreenDestination> NavGraphBuilder.composable(
    noinline content: @Composable AnimatedContentScope.(T) -> Unit
) = composable<T> { backStackEntry ->
    content(backStackEntry.toRoute<T>())
}
