package org.juseni.daytoday.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import org.juseni.daytoday.ui.screens.ConsolidatedScreen
import org.juseni.daytoday.ui.screens.HomeScreen
import org.juseni.daytoday.ui.screens.IncomesScreen
import org.juseni.daytoday.ui.screens.LoginScreen
import org.juseni.daytoday.ui.screens.MonthsScreen
import org.juseni.daytoday.ui.screens.NewApartmentScreen
import org.juseni.daytoday.ui.screens.NewBillScreen

object ScreenRoute {
    const val LOGIN_SCREEN = "login"
    const val HOME_SCREEN = "home"
    const val MONTHS_SCREEN = "months"
    const val CONSOLIDATED_SCREEN = "consolidated"
    const val NEW_BILL_SCREEN = "new_bill"
    const val NEW_APARTMENT_SCREEN = "new_apartment"
    const val INCOMES_SCREEN = "incomes"
}

fun NavController.navigateTop(route: String) {
    navigate(route) {
        popUpTo(graph.id) {
            inclusive = true
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = ScreenRoute.LOGIN_SCREEN
    ) {
        composable(ScreenRoute.LOGIN_SCREEN) {
            LoginScreen(navController)
        }

        composable(ScreenRoute.HOME_SCREEN) {
            HomeScreen(navController)
        }

        composable(ScreenRoute.MONTHS_SCREEN) {
            MonthsScreen(navController)
        }

        composable(
            route = "${ScreenRoute.CONSOLIDATED_SCREEN}/{monthSelected}/{yearSelected}",
            arguments = listOf(
                navArgument("monthSelected") { type = NavType.IntType },
                navArgument("yearSelected") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val monthSelected = backStackEntry.arguments?.getInt("monthSelected")
            val yearSelected = backStackEntry.arguments?.getInt("yearSelected")

            ConsolidatedScreen(
                navController = navController,
                monthSelected = monthSelected ?: 0,
                yearSelected = yearSelected ?: 0
            )
        }

        composable(ScreenRoute.NEW_BILL_SCREEN) {
            NewBillScreen(navController)
        }

        composable(ScreenRoute.NEW_APARTMENT_SCREEN) {
            NewApartmentScreen(navController)
        }

        composable(ScreenRoute.INCOMES_SCREEN) {
            IncomesScreen(navController)
        }


    }
}