package app.kitan.extension.youtube;

import android.content.Context;

/**
 * Shared helpers for resolving the current app context/activity.
 *
 * ⚠️ TODO(verify): if this patch bundle is used together with the official
 * Morphe patches, their extension already provides a context holder
 * (app/morphe/extension/shared/Utils). This fallback keeps this bundle
 * self-contained when used standalone.
 */
final class Utils {

    private static Context appContext;

    private Utils() {
    }

    public static Context getContext() {
        Context context = appContext;
        if (context != null) {
            return context;
        }
        try {
            // Standard trick to get the application context without hooking
            // Activity lifecycles.
            Class<?> activityThread = Class.forName("android.app.ActivityThread");
            Object current = activityThread.getMethod("currentActivityThread").invoke(null);
            Object app = activityThread.getMethod("getApplication").invoke(current);
            if (app instanceof Context) {
                appContext = (Context) app;
                return appContext;
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static android.app.Activity getActivity() {
        Context context = getContext();
        if (context instanceof android.app.Activity) {
            return (android.app.Activity) context;
        }
        // TODO(verify): walk the resumed activity stack if needed.
        return null;
    }
}
