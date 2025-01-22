package com.example.bibliophilesjournal.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.bibliophilesjournal.navigation.BibliophileScreens
import com.example.bibliophilesjournal.screens.BookSplashScreen
import com.example.bibliophilesjournal.screens.details.BookDetailsScreen
import com.example.bibliophilesjournal.screens.home.BookHomeScreen
import com.example.bibliophilesjournal.screens.home.HomeScreenViewModel
import com.example.bibliophilesjournal.screens.login.BookLoginScreen
import com.example.bibliophilesjournal.screens.search.BookSearchScreen
import com.example.bibliophilesjournal.screens.search.BooksSearchViewModel
import com.example.bibliophilesjournal.screens.stats.BookStatsScreen
import com.example.bibliophilesjournal.screens.update.BookUpdateScreen


@Composable
fun BibliophileNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController,
        startDestination = BibliophileScreens.SplashScreen.name ) {

        composable(BibliophileScreens.SplashScreen.name) {
            BookSplashScreen(navController = navController)
        }
        composable(BibliophileScreens.LoginScreen.name) {
            BookLoginScreen(navController = navController)
        }
        composable(BibliophileScreens.BookHomeScreen.name) {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            BookHomeScreen(navController = navController, viewModel = homeScreenViewModel)
        }
        composable(BibliophileScreens.SearchScreen.name) {
            val searchViewModel = hiltViewModel<BooksSearchViewModel>()
            BookSearchScreen(navController = navController, viewModel = searchViewModel)
        }
        composable(BibliophileScreens.BookStatsScreen.name) {
            val homeScreenViewModel = hiltViewModel<HomeScreenViewModel>()
            BookStatsScreen(navController = navController, viewModel = homeScreenViewModel)
        }


        val detailName = BibliophileScreens.DetailScreen.name
        composable("$detailName/{bookId}", arguments = listOf(navArgument("bookId"){
            type = NavType.StringType
        })) { backStackEntry ->
            backStackEntry.arguments?.getString("bookId").let {

                BookDetailsScreen(navController = navController, bookId = it.toString())
            }
        }

        val updateName = BibliophileScreens.UpdateScreen.name
        composable("$updateName/{bookItemId}",
            arguments = listOf(navArgument("bookItemId") {
                type = NavType.StringType
            })) { navBackStackEntry ->

            navBackStackEntry.arguments?.getString("bookItemId").let {
                BookUpdateScreen(navController = navController, bookItemId = it.toString())
            }
        }
    }
}
