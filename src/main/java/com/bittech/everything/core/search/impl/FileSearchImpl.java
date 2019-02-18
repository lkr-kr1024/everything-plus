package com.bittech.everything.core.search.impl;

import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.Thing;
import com.bittech.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务层
 */
//public class FileSearchImpl implements FileSearch {
//
//    private final DataSource dataSource;
//
//    //不让类与类耦合，不用DataSourceFactory.getDataSource获得，解耦操作
//    public FileSearchImpl(DataSource dataSource) {
//        this.dataSource = dataSource;
//    }
//
//    @Override
//    public List<Thing> search(Condition condition) {
//        //TODO
//        //数据库的处理逻辑
//        //实现检索，和数据库关联，需要一个操作数据库的对象，那么需要连接
//        //从DataSourceFactory中传入一个datasource
//        //怎么传？类依赖了数据源，那么在类中定义一个datasource的属性，初始化通过构造方法完成
//
//        //拿到数据源，创建连接->创建命令->准备SQL语句->执行->结构返回->处理结果->包装成Thing返回
//        return new ArrayList<>();
//    }
//}

//update
public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        return this.fileIndexDao.search(condition);
    }
}
