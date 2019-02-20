package com.bittech.everything.config;

import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

//配置类，引用单例
//getter保证属性includePath，excludePath只被获取不被修改
@Getter
//2/17:search的可配置，param可修改
//@Setter
public class EverythingPlusConfig {
    private static volatile EverythingPlusConfig config;

    /**
     * 建立索引的路径
     */
    private Set<String> includePath = new HashSet<>();

    /**
     * 排除索引文件的路径
     */
    private Set<String> excludePath = new HashSet<>();

    //TODO 可配置的参数
///////////////////////////////////////////////////
    //此时可以配置，但值还是固定的，值可变需要通过main args让用户传入
    /**
     * 检索最大的返回值数量
     */
    //2/17/9:46,不给includePath和excludePath   setter
    @Setter
    private Integer maxReturn = 30;

    /**
     * 深度排序的规则,默认是升序
     * order by dept asc limit 30 offset 0
     */
    @Setter
    private Boolean depthOrderAsc = true;
///////////////////////////////////////////////////
    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir")
            + File.separator + "everything_plus";

    private EverythingPlusConfig(){
    }

    private void initDefaultPathsConfig(){

        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录
        Iterable<Path> iterable = fileSystem.getRootDirectories();
        iterable.forEach(path -> config.includePath.add(path.toString()));
        //排除的目录
        //Windows   C:\Windows  C:\ProgramData  C:\Program Files
        //Linux /tmp    /etc    /root
        //Unix
        String osname = System.getProperty("os.name");
        if (osname.startsWith("Windows")){
            config.getExcludePath().add("C:\\Windows");
            config.getExcludePath().add("C:\\ProgramData");
            config.getExcludePath().add("C:\\Program Files");
        }else {
            config.getExcludePath().add("/tmp");
            config.getExcludePath().add("/etc");
            config.getExcludePath().add("/root");
        }

    }


    public static EverythingPlusConfig getInstance(){
        if (config == null){
            synchronized (EverythingPlusConfig.class){
                if (config == null){
                    config = new EverythingPlusConfig();
                    //单运行cmdapp时，NullPointerException
                    //解决：改成饿汉式或者如下
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }
}
