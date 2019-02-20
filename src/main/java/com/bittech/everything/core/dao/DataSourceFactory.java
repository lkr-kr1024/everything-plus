package com.bittech.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.bittech.everything.config.EverythingPlusConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * new的对象可以反复new，意味着在堆上空间占用多，数据源对象不需要反复实例化（连的是一个数据库）
 * 选择工厂设计模式（不让用户随便实例化），所以再加单例；完成数据源初始化和数据库脚本执行
 *
 */
public class DataSourceFactory {

    /**
     * 数据源（单例）
     */
    private static volatile DruidDataSource dataSource;

    private DataSourceFactory() {

    }
    public static DataSource dataSource(){
        if (dataSource == null) {
            synchronized (DataSourceFactory.class){
                if (dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url   username    password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //JDBC规范中关于MySQL    jdbc:mysql://ip:port/databaseName

                    //获取当前工程路径//update： 此处是copy过来的,应该是读取来的，所以加到config里,
                    //这里就不要了
//                    String workDir = System.getProperty("user.dir");
                    //嵌入式
                    //JDBC规范中关于H2   jdbc:h2:filepath    ->存储到本地文件
                    //JDBC规范中关于H2   jdbc:h2:~/filepath    ->存储到当前用户的home目录

                    //JDBC规范中关于H2   jdbc:h2://ip:port/databaseName  ->存储到服务器
                    //
                    dataSource.setUrl("jdbc:h2:"+ EverythingPlusConfig.getInstance().getH2IndexPath());

                    //Druid数据库连接池的可配置参数
                    //解决：严重: testWhileIdle is true, validationQuery not set
                    //https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8
                    //第一种
                    dataSource.setValidationQuery("select now()");
//                    第二种
//                    dataSource.setTestWhileIdle(false);
                }
            }
        }
        return dataSource;
    }

//    public static void main(String[] args) {
//        //打印成功，表示获取数据源成功
//        DataSource dataSource = DataSourceFactory.dataSource();
//        System.out.println(dataSource);
//    }

    //初始化数据库，第一次启动和检查当前文件目录里有没有数据库文件，没有才初始化
    //在交互或者管理器初始化，此处在管理器处
    public static void initDatabase(){
        //1.获取数据源
        DataSource dataSource =
                DataSourceFactory.dataSource();
        //2.获取SQL语句
        //不采取读取绝对路径文件
        //E:\Project\everything-plus\target\classes\everything_plus.sql
        //采取读取classpath路径下的文件（获取classLoader）


//        InputStream in = DataSourceFactory.class.getClassLoader()
//                .getResourceAsStream("everything_plus.sql");
//        if (in == null){
//            throw new RuntimeException("Not read init database script please check it" );
//        }
//        //拿到流，把它变成字符串得到sql语句
//        BufferedReader reader = new BufferedReader(new InputStreamReader(in));


        //自动关闭流采取try-with-resource
        try(InputStream in = DataSourceFactory.class.getClassLoader()
                .getResourceAsStream("everything_plus.sql")){
            if (in == null){
                throw new RuntimeException("Not read init database script please check it" );
            }
            StringBuilder sqlBuilder = new StringBuilder();
            //拿到流，把它变成字符串得到sql语句
            try(BufferedReader reader = new BufferedReader(new InputStreamReader(in));){

                String line = null;
                while ((line = reader.readLine()) != null){
                    if (!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();
            //JDBC
            //3.1获取数据库连接
            Connection connection = dataSource.getConnection();
            //3.2创建命令
            PreparedStatement statement = connection.prepareStatement(sql);
            //3.3执行SQL
            statement.execute();

            connection.close();
            statement.close();

        }
        catch (IOException | SQLException e){
            e.printStackTrace();
        }
    }

}
