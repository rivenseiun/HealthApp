package com.whu.healthapp.activity.personer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.whu.healthapp.R;
import com.whu.healthapp.activity.BaseActivity;
import com.whu.healthapp.activity.homepage.HomePagerActivity;
import com.whu.healthapp.bean.user.Child;
import com.whu.healthapp.bean.user.Lady;
import com.whu.healthapp.bean.user.Normal;
import com.whu.healthapp.bean.user.Older;
import com.whu.healthapp.bean.user.User;
import com.whu.healthapp.utils.UserInfo;

public class PersonSelect extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_select);
//        if ((User.getInstance(PersonSelect.this) instanceof Child ||
//                User.getInstance(PersonSelect.this) instanceof Lady ||
//                User.getInstance(PersonSelect.this) instanceof Older ||
//                User.getInstance(PersonSelect.this) instanceof Normal)) {
        //if(User.getInstance(PersonSelect.this).isSel()){
            Intent intent = null;

            //传递用户的手机号码
            intent = new Intent(this, HomePagerActivity.class);
            intent.putExtra("phone",User.getInstance(PersonSelect.this).getPhone());

            startActivity(intent);
            finish();
        //}
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.personer:
                Normal.getInstance(this);
                User.getInstance(this).setSel(true);
                UserInfo.setSel(this,getString(R.string.personer));
                intent = new Intent(this, HomePagerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.child:
                Child.getInstance(this);
                UserInfo.setSel(this,getString(R.string.child));
                User.getInstance(this).setSel(true);
                intent = new Intent(this, HomePagerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.lady:
                Lady.getInstance(this);
                UserInfo.setSel(this,getString(R.string.lady));
                User.getInstance(this).setSel(true);
                intent = new Intent(this, HomePagerActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.older:
                Older.getInstance(this);
                UserInfo.setSel(this,getString(R.string.older));
                User.getInstance(this).setSel(true);
                intent = new Intent(this, HomePagerActivity.class);
                startActivity(intent);
                finish();
                break;

        }
    }
}


