package com.whu.healthapp.mio;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.whu.healthapp.R;
import com.whu.healthapp.mio.fragment.ActionFragment;


/**
 * Created by 47462 on 2016/11/20.
 */
public class StepActivity extends FragmentActivity {

    private ActionFragment fragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heartrate);

        fragment = new ActionFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.framelayout, fragment).commit();


    }
}
