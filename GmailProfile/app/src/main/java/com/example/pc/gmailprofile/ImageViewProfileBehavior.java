package com.example.pc.gmailprofile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toolbar;

/**
 * Created by PC on 15/09/2016.
 */
public class ImageViewProfileBehavior extends CoordinatorLayout.Behavior<ImageButton> {


    private final static float MIN_AVATAR_PERCENTAGE_SIZE   = 0.3f;
    private final static int EXTRA_FINAL_AVATAR_PADDING     = 80;

    private final static String TAG = "behavior";
    private final Context mContext;
    private float mAvatarMaxSize;

    private float mFinalLeftAvatarPadding;
    private float mStartPosition;
    private int mStartXPosition;
    private float mStartToolbarPosition;

    private int mStartYPosition;

    private int mFinalYPosition;
    private int finalHeight;
    private int mStartHeight;
    private int mFinalXPosition;

    public ImageViewProfileBehavior(Context context, AttributeSet attrs) {
        mContext = context;
        init();

        mFinalLeftAvatarPadding = context.getResources().getDimension(R.dimen.activity_horizontal_margin);
    }


//    public ImageViewProfileBehavior(){
//
//    }

    private void init() {
        bindDimensions();
    }

    private void bindDimensions() {
        mAvatarMaxSize = mContext.getResources().getDimension(R.dimen.image_width);
    }


    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, ImageButton child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageButton child, View dependency) {
        maybeInitProperties(child, dependency);
        //Log.d("TesteBehavior", String.valueOf(dependency.getY()));

        child.setY(dependency.getY() + 600);
        child.setX((float) (dependency.getX() + 200 - (dependency.getY() * -1)/3.3));

        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
        lp.width = (int) (mStartHeight - (dependency.getY() * -1)/3.5);
        lp.height = (int) (mStartHeight - (dependency.getY() * -1)/3.5);
        child.setLayoutParams(lp);
        return true;
        //return super.onDependentViewChanged(parent, child, dependency);
    }


//    @Override
//    public boolean onDependentViewChanged(CoordinatorLayout parent, ImageButton child, View dependency) {
//        maybeInitProperties(child, dependency);
//
//        final int maxScrollDistance = (int) (mStartToolbarPosition - getStatusBarHeight());
//        float expandedPercentageFactor = dependency.getY() / maxScrollDistance;
//
//        float distanceYToSubtract = ((mStartYPosition - mFinalYPosition)
//                * (1f - expandedPercentageFactor)) + (child.getHeight()/2);
//
//        float distanceXToSubtract = ((mStartXPosition - mFinalXPosition)
//                * (1f - expandedPercentageFactor)) + (child.getWidth()/2);
//
//        float heightToSubtract = ((mStartHeight - finalHeight) * (1f - expandedPercentageFactor));
//
//        child.setY(mStartYPosition - distanceYToSubtract);
//        child.setX(mStartXPosition - distanceXToSubtract);
//
//        int proportionalAvatarSize = (int) (mAvatarMaxSize * (expandedPercentageFactor));
//
//        CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) child.getLayoutParams();
//        lp.width = (int) (mStartHeight - heightToSubtract);
//        lp.height = (int) (mStartHeight - heightToSubtract);
//        child.setLayoutParams(lp);
//        return true;
//    }
//
    @SuppressLint("PrivateResource")
    private void maybeInitProperties(ImageButton child, View dependency) {
        if (mStartYPosition == 0)
            mStartYPosition = (int) (dependency.getY());

        if (mFinalYPosition == 0)
            mFinalYPosition = (dependency.getHeight() /2);

        if (mStartHeight == 0)
            mStartHeight = child.getHeight();

        if (finalHeight == 0)
            finalHeight = mContext.getResources().getDimensionPixelOffset(R.dimen.image_small_width);

        if (mStartXPosition == 0)
            mStartXPosition = (int) (child.getX() + (child.getWidth() / 2));

        if (mFinalXPosition == 0)
            mFinalXPosition = mContext.getResources().getDimensionPixelOffset(R.dimen.abc_action_bar_content_inset_material) + (finalHeight / 2);

        if (mStartToolbarPosition == 0)
            mStartToolbarPosition = dependency.getY() + (dependency.getHeight()/2);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("app_bar_height", "dimen", "android");

        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }

        Log.d("TesteBehavior", "STATUS BAR: " + String.valueOf(result));
        return result;
    }
}
