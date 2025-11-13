package org.app.carlos.exporter

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentInteractionController
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

actual class FileOpener actual constructor() {
    @OptIn(ExperimentalForeignApi::class)
    actual fun openFile(path: String) {
        val fullPath = NSHomeDirectory() + "/Documents/" + path
        val url = NSURL.fileURLWithPath(fullPath)
        println("Trying to open: $fullPath")

        dispatch_async(dispatch_get_main_queue()) {
            val controller = UIDocumentInteractionController.interactionControllerWithURL(url)
            controller.name = path
            controller.delegate = null

            val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
            if (rootVC != null) {
                controller.presentOptionsMenuFromRect(rootVC.view.bounds, inView = rootVC.view, animated = true)
                println("✅ Presented document interaction controller")
            } else {
                println("⚠️ Failed to get root view controller")
            }
        }
    }
}