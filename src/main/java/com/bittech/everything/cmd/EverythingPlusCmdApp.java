package com.bittech.everything.cmd;

import com.bittech.everything.config.EverythingPlusConfig;
import com.bittech.everything.core.EverythingPlusManager;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;

import java.util.List;
import java.util.Scanner;

public class EverythingPlusCmdApp {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        //解析用户参数
       // parseParams(args);

        //欢迎
        welcome();

        //统一调度器
        EverythingPlusManager manager = EverythingPlusManager.getInstance();

        //启动后台清理线程
        manager.startBackgroundClearThread();

        //启动监控
//        manager.startFileSystemMonitor();

        //交互式
        //interactive(manager);

    }



//    private static void search(EverythingPlusManager manager, Condition condition) {
//        //name fileType limit orderByAsc
//        condition.setLimit(EverythingPlusConfig.getInstance().getMaxReturn());
//        condition.setOrderByAsc(EverythingPlusConfig.getInstance().getDeptOrderAsc());
//        List<Thing> thingList = manager.search(condition);
//        for (Thing thing : thingList) {
//            System.out.println(thing.getPath());
//        }
//
//    }

    private static void index(EverythingPlusManager manager) {
        //统一调度器中的index
        new Thread(manager::buildIndex).start();
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }

    private static void welcome() {
        System.out.println("欢迎使用，Everything Plus");
    }

    private static void help() {
        System.out.println("命令列表：");
        System.out.println("退出：quit");
        System.out.println("帮助：help");
        System.out.println("索引：index");
        System.out.println("搜索：search <name> [<file-Type> img | doc | bin | archive | other]");
    }

}
