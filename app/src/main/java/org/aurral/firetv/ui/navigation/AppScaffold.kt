package org.aurral.firetv.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.aurral.firetv.ui.components.MiniPlayerBar
import org.aurral.firetv.ui.theme.Background

/** Root layout: nav rail, the current screen, and the persistent mini player. */
@Composable
fun AppScaffold() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Row(modifier = Modifier.fillMaxSize().background(Background)) {
        AppNavRail(
            currentRoute = currentRoute,
            onSelect = { destination -> navController.navigateToTopLevel(destination) },
        )
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                AppNavHost(navController = navController)
            }
            MiniPlayerBar(onOpenNowPlaying = { navController.navigate(Routes.NOW_PLAYING) })
        }
    }
}

private fun NavHostController.navigateToTopLevel(destination: AppDestination) {
    navigate(destination.route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
