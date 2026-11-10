package org.aurral.firetv.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.aurral.firetv.ui.albums.AlbumDetailScreen
import org.aurral.firetv.ui.albums.AlbumsScreen
import org.aurral.firetv.ui.artists.ArtistDetailScreen
import org.aurral.firetv.ui.artists.ArtistsScreen
import org.aurral.firetv.ui.favorites.FavoritesScreen
import org.aurral.firetv.ui.genres.GenreDetailScreen
import org.aurral.firetv.ui.genres.GenresScreen
import org.aurral.firetv.ui.home.HomeScreen
import org.aurral.firetv.ui.nowplaying.LyricsScreen
import org.aurral.firetv.ui.nowplaying.NowPlayingScreen
import org.aurral.firetv.ui.nowplaying.QueueScreen
import org.aurral.firetv.ui.playlists.PlaylistDetailScreen
import org.aurral.firetv.ui.playlists.PlaylistsScreen
import org.aurral.firetv.ui.search.SearchScreen
import org.aurral.firetv.ui.settings.SettingsScreen
import org.aurral.firetv.ui.songs.SongsScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.HOME.route,
        modifier = modifier,
    ) {
        composable(AppDestination.HOME.route) {
            HomeScreen(
                onOpenAlbum = { navController.navigate(Routes.albumDetail(it)) },
                onOpenPlaylist = { navController.navigate(Routes.playlistDetail(it)) },
            )
        }
        composable(AppDestination.SEARCH.route) {
            SearchScreen(
                onOpenAlbum = { navController.navigate(Routes.albumDetail(it)) },
                onOpenArtist = { navController.navigate(Routes.artistDetail(it)) },
            )
        }
        composable(AppDestination.SONGS.route) {
            SongsScreen()
        }
        composable(AppDestination.ARTISTS.route) {
            ArtistsScreen(onOpenArtist = { navController.navigate(Routes.artistDetail(it)) })
        }
        composable(AppDestination.ALBUMS.route) {
            AlbumsScreen(onOpenAlbum = { navController.navigate(Routes.albumDetail(it)) })
        }
        composable(AppDestination.GENRES.route) {
            GenresScreen(onOpenGenre = { navController.navigate(Routes.genreDetail(it)) })
        }
        composable(AppDestination.PLAYLISTS.route) {
            PlaylistsScreen(onOpenPlaylist = { navController.navigate(Routes.playlistDetail(it)) })
        }
        composable(AppDestination.FAVORITES.route) {
            FavoritesScreen(
                onOpenAlbum = { navController.navigate(Routes.albumDetail(it)) },
                onOpenArtist = { navController.navigate(Routes.artistDetail(it)) },
            )
        }
        composable(AppDestination.SETTINGS.route) {
            SettingsScreen()
        }

        composable(
            route = Routes.ARTIST_DETAIL,
            arguments = listOf(navArgument(Routes.ARTIST_ID) { type = NavType.StringType }),
        ) { entry ->
            ArtistDetailScreen(
                artistId = entry.arguments?.getString(Routes.ARTIST_ID).orEmpty(),
                onOpenAlbum = { navController.navigate(Routes.albumDetail(it)) },
            )
        }
        composable(
            route = Routes.ALBUM_DETAIL,
            arguments = listOf(navArgument(Routes.ALBUM_ID) { type = NavType.StringType }),
        ) { entry ->
            AlbumDetailScreen(
                albumId = entry.arguments?.getString(Routes.ALBUM_ID).orEmpty(),
                onOpenArtist = { navController.navigate(Routes.artistDetail(it)) },
            )
        }
        composable(
            route = Routes.GENRE_DETAIL,
            arguments = listOf(navArgument(Routes.GENRE_NAME) { type = NavType.StringType }),
        ) { entry ->
            GenreDetailScreen(
                genreName = entry.arguments?.getString(Routes.GENRE_NAME).orEmpty(),
            )
        }
        composable(
            route = Routes.PLAYLIST_DETAIL,
            arguments = listOf(navArgument(Routes.PLAYLIST_ID) { type = NavType.StringType }),
        ) { entry ->
            PlaylistDetailScreen(
                playlistId = entry.arguments?.getString(Routes.PLAYLIST_ID).orEmpty(),
                onBack = { navController.popBackStack() },
            )
        }

        composable(Routes.NOW_PLAYING) {
            NowPlayingScreen(
                onOpenQueue = { navController.navigate(Routes.QUEUE) },
                onOpenLyrics = { songId -> navController.navigate(Routes.lyrics(songId)) },
            )
        }
        composable(Routes.QUEUE) {
            QueueScreen()
        }
        composable(
            route = Routes.LYRICS,
            arguments = listOf(navArgument(Routes.SONG_ID) { type = NavType.StringType }),
        ) { entry ->
            LyricsScreen(songId = entry.arguments?.getString(Routes.SONG_ID).orEmpty())
        }
    }
}
