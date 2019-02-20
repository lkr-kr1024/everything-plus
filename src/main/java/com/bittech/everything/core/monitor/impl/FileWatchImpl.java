package com.bittech.everything.core.monitor.impl;

import com.bittech.everything.core.common.FileConvertThing;
import com.bittech.everything.core.common.HandlePath;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;


//public FileAlterationObserver(final String directoryName) observer的构造里要的就是一个目录，
// FileAlterationObserver这个类里有实现的各种方法
//FileAlterationObserver实现了FileAlterationListener
//FileAlterationMonitor里有个线程

//monitor下还有个FileAlterationListenerAdaptor适配器，它继承了FileAlterationListener，
// 供选择可实现适配器里的方法或者接口listener的方法
public class FileWatchImpl implements FileWatch, FileAlterationListener {

    private FileIndexDao fileIndexDao;

    private FileAlterationMonitor monitor;

    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(10);
    }

    @Override
    public void onStart(FileAlterationObserver observer) {
//        observer.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File directory) {
        System.out.println("onDirectoryCreate " + directory);
    }

    @Override
    public void onDirectoryChange(File directory) {
        System.out.println("onDirectoryChange " + directory);
    }

    @Override
    public void onDirectoryDelete(File directory) {
        System.out.println("onDirectoryDelete " + directory);
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("onFileCreate " + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        System.out.println("onFileChange " + file);
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("onFileDelete " + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
//        observer.removeListener(this);
    }

    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
//    public void monitor(HandlePath handlePath) {
//        //监控的是includePath集合
//        for (String path : handlePath.getIncludePath()) {
//            this.monitor.addObserver(new FileAlterationObserver(path, new FileFilter() {
//                @Override
//                public boolean accept(File pathname) {
//                    String currentPath = pathname.getAbsolutePath();
//                    for (String excludePath:handlePath.getExcludePath()
//                         ) {
//                        if (excludePath.startsWith(currentPath)){
//                            return false;
//                        }
//                    }
//                    return true;
//                }
//            }));
//        }
//    }


        public void monitor(HandlePath handlePath) {
        //监控的是includePath集合
        for (String path : handlePath.getIncludePath()) {
            //只传一个参数path,会监控所有目录，但需求是在监控includepath的目录的时候把需要排除的目录排除掉不要监控
            //pathname filefilter在监控includepath的同时把需要排除掉的excludepath也排除掉
            FileAlterationObserver observer = new FileAlterationObserver(
                    path, pathname -> {
                String currentPath = pathname.getAbsolutePath();
                for (String excludePath : handlePath.getExcludePath()) {
                    if (excludePath.startsWith(currentPath)) {
                        //可以=，但startswith更好，因为存在目录的问题
                        //如果排除了目录A，那么A下的所有文件应该都排除
                        return false;
                    }
                }
                return true;
            });
            observer.addListener(this);
            this.monitor.addObserver(observer);
        }
    }


    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
