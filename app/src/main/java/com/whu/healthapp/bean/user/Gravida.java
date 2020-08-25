package com.whu.healthapp.bean.user;

import android.content.Context;

import com.whu.healthapp.R;
import com.whu.healthapp.utils.ContextNullException;


public class Gravida extends User {

    private Gravida() {
    }

    ;

    public static User getInstance(Context context) {
        synchronized (User.class) {
            if (context == null) {
                throw new ContextNullException();
            }
            if (User == null || !(User instanceof Gravida)) {
                User = new Gravida();
                User.mSpUser = context.getApplicationContext().getSharedPreferences("USER", Context.MODE_PRIVATE);
                User.setType(Integer.parseInt(context.getResources().getString(R.string.user_type_gravida)));
                User.mContext = context.getApplicationContext();
            }
            return User;
        }

    }

}
