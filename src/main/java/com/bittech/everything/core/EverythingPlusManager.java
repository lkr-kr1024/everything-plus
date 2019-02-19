package com.bittech.everything.core;

import com.bittech.everything.config.EverythingPlusConfig;
import com.bittech.everything.core.common.HandlePath;
import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.FileScan;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.interceptor.impl.ThingClearInterceptor;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.monitor.FileWatch;
import com.bittech.everything.core.monitor.impl.FileWatchImpl;
import com.bittech.everything.core.search.FileSearch;
import com.bittech.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EverythingPlusManager {

    private static volatile EverythingPlusManager manager;
    private FileSearch fileSearch;
    private FileScan fileScan;

    //线程池中产生多个线程
    private ExecutorService executorService;
//
//    public EverythingPlusManager(FileSearch fileSearch, FileScan fileScan) {
//        this.fileSearch = fileSearch;
//        this.fileScan = fileScan;
//    }
//第5个录屏中断后
    /**
     * 清理删除的文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);

    /**
     * 文件监控
     */
//    private FileWatch fileWatch;

    private EverythingPlusManager() {
        this.initComponent();
    }

    private void initComponent() {
        //数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
//拿到数据源对象后，初始化数据库，首先检查当前工作目录下有没有数据库，有就不该重复初始化

        /**
         * 检查数据库
         */
//        checkDatabase();
        initOrResetDatabase();

        //业务层的对象
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);

        this.fileSearch = new FileSearchImpl(fileIndexDao);

        this.fileScan = new FileScanImpl();
//第6个视频12点重构
        //在真正发布代码的时候是不需要的
//        this.fileScan.interceptor(new FilePrintInterceptor());
        this.fileScan.interceptor(new FileIndexInterceptor(fileIndexDao));
//重构时没有下面代码
        this.thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        //清理默认线程用户线程，需要把它变成守护线程，程序退出时它也停止
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);

        //文件监控对象
//        this.fileWatch = new FileWatchImpl(fileIndexDao);

    }

//    private void checkDatabase() {
//        String workDir = System.getProperty("user.dir");
//        String fileName = EverythingPlusConfig.getInstance().getH2IndexPath() + File.separator+".mv.db";
//        File dbFile = new File(fileName);
//        if (dbFile.isFile() && !dbFile.exists()){
//            DataSourceFactory.initDatabase();
//        }
//    }

    //第5个录屏中断后
    public void initOrResetDatabase() {
        DataSourceFactory.initDatabase();
    }

    public static EverythingPlusManager getInstance() {
        if (manager == null) {
            synchronized(EverythingPlusManager.class) {
                if (manager == null) {
                    manager = new EverythingPlusManager();
                }
            }
        }
        return manager;
    }
//第5个录屏中断，12点之后的扩展
    /**
     * 检索（客户端只需要调用这个方法即可）
     */
    public List<Thing> search(Condition condition) {
        //Stream 流式处理 JDK8
        return this.fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //做删除
                        //生产者-消费者
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;

                }).collect(Collectors.toList());
    }

    /**
     * 索引
     */
    public void buildIndex(){
        Set<String> directories = EverythingPlusConfig.getInstance().getIncludePath();

        //同步操作，C->D->E-...
//        for (String path:directories) {
//            this.fileScan.index("path");
//        }
        if (this.executorService == null){
            this.executorService = Executors.newFixedThreadPool
                    (directories.size(), new ThreadFactory() {

                private final AtomicInteger threadId = new
                        AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }

        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());

        System.out.println("Build index start...");
        for (String path:directories){
            this.executorService.submit(() -> {
                EverythingPlusManager.this.fileScan.index(path);
                //当前任务完成，值减一
                countDownLatch.countDown();
            });
        }
        /**
         * 阻塞，直到任务完成，值为0
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Build index complete...");
        //多线程判断构建索引完成CountDownLatch类
        //或者自己加标记，死循环完成一个减一，此处采用jdk提供的类
    }

    //第5个录屏中断后，12：28
    /**
     * 启动清理线程
     */
//    public void startBackgroundClearThread() {
//        if (this.backgroundClearThreadStatus.get()) {
//            System.out.println("Cant repeat start BackgroundClearThread");
//        } else {
    //多线程下存在资源竞争,改成下面更原子的操作
//            this.backgroundClearThread.start();
//            this.backgroundClearThreadStatus.set(false);
//        }
//    }
    public void startBackgroundClearThread() {
        //main中不能启动多次，所有加标记，对应上边private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);

        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("Cant repeat start BackgroundClearThread");
        }
    }

    /**
     * 启动文件系统监听
     */
//    public void startFileSystemMonitor() {
//        EverythingPlusConfig config = EverythingPlusConfig.getInstance();
//        HandlePath handlePath = new HandlePath();
//        handlePath.setIncludePath(config.getIncludePath());
//        handlePath.setExcludePath(config.getExcludePath());
//        this.fileWatch.monitor(handlePath);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("文件系统监控启动");
//                fileWatch.start();
//            }
//        }).start();
//    }
//}
}