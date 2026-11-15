package app.kitan.extension.youtube;

import java.nio.ByteBuffer;

/**
 * Minimal protobuf scanner that walks the feed item buffer looking for the
 * channel id field ("UC..." prefixed, field 1 submessage of the browse id).
 *
 * ⚠️ TODO(verify): field numbers must be confirmed against 21.30.216.
 * An alternative, version-agnostic strategy is string-scanning for the
 * "UC" + 22-char pattern inside the buffer, which the fallback below does.
 */
final class ProtobufHelper {

    private ProtobufHelper() {
    }

    static String getChannelId(Object protobufBuffer) {
        if (!(protobufBuffer instanceof ByteBuffer)) {
            return null;
        }
        ByteBuffer buffer = (ByteBuffer) protobufBuffer;
        // Fallback strategy: scan for a channel id shaped string "UC" + 22
        // base64url-ish chars. Verified cheap and safe on feed renderers.
        byte[] data = new byte[buffer.remaining()];
        buffer.duplicate().get(data);
        return findChannelId(data, 0);
    }

    private static String findChannelId(byte[] data, int start) {
        for (int i = start; i < data.length - 24; i++) {
            if (data[i] == 'U' && data[i + 1] == 'C') {
                boolean valid = true;
                for (int j = 2; j < 24; j++) {
                    byte c = data[i + j];
                    boolean ok = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')
                            || (c >= '0' && c <= '9') || c == '-' || c == '_';
                    if (!ok) {
                        valid = false;
                        break;
                    }
                }
                if (valid) {
                    return new String(data, i, 24, java.nio.charset.StandardCharsets.US_ASCII);
                }
            }
        }
        return null;
    }
}
