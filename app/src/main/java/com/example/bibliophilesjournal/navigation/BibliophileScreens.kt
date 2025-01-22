package com.example.bibliophilesjournal.navigation

enum class BibliophileScreens {

    SplashScreen,
    LoginScreen,
    CreateAccountScreen,
    BookHomeScreen,
    SearchScreen,
    DetailScreen,
    UpdateScreen,
    BookStatsScreen;

    companion object {
        fun fromRoute(route: String?): BibliophileScreens
                = when(route?.substringBefore("/")) {
            SplashScreen.name -> SplashScreen
            LoginScreen.name -> LoginScreen
            CreateAccountScreen.name -> CreateAccountScreen
            BookHomeScreen.name -> BookHomeScreen
            SearchScreen.name -> SearchScreen
            DetailScreen.name -> DetailScreen
            UpdateScreen.name -> UpdateScreen
            BookStatsScreen.name -> BookStatsScreen
            null -> BookHomeScreen
            else -> throw IllegalArgumentException("Route $route is not recognized")
        }
    }

}