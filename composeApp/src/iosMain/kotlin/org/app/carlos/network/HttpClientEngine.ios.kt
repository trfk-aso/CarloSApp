package org.app.carlos.network

import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*

actual fun createPlatformEngine(): HttpClientEngineFactory<*> = Darwin