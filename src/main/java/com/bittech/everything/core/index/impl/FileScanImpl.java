package com.bittech.everything.core.index.impl;

import com.bittech.everything.config.EverythingPlusConfig;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.model.Thing;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class FileScanImpl implements FileScan {

//    private final DataSource dataSource;
//    public FileScanImpl(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
    //update
    //DAO
    private EverythingPlusConfig config = EverythingPlusConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();
    @Override
    public void index(String path) {
        File file = new File(path);
        //递归
        if (file.isFile()){
            //D:\a\b\abc.pdf    ->  D:\a\b  只要文件的父目录在排除目录中包含那么就排除它，反之加进fileList
            if (config.getExcludePath().contains(file.getParent())){
                return;
            }
        }else {
            if (config.getExcludePath().contains(path)) {
                return;
            }else {
                File[] files = file.listFiles();
                if (files != null){
                    for (File f:files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
        }
        //File Directory拦截处理
        for (FileInterceptor interceptor:this.interceptors) {
            interceptor.apply(file);
        }
    }

    @Override
    public void interceptor(FileInterceptor interceptor) {
            this.interceptors.add(interceptor);
    }
}

//fileindex和filesearch都是数据源操作数据库，代码特点一样
//把fileindex和filesearch关于数据库的操作合并为数据库访问的类，称为dao