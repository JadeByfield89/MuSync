
package com.jbsoft.musync.activities;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.jbsoft.musync.R;
import com.jbsoft.musync.views.SlidingUpPanelLayout;
import com.jbsoft.musync.views.SlidingUpPanelLayout.PanelSlideListener;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;

public class SocialActivity extends SherlockActivity {

    ActionBar actionBar;

    View dragView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        setContentView(R.layout.social_activity);
        dragView = findViewById(R.id.dragView2);

        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_color));

        // Converts the panel height(65dp) to pixels
        Resources r = getResources();
        r.getDisplayMetrics();

        SlidingUpPanelLayout layout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        layout.setDragView(dragView);
        // layout.setShadowDrawable(getResources().getDrawable(R.drawable.drawer_shadow));

        layout.setPanelSlideListener(new PanelSlideListener() {

            @Override
            public void onPanelSlide(View panel, float slideOffset) {

                /*
                 * if (slideOffset < 0.2) { if (actionBar.isShowing()) {
                 * actionBar.hide(); } } else { if (!actionBar.isShowing()) {
                 * actionBar.show(); } }
                 */

            }

            @Override
            public void onPanelExpanded(View panel) {

            }

            @Override
            public void onPanelCollapsed(View panel) {

            }

        });

    }

}
