package org.app.carlos

import kotlinx.cinterop.ExperimentalForeignApi
import platform.UIKit.*
import platform.Foundation.*
import org.app.carlos.di.initKoin

@OptIn(ExperimentalForeignApi::class)
class AppDelegate : UIResponder(), UIApplicationDelegateProtocol {

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?
    ): Boolean {
        initKoin()

        val window = UIWindow(frame = UIScreen.main.bounds)
        window.rootViewController = MainViewController()
        window.makeKeyAndVisible()

        this.window = window

        return true
    }
}
