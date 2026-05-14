package com.horizondev.habitbloom.platform

import android.content.Context
import android.content.Intent
import android.net.Uri
import org.koin.core.context.GlobalContext

actual val appVersionName: String
    get() = runCatching {
        val context: Context = GlobalContext.get().get()
        val versionName = context.packageManager.getPackageInfo(context.packageName, 0).versionName
        versionName ?: "1.0"
    }.getOrDefault("1.0")

actual val appStoreName: String = "Google Play"

actual fun openStoreMainPage() {
    runCatching {
        val context: Context = GlobalContext.get().get()
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}
