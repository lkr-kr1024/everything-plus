package com.bittech.everything.core.dao.impl;

import com.bittech.everything.core.dao.DataSourceFactory;
import com.bittech.everything.core.dao.FileIndexDao;
import com.bittech.everything.core.model.Condition;
import com.bittech.everything.core.model.FileType;
import com.bittech.everything.core.model.Thing;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FileIndexDaoImpl implements FileIndexDao {

    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //insert into file_index
            String sql = "insert into file_index(name,path,depth,file_type) values (?,?,?,?)";
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数 1 2 3 4
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepth());
            //FileType.DOC  ->  DOC
            statement.setString(4,thing.getFileType().name());
            //5.执行命令
            statement.executeUpdate();

        }catch (SQLException e){

        }finally {
           releaseResource(null,statement,connection);
        }
    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            String sql = "delete from file_index where path like '" + thing.getPath() + "%'";
            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数 1 2 3 4
            //5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseResource(null, statement, connection);
        }
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try{
            //1.获取数据库连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            //insert into file_index
            //String sql = "select name,path,depth,file_type from file_index";

            //name : like
            //filetype : = (不用模糊匹配)
            //limit : limit offset
            //orderbyasc : order by

            //sql拼接不用+，用StringBuilder(不用StringBuffer，虽然线程安全，但此处不存在多线程访问，不被多线程共享；
            // 但如果放在属性上就需要用StringBuffer，因为对象可能被多线程同时访问）
            //
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("select name,path,depth,file_type from file_index");
            //name匹配：前模糊，后模糊，前后模糊
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName()).append("%' ");
            //filetype可选的，有可能条件的filetype为空
            if (condition.getFileType() != null){
                sqlBuilder.append(" and file_type = '")
                        //filetype为String，且用户输入filetype可能为小写
                        .append(condition.getFileType().toUpperCase())
                        .append("' ");
            }
            //limit, order by必选的
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOrderByAsc() ? "asc" : "desc");
            sqlBuilder.append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");

            System.out.println(sqlBuilder);
            //3.准备命令
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数 1 2 3 4
            //5.执行命令
            resultSet = statement.executeQuery();
            //6.处理结果
            while (resultSet.next()){
                //把数据库中的行记录 ——> Java中的对象Thing
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));

                String fileType = resultSet.getString("file_type");
                //String类型的fileType转为枚举型对象
                //lookup中DOC是扩展名但不唯一，所以在FileType.java中加方法
                thing.setFileType(FileType.lookupByName(fileType));

                things.add(thing);
            }
        }catch (SQLException e){

        }finally {
            releaseResource(resultSet,statement,connection);
        }
        return things;
    }
    //解决内部代码大量重复问题：重构
    private void releaseResource(ResultSet resultSet,PreparedStatement
             statement,Connection connection){
        if (resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
