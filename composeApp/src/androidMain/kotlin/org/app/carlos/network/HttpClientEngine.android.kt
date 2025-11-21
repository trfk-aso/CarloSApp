package org.app.carlos.network

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.*

actual fun createPlatformEngine(): HttpClientEngineFactory<*> = OkHttp