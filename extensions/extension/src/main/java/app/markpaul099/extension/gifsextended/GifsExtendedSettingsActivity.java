package app.markpaul099.extension.gifsextended;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

/**
 * The GIFs Extended screen, opened from Gboard's settings. Built in code so it needs no
 * resources of its own.
 */
@SuppressWarnings("unused")
public final class GifsExtendedSettingsActivity extends Activity {

    /** Toggles one setting and returns whether it was saved. */
    private interface Setter {
        boolean set(Context context, boolean enabled);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? android.R.style.Theme_DeviceDefault_DayNight
                : android.R.style.Theme_DeviceDefault_Light);
        super.onCreate(savedInstanceState);
        setTitle("GIFs Extended");
        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        LinearLayout list = new LinearLayout(this);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(0, dp(8), 0, dp(8));

        list.addView(header("GIF search"));
        list.addView(toggle(
                "Search Google Images",
                "GIF search shows animated results from Google Images. Turn off to use Tenor only.",
                GifsExtendedSettings.googleImages(this),
                GifsExtendedSettings::setGoogleImages));
        list.addView(toggle(
                "SafeSearch",
                "Filter explicit results from Google Images and Tenor.",
                GifsExtendedSettings.safeSearch(this),
                GifsExtendedSettings::setSafeSearch));

        ScrollView scroll = new ScrollView(this);
        scroll.addView(list);
        setContentView(scroll);
    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return true;
    }

    private TextView header(String text) {
        TextView header = new TextView(this);
        header.setText(text);
        header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        header.setTextColor(themeColor(android.R.attr.colorAccent));
        header.setPadding(dp(16), dp(16), dp(16), dp(8));
        return header;
    }

    private View toggle(String title, String summary, boolean checked, Setter setter) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(dp(16), dp(12), dp(16), dp(12));
        row.setMinimumHeight(dp(72));

        LinearLayout text = new LinearLayout(this);
        text.setOrientation(LinearLayout.VERTICAL);
        TextView titleView = new TextView(this);
        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        titleView.setTextColor(themeColor(android.R.attr.textColorPrimary));
        TextView summaryView = new TextView(this);
        summaryView.setText(summary);
        summaryView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        summaryView.setTextColor(themeColor(android.R.attr.textColorSecondary));
        text.addView(titleView);
        text.addView(summaryView);
        row.addView(text, new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        Switch toggle = new Switch(this);
        toggle.setChecked(checked);
        toggle.setContentDescription(title);
        toggle.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(android.widget.CompoundButton view, boolean enabled) {
                if (!setter.set(GifsExtendedSettingsActivity.this, enabled)) {
                    // Not saved (device still locked): show the stored value again
                    // without calling this listener a second time.
                    view.setOnCheckedChangeListener(null);
                    view.setChecked(!enabled);
                    view.setOnCheckedChangeListener(this);
                }
            }
        });
        row.addView(toggle);
        row.setOnClickListener(view -> toggle.toggle());
        return row;
    }

    private int themeColor(int attribute) {
        TypedArray values = obtainStyledAttributes(new int[]{attribute});
        try {
            return values.getColor(0, 0xFF000000);
        } finally {
            values.recycle();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
