package com.whu.healthapp.activity.homepage;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.whu.healthapp.R;

public class HomePagerActivity extends Activity implements View.OnClickListener {

    //标签页控制
    private FragmentManager fragmentManager;
    private HomePager1Fragment tab1;
    private HomePager2Fragment tab2;
    private HomePager3Fragment tab3;
    private HomePager4Fragment tab4;
    private RadioGroup rgTabs;
    //private RadioButton rgTab1, rgTab2, rgTab3, rgTab4;

    private TextView tvTag1, tvTag2, tvTag3, tvTag4;
    private TextView tvTagName1, tvTagName2, tvTagName3, tvTagName4;

    public static Typeface iconfont = null;

    public final static Typeface getIconfont() {
        return iconfont;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pager);

        Typeface iconfont = Typeface.createFromAsset(getAssets(), "iconfont/iconfont.ttf");

        //获取控件
        tvTag1 = (TextView) findViewById(R.id.icon_assess);
        tvTag2 = (TextView) findViewById(R.id.icon_data);
        tvTag3 = (TextView) findViewById(R.id.icon_forum);
        tvTag4 = (TextView) findViewById(R.id.icon_user);
        tvTag1.setTypeface(iconfont);
        tvTag2.setTypeface(iconfont);
        tvTag3.setTypeface(iconfont);
        tvTag4.setTypeface(iconfont);
        tvTagName1 = (TextView) findViewById(R.id.tvTagName1);
        tvTagName2 = (TextView) findViewById(R.id.tvTagName2);
        tvTagName3 = (TextView) findViewById(R.id.tvTagName3);
        tvTagName4 = (TextView) findViewById(R.id.tvTagName4);

        //时间监听
        tvTag1.setOnClickListener(this);
        tvTag2.setOnClickListener(this);
        tvTag3.setOnClickListener(this);
        tvTag4.setOnClickListener(this);

        // 实例化
        tab1 = new HomePager1Fragment();
        tab2 = new HomePager2Fragment();
        tab3 = new HomePager3Fragment();
        tab4 = new HomePager4Fragment();
        fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.framelayout, tab1);
        fragmentTransaction.add(R.id.framelayout, tab2);
        fragmentTransaction.add(R.id.framelayout, tab3);
        fragmentTransaction.add(R.id.framelayout, tab4);


        //初始化
        initAllPage(fragmentTransaction);
        showTab(fragmentTransaction, tab1, tvTag1, tvTagName1);
        fragmentTransaction.commit();

    }

    /**
     * 切换页面所做的初始化操作
     *
     * @param transaction
     */
    public void initAllPage(FragmentTransaction transaction) {
        transaction.hide(tab1);
        transaction.hide(tab2);
        transaction.hide(tab3);
        transaction.hide(tab4);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvTag1.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTag2.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTag3.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTag4.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTagName1.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTagName2.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTagName3.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
            tvTagName4.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck, getTheme()));
        } else {
            tvTag1.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTag2.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTag3.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTag4.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTagName1.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTagName2.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTagName3.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
            tvTagName4.setTextColor(getResources().getColor(R.color.text_homepage_tab_nocheck));
        }
    }


    /**
     * 显示指定标签页
     *
     * @param transaction
     * @param fragment
     * @param tvTag
     */
    public void showTab(FragmentTransaction transaction, Fragment fragment, TextView tvTag, TextView tvTagName) {
        transaction.show(fragment);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tvTag.setTextColor(getResources().getColor(R.color.theme_green));
            tvTagName.setTextColor(getResources().getColor(R.color.theme_green));
        } else {
            tvTag.setTextColor(getResources().getColor(R.color.theme_green));
            tvTagName.setTextColor(getResources().getColor(R.color.theme_green));
        }
    }



    /**
     * 标签点击监听事件
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        FragmentTransaction fragmentTransactiona = null;
        fragmentTransactiona = fragmentManager.beginTransaction();
        initAllPage(fragmentTransactiona);
        switch (v.getId()) {
            case R.id.icon_assess :
                showTab(fragmentTransactiona, tab1, tvTag1, tvTagName1);
                break;
            case R.id.icon_data :
                showTab(fragmentTransactiona, tab2, tvTag2, tvTagName2);
                break;
            case R.id.icon_forum :
                showTab(fragmentTransactiona, tab4, tvTag3, tvTagName3);
                break;
            case R.id.icon_user :
                showTab(fragmentTransactiona, tab3, tvTag4, tvTagName4);
                break;
        }
        fragmentTransactiona.commit();
    }
}