package com.whu.healthapp.bean.user;

import android.content.Context;

import com.whu.healthapp.R;
import com.whu.healthapp.utils.ContextNullException;


public class Child extends User {

    private Child() {
    }

    ;

    public static User getInstance(Context context) {
        synchronized (User.class) {
            if (context == null) {
                throw new ContextNullException();
            }
            if (User == null || !(User instanceof Child)) {
                User = new Child();
                User.mSpUser = context.getApplicationContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
                User.setType(Integer.parseInt(context.getResources().getString(R.string.user_type_child)));
                User.mContext = context.getApplicationContext();
            }
            return User;
        }

    }


}
