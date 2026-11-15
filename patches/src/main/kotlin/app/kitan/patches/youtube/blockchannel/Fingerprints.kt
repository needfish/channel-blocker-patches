package app.kitan.patches.youtube.blockchannel

import app.morphe.patcher.Fingerprint
import app.morphe.patcher.string
import com.android.tools.smali.dexlib2.AccessFlags

/**
 * Hooks the app startup path so the extension can initialize its
 * SharedPreferences blocklist before any feed/search filtering runs.
 *
 * ⚠️ TODO(verify): this fingerprint targets YouTube 21.30.216. Verify against
 * the decompiled APK (jadx) before releasing. The Morphe upstream project has
 * an equivalent hook; cross-check with:
 *   https://github.com/MorpheApp/morphe-patches/tree/main/patches/src/main/kotlin/app/morphe/patches/youtube/misc/integrations
 */
object StartupFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    // The application class onCreate path calls the integrations initializer
    // (Morphe/ReVanced style). Match the obfuscated caller via the extension
    // class reference it already contains.
    strings = listOf(
        "com.google.android.youtube.application.Application"
    )
)

/**
 * Hooks the Litho layout creation path used by feed/search renderers, so every
 * mounted component can be checked against the channel blocklist.
 *
 * ⚠️ TODO(verify): verify against the decompiled 21.30.216 APK. Look for the
 * method that receives a `com.facebook.litho.ComponentContext` and a deep
 * protobuf buffer of the feed item — the Morphe upstream equivalent lives in
 * patches/youtube/misc/litho/filter/. The method typically contains the
 * string constant "tree" and calls ComponentContext#getLogger.
 */
object LithoFilterFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.STATIC),
    returnType = "V",
    filters = listOf(
        string("feedItem") // TODO(verify): pin down with jadx
    )
)

/**
 * Hooks the video flyout (three-dot) menu builder for player and feed items so
 * the extension can append a "Block channel" row.
 *
 * ⚠️ TODO(verify): verify against the decompiled 21.30.216 APK. Search for the
 * class handling the engagement flyout that references
 * "video_context_menu" / "playability" menu item ids. Upstream reference:
 *   https://github.com/MorpheApp/morphe-patches — extensions/youtube .../utils/FlyoutUtils.java
 */
object FlyoutMenuFingerprint : Fingerprint(
    accessFlags = listOf(AccessFlags.PUBLIC, AccessFlags.FINAL),
    returnType = "V",
    strings = listOf(
        "FlyoutMenu" // TODO(verify)
    )
)
