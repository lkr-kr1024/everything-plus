package com.bittech.everything.config;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

//配置类，引用单例
@Getter
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

    /**
     * 检索最大的返回值数量
     */
    @Setter
    private Integer maxReturn = 30;

    /**
     * 深度排序的规则,默认是升序
     * order by dept asc limit 30 offset 0
     */
    @Setter
    private Boolean depthOrderAsc = true;

    /**
     * H2数据库文件路径
     */

    private void initDefaultPathsConfig(){
        //1.获取文件系统
        FileSystem fileSystem = FileSystems.getDefault();
        //遍历的目录

    }



    private EverythingPlusConfig(){}

    public static EverythingPlusConfig getInstance(){
        if (config == null){
            synchronized (EverythingPlusConfig.class){
                if (config == null){
                    config = new EverythingPlusConfig();

                    //1.获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();
                    //遍历的目录
                    Iterable<Path> iterable = fileSystem.getRootDirectories();
                    iterable.forEach(path -> config.getIncludePath().add(path.toString()));
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
            }
        }
        return config;
    }
}