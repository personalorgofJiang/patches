package app.revanced.patches.youtube.misc.videobuffer.annotations

import app.revanced.patcher.annotation.Compatibility
import app.revanced.patcher.annotation.Package

// TODO: delete this
@Compatibility([Package("com.google.android.youtube", arrayOf("18.16.37", "18.19.35", "18.20.39", "18.23.35"))])
@Target(AnnotationTarget.CLASS)
internal annotation class CustomVideoBufferCompatibility
