package app.revanced.patches.bilibili.misc.other.patch

import app.revanced.extensions.toErrorResult
import app.revanced.patcher.annotation.Description
import app.revanced.patcher.annotation.Name
import app.revanced.patcher.data.BytecodeContext
import app.revanced.patcher.extensions.InstructionExtensions.addInstructions
import app.revanced.patcher.patch.BytecodePatch
import app.revanced.patcher.patch.PatchResult
import app.revanced.patcher.patch.PatchResultSuccess
import app.revanced.patcher.patch.annotations.Patch
import app.revanced.patches.bilibili.annotations.BiliBiliCompatibility
import app.revanced.patches.bilibili.misc.other.fingerprints.BLRouteBuilderFingerprint
import app.revanced.patches.bilibili.misc.other.fingerprints.RouteRequestFingerprint

@Patch
@BiliBiliCompatibility
@Name("bl-route-intercept")
@Description("哔哩哔哩页面路由修改")
class BLRoutePatch : BytecodePatch(listOf(BLRouteBuilderFingerprint, RouteRequestFingerprint)) {
    override fun execute(context: BytecodeContext): PatchResult {
        BLRouteBuilderFingerprint.result?.mutableClass?.methods?.find { m ->
            m.name == "<init>" && m.parameterTypes.let { it.size == 1 && it[0] == "Landroid/net/Uri;" }
        }?.addInstructions(
            0, """
            invoke-static {p1}, Lapp/revanced/bilibili/patches/BLRoutePatch;->intercept(Landroid/net/Uri;)Landroid/net/Uri;
            move-result-object p1
        """.trimIndent()
        ) ?: return BLRouteBuilderFingerprint.toErrorResult()
        RouteRequestFingerprint.result?.mutableClass?.methods?.find { m ->
            m.name == "<init>" && m.parameterTypes.let { it.size == 2 && it[0] == "Landroid/net/Uri;" }
        }?.addInstructions(
            0, """
            invoke-static {p1}, Lapp/revanced/bilibili/patches/BLRoutePatch;->intercept(Landroid/net/Uri;)Landroid/net/Uri;
            move-result-object p1
        """.trimIndent()
        ) ?: return RouteRequestFingerprint.toErrorResult()
        return PatchResultSuccess()
    }
}
