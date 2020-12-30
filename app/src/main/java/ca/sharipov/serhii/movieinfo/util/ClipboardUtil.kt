package ca.sharipov.serhii.movieinfo.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.core.content.ContextCompat


fun Context.copyToClipboard(text: String) {
    val clipboard = ContextCompat.getSystemService(this, ClipboardManager::class.java)
    val clip = ClipData.newPlainText("label", text)
    clipboard?.setPrimaryClip(clip)
}
