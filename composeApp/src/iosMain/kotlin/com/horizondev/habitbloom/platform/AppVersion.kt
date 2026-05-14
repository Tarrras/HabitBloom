package com.horizondev.habitbloom.platform

import platform.Foundation.NSBundle
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual val appVersionName: String
    get() = NSBundle.mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: "1.0"

actual val appStoreName: String = "App Store"

actual fun openStoreMainPage() {
    NSURL.URLWithString("https://apps.apple.com/")?.let { url ->
        UIApplication.sharedApplication.openURL(url)
    }
}
