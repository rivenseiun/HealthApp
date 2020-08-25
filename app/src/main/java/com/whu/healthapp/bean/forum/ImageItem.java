package com.whu.healthapp.bean.forum;

import java.io.Serializable;

/**
 * Created by Jiang.YX on 2016/10/29.
 */
public class ImageItem implements Serializable
{
    private static final long serialVersionUID = -7188270558443739436L;
    public String imageId;
    public String thumbnailPath;
    public String sourcePath;
    public boolean isSelected = false;
}

