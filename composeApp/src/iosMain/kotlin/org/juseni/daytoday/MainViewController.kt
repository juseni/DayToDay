package org.juseni.daytoday

import androidx.compose.ui.window.ComposeUIViewController
import org.juseni.daytoday.di.initializeKoin

fun MainViewController() = ComposeUIViewController(
    configure = { initializeKoin() }
) {
    App()
}