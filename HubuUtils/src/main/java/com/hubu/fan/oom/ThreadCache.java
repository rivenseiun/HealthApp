package com.hubu.fan.oom;

/**
 * Created by 47462 on 2016/10/18.
 */
public interface ThreadCache {

    public void put(Thread thread);

    public void recycle();
}
