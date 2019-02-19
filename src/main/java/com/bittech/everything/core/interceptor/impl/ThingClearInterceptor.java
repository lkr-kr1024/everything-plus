package com.bittech.everything.core.interceptor.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.interceptor.ThingInterceptor;
import com.bittech.everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public class ThingClearInterceptor implements ThingInterceptor, Runnable {//线程从队列中取Thing

    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    @Override
    public void run() {
        while (true) {
            Thing thing = this.queue.poll();
            //防止delete时空指针异常，JavaAPI：Queue.java中197行
            if (thing != null) {
                fileIndexDao.delete(thing);
            }
            //1.优化 批量删除，修改delete为批量删除
            //List<Thing> thingList = new ArrayList<>();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
