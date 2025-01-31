package org.juseni.daytoday


import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.juseni.daytoday.ui.NavGraph
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        Navigation()
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
