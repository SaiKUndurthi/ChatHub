package edu.sfsu.csc780.chathub.ui;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.PreferenceManager;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;

import edu.sfsu.csc780.chathub.R;

public class DesignUtils {

    private static final String LOG_TAG = DesignUtils.class.getSimpleName();
    /**
     * This utility method takes a bitmap and tries to apply a auto palette color to the
     * associated view.
     * The Palatte API doesn't always return every type of color swatch, so we'll attempt to get a
     * swatch in the following preference order:
     * 1) light muted
     * 2) light vibrant
     * 3) muted
     * @param bitmap
     * @param backgroundView
     */
    public static void setBackgroundFromPalette(Bitmap bitmap, final View backgroundView) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {

                // We prefer the light muted colors so let's try that first
                Palette.Swatch swatch = palette.getLightMutedSwatch();

                // If we didn't find a light muted, let's try a light vibrant
                if (swatch == null) {
                    swatch = palette.getLightVibrantSwatch();
                }

                // If we still don't have a color swatch, let fall back to a muted option
                if (swatch == null) {
                    swatch = palette.getMutedSwatch();
                }

                int color = swatch != null ? swatch.getRgb() : backgroundView.getSolidColor();
                //Log.d(LOG_TAG, "color:" + color + " default:" + backgroundView.getSolidColor());

                Drawable backgroundDrawable = backgroundView.getBackground();
                if (backgroundDrawable instanceof GradientDrawable) {
                    ((GradientDrawable)backgroundDrawable).setColor(color);
                } else {
                    Log.e(LOG_TAG, "Cannot apply palette because view is not a shape");
                }
            }
        });
    }

    /**
     * Use Android DateUtils to format a timestamp for messages
     * @param context
     * @param timestamp
     * @return
     */
    public static String formatTime(Context context, long timestamp) {
        return  DateUtils.getRelativeDateTimeString(context, timestamp,
                DateUtils.MINUTE_IN_MILLIS,
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_RELATIVE).toString();
    }

    /**
     * Get the theme that is stored in our shared preferences
     * @param context
     * @return
     */
    public static int getPreferredTheme(Context context) {
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences
                (context);
        final String preferenceName = context.getString(R.string.color_scheme_preference);
        String preferenceValue = preferences.getString(preferenceName, "0");
        return Integer.parseInt(preferenceValue);
    }

    /**
     * Apply the preferred theme to the Activity that is passed in
     * @param activity
     */
    public static void applyColorfulTheme(Activity activity) {

        int newTheme = 0;
        switch (getPreferredTheme(activity)) {
            case 1:
                newTheme = R.style.BlueTheme;
                break;
            case 2:
                newTheme = R.style.GreenTheme;
                break;
            case 3:
                newTheme = R.style.BlackTheme;
                break;
            default:
                newTheme = R.style.AppTheme;
        }
        activity.setTheme(newTheme);
    }
}
