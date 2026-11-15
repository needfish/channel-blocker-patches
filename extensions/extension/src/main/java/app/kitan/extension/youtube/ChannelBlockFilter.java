package app.kitan.extension.youtube;

/**
 * Extracts channel ids from Litho feed/search components so
 * {@link ChannelBlocker#filterFeedItem} can decide what to hide.
 *
 * Covers both surfaces:
 *  - search results  (component paths containing "search")
 *  - recommendations (watch-next / home feed renderers)
 *
 * ⚠️ TODO(verify): the protobuf field numbers below follow YouTube's public
 * video_with_context / compact_video renderer layout and must be verified
 * against 21.30.216 with jadx before release.
 */
final class ChannelBlockFilter {

    private ChannelBlockFilter() {
    }

    /**
     * @return the channel id (UC...) of the feed item, or null if the component
     * is not a video renderer.
     */
    static String getChannelId(String componentPath, Object protobufBuffer) {
        if (componentPath == null || protobufBuffer == null) {
            return null;
        }
        if (!isVideoRenderer(componentPath)) {
            return null;
        }
        return ProtobufHelper.getChannelId(protobufBuffer);
    }

    private static boolean isVideoRenderer(String componentPath) {
        // Search results and recommendation renderers share these path names.
        return componentPath.contains("video_with_context")
                || componentPath.contains("compact_video")
                || componentPath.contains("search_video_with_context");
    }
}
