package com.rscottcarson.circleschedulertest;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Explode;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private View view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        setContentView(R.layout.activity_main);

        view1 = findViewById(R.id.circle1);

        view1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "onClick");
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
//                // create the transition animation - the images in the layouts
//                // of both activities are defined with android:transitionName="profile"
//                ActivityOptions options = ActivityOptions
//                        .makeSceneTransitionAnimation(MainActivity.this, view1, "profile");
//                // start the new activity
//                startActivity(intent, options.toBundle());


                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(
                        MainActivity.this, new Pair<>(findViewById(R.id.circle1), getString(R.string.circle_trans)));

                        startActivity(intent, activityOptions.toBundle());

            }
        });

    }


}
