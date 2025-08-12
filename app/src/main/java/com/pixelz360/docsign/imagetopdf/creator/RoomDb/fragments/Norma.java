package com.pixelz360.docsign.imagetopdf.creator.RoomDb.fragments;

import android.animation.ObjectAnimator;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

public class Norma {




    public void newClss(TextView scanTextView, HorizontalScrollView horizontalScrollView) {

        scanTextView.post(() -> {
            int scrollRange = scanTextView.getWidth() - horizontalScrollView.getWidth();

            if (scrollRange > 0) {
                // Animate the scrolling
                ObjectAnimator animator = ObjectAnimator.ofInt(
                        horizontalScrollView,
                        "scrollX",
                        0,
                        scrollRange
                );
                animator.setDuration(2000); // Duration of animation in milliseconds
                animator.setRepeatCount(ObjectAnimator.INFINITE); // Infinite loop
                animator.setRepeatMode(ObjectAnimator.RESTART); // Restart when done
                animator.start();
            }
        });
    }
}
