package app.kitan.extension.youtube;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Channel blocker.
 *
 * Holds the persistent blocklist and exposes the entry points the injected
 * bytecode calls:
 *
 *  - initialize()            app startup (see StartupFingerprint)
 *  - filterFeedItem(...)     Litho filter hook, returns true to hide an item
 *  - addFlyoutMenuItem(...)  video flyout menu hook, appends the toggle button
 *
 * Storage: app-private SharedPreferences (survives app updates, cleared on
 * data wipe — same trade-off Morphe's own settings make).
 */
@SuppressWarnings("unused")
public final class ChannelBlocker {

    private static final String PREFS_FILE = "kitan_channel_blocker";
    private static final String PREF_BLOCKED_CHANNELS = "blocked_channels";

    private static SharedPreferences prefs;
    private static volatile Set<String> blockedChannels;
    private static String currentVideoId;
    private static String currentChannelId;

    private ChannelBlocker() {
    }

    // region Lifecycle

    public static void initialize() {
        if (prefs != null) {
            return;
        }
        Context context = Utils.getContext();
        if (context == null) {
            return;
        }
        prefs = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        blockedChannels = Collections.synchronizedSet(
                new HashSet<>(prefs.getStringSet(PREF_BLOCKED_CHANNELS, Collections.emptySet())));
    }

    // endregion

    // region Blocklist

    public static boolean isChannelBlocked(String channelId) {
        Set<String> blocked = blockedChannels;
        return blocked != null && channelId != null && blocked.contains(channelId);
    }

    public static boolean blockChannel(String channelId) {
        Set<String> blocked = blockedChannels;
        if (blocked == null || channelId == null || channelId.isEmpty()) {
            return false;
        }
        blocked.add(channelId);
        persist();
        return true;
    }

    public static boolean unblockChannel(String channelId) {
        Set<String> blocked = blockedChannels;
        if (blocked == null || channelId == null) {
            return false;
        }
        boolean removed = blocked.remove(channelId);
        if (removed) {
            persist();
        }
        return removed;
    }

    private static void persist() {
        if (prefs == null) {
            return;
        }
        // SharedPreferences cannot store the live set; hand it a snapshot.
        prefs.edit().putStringSet(PREF_BLOCKED_CHANNELS, new HashSet<>(blockedChannels)).apply();
    }

    // endregion

    // region Litho feed filter (search results + recommendations)

    /**
     * Called from the injected Litho hook for every feed/search renderer.
     *
     * @param componentPath litho component tree path (e.g. "video_with_context")
     * @param protobufBuffer deep layout protobuf of the feed item
     * @return true when the item must be hidden (its channel is blocked)
     */
    public static boolean filterFeedItem(String componentPath, Object protobufBuffer) {
        if (blockedChannels == null || blockedChannels.isEmpty()) {
            return false;
        }
        String channelId = ChannelBlockFilter.getChannelId(componentPath, protobufBuffer);
        if (channelId == null) {
            return false;
        }
        return isChannelBlocked(channelId);
    }

    // endregion

    // region Flyout menu button

    /**
     * Called when a video flyout (three-dot) menu is about to be shown.
     * Adds a "Block channel" / "Unblock channel" row to the menu.
     *
     * @param menu the flyout menu instance (Litho popup builder) being created
     */
    public static void addFlyoutMenuItem(Object menu) {
        // Resolve the channel the menu belongs to from the flyout's video id.
        String videoId = FlyoutMenu.getVideoId(menu);
        if (videoId == null) {
            return;
        }
        currentVideoId = videoId;
        currentChannelId = null; // resolved lazily on first tap (see onTap below)

        FlyoutMenu.addMenuItem(menu, blockedChannels != null
                ? "Unblock channel"
                : "Block channel",
                FlyoutMenu::onBlockToggleTapped);
    }

    /**
     * Tap handler for the injected flyout row. Resolves the channel id for the
     * current video (if not yet resolved) and toggles the block.
     */
    static void onBlockToggleTapped() {
        String channelId = currentChannelId;
        if (channelId == null) {
            channelId = FlyoutMenu.resolveChannelId(currentVideoId);
            currentChannelId = channelId;
        }
        if (channelId == null) {
            showToast("Could not resolve channel");
            return;
        }

        if (isChannelBlocked(channelId)) {
            unblockChannel(channelId);
            showToast("Channel unblocked");
        } else {
            blockChannel(channelId);
            showToast("Channel blocked — removed from search & recommendations");
        }
    }

    private static void showToast(String message) {
        Activity activity = Utils.getActivity();
        if (activity != null) {
            activity.runOnUiThread(() ->
                    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show());
        }
    }

    // endregion
}
