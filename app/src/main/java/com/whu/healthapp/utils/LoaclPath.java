package com.whu.healthapp.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;


/**
 * Created by 47462 on 2016/9/23.
 */
public class LoaclPath {
    private static String projectFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/jqb";


    /**
     * @return the projectFolder
     */
    public static String getUserFolder(Context context) {
        File file = new File(projectFolder + "/" + 1);
        if (!file.exists()) {
            file.mkdirs();
        }
        return projectFolder + "/" + 1;
    }
}
