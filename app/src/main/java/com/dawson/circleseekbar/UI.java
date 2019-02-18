package com.dawson.circleseekbar;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.animation.AlphaAnimation;
import android.view.animation.CycleInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class UI {
    public static final float ALPHA_ENABLE = 1.0f;
    public static final float ALPHA_DISABLE = 0.4f;

    public static Spannable formatString(Context ctx, String str, int secondaryStyle) {
        int startSpan = str.indexOf("\n");
        int endSpan = str.length();
        Spannable spanString = new SpannableString(str);
        spanString.setSpan(new TextAppearanceSpan(ctx, secondaryStyle), startSpan, endSpan, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spanString;
    }

    public static CharSequence stylizeString(CharSequence text) {
        text = UI.setSpanBetweenTokens(text, "##", new StyleSpan(Typeface.BOLD));

        return text;
    }

    public static CharSequence setSpanBetweenTokens(CharSequence text, String token, CharacterStyle... cs) {
        // Start and end refer to the points where the span will apply
        int tokenLen = token.length();
        int start = text.toString().indexOf(token) + tokenLen;
        int end = text.toString().indexOf(token, start);

        if (start > -1 && end > -1) {
            // Copy the spannable string to a mutable spannable string
            SpannableStringBuilder ssb = new SpannableStringBuilder(text);
            for (CharacterStyle c : cs) {
                ssb.setSpan(c, start, end, 0);
            }

            // Delete the tokens before and after the span
            ssb.delete(end, end + tokenLen);
            ssb.delete(start - tokenLen, start);

            text = ssb;
        }

        return text;
    }

    public static int dp2px(float dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (dp * density + 0.5);
    }

/*
    private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
	}
*/

    public static int px2dp(int px) {
//	    return (int) (px / Resources.getSystem().getDisplayMetrics().density);
        float density = Resources.getSystem().getDisplayMetrics().density;
        return (int) (px / density + 0.5);
    }

    public static int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                Resources.getSystem().getDisplayMetrics());
    }

    public static int displayWidthInPx() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int displayHeightInPx() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public static int realScreenHeightInPx(Activity activity) {
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        return metrics.heightPixels;
    }


    public static AlphaAnimation animationStart(ImageView iv) {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        animation.setDuration(2000);
        animation.setInterpolator(new CycleInterpolator(1));
        iv.startAnimation(animation);
        return animation;
    }

    public static void setImageAndTextEnable(@Nullable ImageView imageView, @Nullable TextView textView,
                                             boolean status) {
        float alphaValue = status ? ALPHA_ENABLE : ALPHA_DISABLE;
        if (imageView != null) {
            imageView.setEnabled(status);
            imageView.setAlpha(alphaValue);
        }
        if (textView != null) {
            textView.setAlpha(alphaValue);
        }
    }

    @ColorInt
    public static int getColor(Context context, @ColorRes int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(resId);
        } else {
            return context.getResources().getColor(resId);
        }
    }

    /**
     * @param activity                you must set this param
     * @param useThemeStatusBarColor  Whether you want set the color of the status bar, if it is not set, it is
     *                                transparent
     * @param whiteStatusBarIconColor the status icon will be white if true
     */
    public static void setStatusBar(Activity activity, boolean useThemeStatusBarColor, boolean whiteStatusBarIconColor) {
/*        if (activity instanceof VideoActivity) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0 and above
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            if (useThemeStatusBarColor) {
                activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.white));
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
*//*        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4 to 5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }*//*

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !whiteStatusBarIconColor) {
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }*/
    }
}
