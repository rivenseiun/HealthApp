package com.whu.healthapp.test;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hubu.fan.utils.listview.AlmightyAdapter;
import com.hubu.fan.utils.listview.AlmightyViewHolder;
import com.whu.healthapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {


    private ListView listView;
    private MyScrollView myScrollView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        listView = (ListView) findViewById(R.id.listview);
        List<String> data = new ArrayList<String>();
        data.add("");
        data.add("");
        data.add("");
        data.add("");
        data.add("");data.add("");
        data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");data.add("");
        WindowManager wm = getWindowManager();

        LinearLayout.LayoutParams ll = (LinearLayout.LayoutParams) listView.getLayoutParams();
        ll.height = wm.getDefaultDisplay().getHeight();
        listView.setAdapter(new AlmightyAdapter<String>(this, R.layout.activity_main2,data ) {
            @Override
            public void setView(AlmightyViewHolder viewHolder, String content) {

            }
        });
        myScrollView = (MyScrollView) findViewById(R.id.scrollView);
        myScrollView.listView = listView;
        myScrollView.linearLayout = (LinearLayout) findViewById(R.id.linerLayout);

        Toast.makeText(this,getCacheDir().getAbsolutePath(),Toast.LENGTH_LONG).show();


    }


    public void onClick(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
