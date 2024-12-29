package org.juseni.daytoday

//import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.juseni.daytoday.theme.DayToDayTheme
import org.juseni.daytoday.ui.NavGraph
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        Navigation()
    }
//    DayToDayTheme {
////        var showContent by remember { mutableStateOf(false) }
////        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
////            Button(onClick = { showContent = !showContent }) {
////                Text("Click me!")
////            }
////            AnimatedVisibility(showContent) {
////                val greeting = remember { Greeting().greet() }
////                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
////                    Image(painterResource(Res.drawable.compose_multiplatform), null)
////                    Text("Compose: $greeting")
////                }
////            }
////        }
//    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavGraph(navController = navController)
}
