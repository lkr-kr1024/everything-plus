package com.bittech.everything.core.index;

import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.impl.FileIndexDaoImpl;
import com.bittech.everything.core.index.impl.FileScanImpl;
import com.bittech.everything.core.interceptor.FileInterceptor;
import com.bittech.everything.core.interceptor.impl.FileIndexInterceptor;
import com.bittech.everything.core.interceptor.impl.FilePrintInterceptor;
import com.bittech.everything.core.model.Thing;

//public interface FileScan {
////    void insert(Thing thing);
//    //update
//    void index(String path);
//}

/**
 *面向接口编程
 */
public interface FileScan {

    /**
     * 遍历Path
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);

    public static void main(String[] args) {
        DataSourceFactory.initDatabase();
        FileScanImpl scan = new FileScanImpl();
        FileInterceptor printInterceptor = new FilePrintInterceptor();
        scan.interceptor(printInterceptor);

        FileIndexInterceptor fileIndexInterceptor = new
                FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));

        scan.interceptor(fileIndexInterceptor);
        scan.index("E:\\C++资料书\\C++进阶");
    }
}
