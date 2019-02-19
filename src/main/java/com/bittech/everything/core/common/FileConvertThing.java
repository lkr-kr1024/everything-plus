package com.bittech.everything.core.common;

import com.bittech.everything.core.model.FileType;
import com.bittech.everything.core.model.Thing;

import java.io.File;

/**
 * 辅助工具类
 * 将File对象转换成Thing对象
 */
//不让覆写不让随便new，普通工具类相当于函数
public final class FileConvertThing {//没法继承

    private FileConvertThing(){}//没法随便new了

    public static Thing convert(File file){
        Thing thing = new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getAbsolutePath());
        thing.setDepth(computeFileDepth(file));
        thing.setFileType(computeFileType(file));

        return thing;
    }
    private static int computeFileDepth(File file){
//        int dept = 0;
//        String[] segments = file.getAbsolutePath().split("\\\\");
//        dept = segments.length;
//        return dept;
        String[] segments = file.getAbsolutePath().split("\\\\");
        return segments.length;
    }
    private static FileType computeFileType(File file){
        if (file.isDirectory()){
            return FileType.OTHER;
        }
        int index = file.getName().lastIndexOf(".");
        //找到文件类型了，取扩展名
//        if (index != -1){
//            file.getName().substring(index+1);//存在越界问题,eg:abc.
//        }
        String fileName = file.getName();
        if (index != -1 && index < fileName.length() - 1){
            String extend = fileName.substring(index+1);
            return FileType.lookup(extend);
        }
        else {
            return FileType.OTHER;
        }
    }
}
