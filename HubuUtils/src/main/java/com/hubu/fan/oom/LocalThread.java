package com.hubu.fan.oom;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 47462 on 2016/10/18.
 */
public class LocalThread implements ThreadCache {
    /**
     * 线程控制
     */
    protected List<Thread> mThreads = new ArrayList<Thread>();
    @Override
    public void put(Thread thread) {
        if (thread == null) {
            return;
        }
        mThreads.add(thread);
    }

    @Override
    public void recycle() {
        try {
            for (Thread thread : mThreads) {
                if (thread == null) {
                    continue;
                }
                if (!thread.isAlive()) {
                    thread.interrupt();
                }
            }
        } catch (Exception e) {
            System.out.println("清除线程缓冲池错误");
            e.printStackTrace();
        }
        mThreads.clear();
    }
}
