package com.whu.healthapp.mio;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.whu.healthapp.R;
import com.whu.healthapp.mio.fragment.HeartRateFragment;


/**
 * Created by 47462 on 2016/11/20.
 */
public class HeartRateActivity extends FragmentActivity {

    private HeartRateFragment fragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartrate);

        fragment = new HeartRateFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, fragment).commit();


    }
}
