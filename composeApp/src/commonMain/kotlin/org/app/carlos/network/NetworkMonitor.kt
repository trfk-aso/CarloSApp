package org.app.carlos.network

import kotlinx.coroutines.flow.StateFlow

interface NetworkMonitor {
    val isConnected: StateFlow<Boolean>
    fun start()
    fun stop()
}

expect fun createNetworkMonitor(): NetworkMonitor