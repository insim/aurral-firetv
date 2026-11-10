package org.aurral.firetv.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

/** Top-level destinations reachable from the nav rail. */
enum class AppDestination(
    val route: String,
    val label: String,
    val icon: ImageVector,
) {
    HOME("home", "Home", Icons.Filled.Home),
    SEARCH("search", "Search", Icons.Filled.Search),
    ARTISTS("artists", "Artists", Icons.Filled.Person),
    ALBUMS("albums", "Albums", Icons.Filled.Album),
    GENRES("genres", "Genres", Icons.Filled.Category),
    PLAYLISTS("playlists", "Playlists", Icons.Filled.QueueMusic),
    FAVORITES("favorites", "Favorites", Icons.Filled.Favorite),
    SETTINGS("settings", "Settings", Icons.Filled.Settings),
}
