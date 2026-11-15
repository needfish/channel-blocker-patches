package app.kitan.extension.youtube;

/**
 * Flyout (three-dot) menu integration.
 *
 * ⚠️ TODO(verify): the Litho menu-building calls below must be matched against
 * the decompiled 21.30.216 flyout classes. The Morphe upstream project ships a
 * battle-tested reference implementation — see
 *   https://github.com/MorpheApp/morphe-patches
 *   extensions/youtube/src/main/java/app/morphe/extension/youtube/patches/utils/FlyoutUtils.java
 * which builds flyout rows with the app's own Litho component factories
 * (ThumbnailAspectRatio ->... row builder). Port the row-building block from
 * there and keep only the tap handler call from this file.
 */
final class FlyoutMenu {

    private FlyoutMenu() {
    }

    interface MenuItemTap {
        void onTap();
    }

    static String getVideoId(Object menu) {
        // TODO(verify): read the video id attached to the flyout menu context.
        return null;
    }

    static String resolveChannelId(String videoId) {
        // TODO(verify): resolve channel id for the video (player response /
        // browse id lookup). Morphe's FlyoutUtils resolves this for its own
        // menu entries; port the same helper.
        return null;
    }

    static void addMenuItem(Object menu, String title, MenuItemTap onTap) {
        // TODO(verify): append a Litho row matching the app's own menu items.
    }

    static void onBlockToggleTapped() {
        ChannelBlocker.onBlockToggleTapped();
    }
}
