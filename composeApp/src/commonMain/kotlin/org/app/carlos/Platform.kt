package org.app.carlos

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform