package app.kitan.patches.youtube.blockchannel

import app.kitan.patches.youtube.Constants.COMPATIBILITY_YOUTUBE
import app.morphe.patcher.extensions.InstructionExtensions.addInstructions
import app.morphe.patcher.patch.patch

private const val EXTENSION_CLASS = "Lapp/kitan/extension/youtube/ChannelBlocker;"

/**
 * "Block channel" patch.
 *
 * Features:
 *  - Adds a "Block channel" button to the video flyout (three-dot) menu.
 *  - Persists the channel id of the blocked channel locally.
 *  - Hides every feed item (search results + recommendations) from blocked
 *    channels via a Litho filter.
 *
 * The flyout button is a toggle: tapping it on an already-blocked channel
 * unblocks it, so no separate settings screen is required.
 */
@Suppress("unused")
val blockChannelPatch = patch(
    name = "Block channel",
    description = "Adds a 'Block channel' button to the video menu. " +
            "Blocked channels are hidden from search results and video recommendations.",
    default = true
) {
    compatibleWith(COMPATIBILITY_YOUTUBE)

    extendWith("extensions/extension.mpe")

    execute {
        // 1. Initialize the extension (loads the persisted blocklist).
        StartupFingerprint.method.addInstructions(
            0,
            """
                invoke-static {}, $EXTENSION_CLASS;->initialize()V
            """
        )

        // 2. Register the Litho filter so feed/search items from blocked
        //    channels are removed before they are measured/rendered.
        //    The extension inspects the protobuf buffer of every mounted
        //    component and returns the block decision for its channel id.
        LithoFilterFingerprint.method.addInstructions(
            0,
            """
                invoke-static { p1, p2 }, $EXTENSION_CLASS;->filterFeedItem(Ljava/lang/String;Ljava/lang/Object;)Z
                move-result v0
                if-eqz v0, :filter_continue
                return-void
                :filter_continue
                nop
            """
        )

        // 3. Append the "Block channel" toggle to the video flyout menu.
        FlyoutMenuFingerprint.method.addInstructions(
            0,
            """
                invoke-static { p0 }, $EXTENSION_CLASS;->addFlyoutMenuItem(Ljava/lang/Object;)V
            """
        )
    }
}
