package me.myfilemanager.Custom;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

/**
 * Created by vV on 2015/6/30.
 */
public class CustomDrawer extends DrawerLayout {

    private String TAG = CustomDrawer.class.getSimpleName();
    public CustomDrawer(Context context) {
        super(context, null);
    }

    public CustomDrawer(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    public CustomDrawer(Context context, AttributeSet attrs, int defStyle){


        super(context, attrs, defStyle);
    }

    private @interface EdgeGravity {}

    public boolean isDrawerVisible(@EdgeGravity int drawerGravity) {
        final View drawerView = findDrawerWithGravity(Gravity.END);
        if (drawerView != null) {
            return isDrawerVisible(drawerView);
        }
        return false;
    }

    public void openDrawer(@EdgeGravity int gravity) {
        final View drawerView = findDrawerWithGravity(Gravity.END);
        if (drawerView == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " +
                    gravityToString(gravity));
        }
        openDrawer(drawerView);

    }

    public void closeDrawer(@EdgeGravity int gravity) {
        final View drawerView = findDrawerWithGravity(Gravity.END);
        if (drawerView == null) {
            throw new IllegalArgumentException("No drawer view found with gravity " +
                    gravityToString(gravity));
        }
        closeDrawer(drawerView);
    }
    static String gravityToString(@EdgeGravity int gravity) {
        if ((gravity & Gravity.LEFT) == Gravity.LEFT) {
            return "LEFT";
        }
        if ((gravity & Gravity.RIGHT) == Gravity.RIGHT) {
            return "RIGHT";
        }
        return Integer.toHexString(gravity);
    }

    View findDrawerWithGravity(int gravity) {
        final int absHorizGravity = GravityCompat.getAbsoluteGravity(
                gravity, ViewCompat.getLayoutDirection(this)) & Gravity.HORIZONTAL_GRAVITY_MASK;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = getChildAt(i);
            final int childAbsGravity = getDrawerViewAbsoluteGravity(child);
            if ((childAbsGravity & Gravity.HORIZONTAL_GRAVITY_MASK) == absHorizGravity) {
                return child;
            }
        }
        return null;
    }
    int getDrawerViewAbsoluteGravity(View drawerView) {
        final int gravity = ((LayoutParams) drawerView.getLayoutParams()).gravity;
        return GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(this));
    }
}
