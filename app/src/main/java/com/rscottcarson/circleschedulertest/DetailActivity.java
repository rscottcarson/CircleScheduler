package com.rscottcarson.circleschedulertest;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;

/**
 * Created by scottcarson on 1/11/17.
 */

public class DetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_detail);

        final View view = findViewById(R.id.circle_detail);

        postponeEnterTransition();
        scheduleStartPostponedTransition(view);

        // set an exit transition
        CircleSchedulerTransition transition = new CircleSchedulerTransition();
        transition.addTarget(view);

        transition.setDuration(1000);

        getWindow().setSharedElementExitTransition(new CircleSchedulerTransition());
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setSharedElementEnterTransition(new CircleSchedulerTransition());

    }


    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        Log.i("onPreDraw", "on predraw");
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        startPostponedEnterTransition();
                        return true;
                    }
                });
    }

    @Override
    public void onBackPressed(){
        finishAfterTransition();
    }
}
