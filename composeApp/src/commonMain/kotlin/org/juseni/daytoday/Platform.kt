package org.juseni.daytoday

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform