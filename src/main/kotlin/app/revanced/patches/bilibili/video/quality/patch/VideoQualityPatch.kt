package app.revanced.patches.bilibili.video.quality.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.extensions.InstructionExtensions.addInstructionsWithLabels
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.bilibili.annotations.BiliBiliCompatibility
import app.revanced.patches.bilibili.patcher.patch.MultiMethodBytecodePatch
import app.revanced.patches.bilibili.utils.toErrorResult
import app.revanced.patches.bilibili.video.quality.fingerprints.PlayerQualityServiceFingerprint
import app.revanced.patches.bilibili.video.quality.fingerprints.PlayerSettingHelperFingerprint
import org.jf.dexlib2.iface.Method

@Patch
@Name("video-default-quality")
@BiliBiliCompatibility
@Description("视频默认画质设置")
class VideoQualityPatch : MultiMethodBytecodePatch(
    fingerprints = listOf(PlayerSettingHelperFingerprint),
    multiFingerprints = listOf(PlayerQualityServiceFingerprint)
) {
    override fun execute(context: BytecodeContext): PatchResult {
        super.execute(context)
        var defaultQnMethod: Method? = null
        PlayerSettingHelperFingerprint.result?.also {
            defaultQnMethod = it.method
        }?.mutableMethod?.addInstructionsWithLabels(
            0, """
            invoke-static {}, Lapp/revanced/bilibili/patches/VideoQualityPatch;->fullScreenQuality()I
            move-result v0
            if-eqz v0, :jump
            return v0
            :jump
            nop
        """.trimIndent()
        ) ?: return PlayerSettingHelperFingerprint.toErrorResult()
        PlayerQualityServiceFingerprint.result.ifEmpty {
            return PlayerQualityServiceFingerprint.toErrorResult()
        }.forEach {
            it.mutableMethod.addInstructionsWithLabels(
                0, """
                invoke-static {}, Lapp/revanced/bilibili/patches/VideoQualityPatch;->getMatchedHalfScreenQuality()I
                move-result v0
                if-eqz v0, :jump
                return v0
                :jump
                nop
            """.trimIndent()
            )
        }
        context.findClass("Lapp/revanced/bilibili/patches/VideoQualityPatch;")?.let { clazz ->
            clazz.mutableClass.methods.find { it.name == "defaultQn" }?.addInstructions(
                0, """
                invoke-static {}, $defaultQnMethod
                move-result v0
                return v0
            """.trimIndent()
            )
        }
        return PatchResultSuccess()
    }
}
