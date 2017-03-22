package org.smart4j.framework.helper;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smart4j.framework.utils.CollectionUtil;
import org.smart4j.framework.utils.PropsUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * Created by DP on 2016/10/20.
 */
public class DatabaseHelper {
    private static final ThreadLocal<Connection> CONNECTION_HOLDER;
    private static final Logger LOGGER= LoggerFactory.getLogger(DatabaseHelper.class);
    private static final QueryRunner QUERY_RUNNER;
    private static final BasicDataSource DATA_SOURCE;

    /**
     * 初始化数据源
     */
    static {
        CONNECTION_HOLDER=new ThreadLocal<Connection>();
        QUERY_RUNNER=new QueryRunner();
        Properties conf= PropsUtil.loadProps("config.properties");
        String driver=conf.getProperty("jdbc.driver");
        String url=conf.getProperty("jdbc.url");
        String username=conf.getProperty("jdbc.username");
        String password=conf.getProperty("jdbc.password");

        DATA_SOURCE=new BasicDataSource();
        DATA_SOURCE.setDriverClassName(driver);
        DATA_SOURCE.setUrl(url);
        DATA_SOURCE.setUsername(username);
        DATA_SOURCE.setPassword(password);

    }


    /**
     * 获取数据库连接
     * @return
     */
    public static Connection getConnection(){
        Connection conn=CONNECTION_HOLDER.get();
        if(conn==null){
            try {
                conn=DATA_SOURCE.getConnection();
            } catch (SQLException e) {
                LOGGER.error("get connection failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
        return conn;
    }



    /**
     * 查询实体列表
     * @param entityClass
     * @param sql
     * @param <T>
     * @return
     */
    public static <T> List<T> queryEntityList(Class<T> entityClass,String sql,Object... params){
        List<T> entityList;
        try {
            Connection conn=getConnection();
            entityList=QUERY_RUNNER.query(conn,sql,new BeanListHandler<T>(entityClass),params);
        } catch (SQLException e) {
            LOGGER.error("query entity list failure",e);
            throw new RuntimeException(e);
        }
        return entityList;
    }


    /**
     * 查询实体
     * @param entityClass
     * @param sql
     * @param params
     * @param <T>
     * @return
     */
    public static <T> T queryEntity(Class<T> entityClass,String sql,Object... params){
        T entity;
        Connection conn=getConnection();
        try {
            entity=QUERY_RUNNER.query(conn,sql,new BeanHandler<T>(entityClass),params);
        } catch (SQLException e) {
            LOGGER.error("query entity  failure",e);
            throw new RuntimeException(e);
        }
        return entity;
    }


    /**
     * 执行查询语句
     * @param sql
     * @param params
     * @return
     */
    public static List<Map<String,Object>> executeQuery(String sql, Object... params){
        List<Map<String,Object>> result;
        Connection conn=getConnection();
        try {
            result=QUERY_RUNNER.query(conn,sql,new MapListHandler(),params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw  new RuntimeException(e);
        }
        return result;
    }

    /**
     * 执行更新语句(包括插入，更新，删除)
     * @param sql
     * @param params
     * @return
     */
    public static int executeUpdate(String sql,Object... params){
        int rows=0;
        Connection conn=getConnection();
        try {
            rows=QUERY_RUNNER.update(conn,sql,params);
        } catch (SQLException e) {
            LOGGER.error("execute query failure", e);
            throw  new RuntimeException(e);
        }
        return rows;
    }

    /**
     * 得到表名
     * @param entityClass
     * @return
     */
    private static String getTableName(Class<?> entityClass){
        return   entityClass.getSimpleName();
    }

    /**
     * 插入实体
     * @param entityClass
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static <T> boolean insertEntity(Class<T> entityClass,Map<String,Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can  not insert entity:fielsMap is empty");
            return false;
        }
        String sql="INSERT INTO " + getTableName(entityClass);
        StringBuilder columns=new StringBuilder("(");
        StringBuilder values=new StringBuilder("(");
        for(String fieldName:fieldMap.keySet()){
            columns.append(fieldName).append(", ");
            values.append("?, ");
        }
        columns.replace(columns.lastIndexOf(", "),columns.length(),")");//将最后一个“， ”替换成“)”
        values.replace(values.lastIndexOf(", "),values.length(),")");
        sql+=columns + " VALUES " + values;
        Object[] params=fieldMap.values().toArray();
        return executeUpdate(sql,params) == 1;//返回一行被修改
    }

    /**
     * 更新实体
     * @param entityClass
     * @param id
     * @param fieldMap
     * @param <T>
     * @return
     */
    public static <T> boolean updateEntity(Class<T> entityClass,long id,Map<String,Object> fieldMap){
        if(CollectionUtil.isEmpty(fieldMap)){
            LOGGER.error("can  not insert entity:fielsMap is empty");
            return false;
        }
        String sql="UPDATE " + getTableName(entityClass) + " SET";
        StringBuilder columns=new StringBuilder();
        for (String fieldName:fieldMap.keySet()){
            columns.append(fieldName).append("=?, ");
        }
        sql+=columns.substring(0,columns.lastIndexOf(", ")) + " WHERE id=?";//拼凑sql语句
        List<Object>  paramsList=new ArrayList<Object>();
        paramsList.addAll(fieldMap.values());
        paramsList.add(id);
        Object[] params=paramsList.toArray();
        return executeUpdate(sql,params)==1;
    }

    /**
     * 删除实体
     * @param entityClass
     * @param id
     * @param <T>
     * @return
     */
    public static <T> boolean deleteEntity(Class<T> entityClass,long id){
        String sql ="DELETE FROM " + getTableName(entityClass) + " WHERE id=?";
        return executeUpdate(sql,id)==1;
    }

    /**
     * 执行SQL文件
     * @param filePath
     */
    public static void executeSqlFile(String filePath){
        InputStream is=Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        BufferedReader reader=new BufferedReader((new InputStreamReader(is)));
        try {
            String sql;
            while((sql=reader.readLine())!= null){
                executeUpdate(sql);
            }
        } catch (IOException e) {
            LOGGER.error("execute sql file failure",e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 开启事务
     * 注意，默认是自动提交事务，所以需要将提交属性设置为false，在开启事务完毕之后，需要将Connection对象放入
     * 本地线程变量池中，当事务提交或者回滚后，需要删除本地线程变量池中的本事务连接
     */
    public static void beginTransaction(){
        Connection conn=getConnection();
        if(conn!=null){
            try {
                conn.setAutoCommit(false);
            }catch (SQLException e) {
                LOGGER.error("begin transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.set(conn);
            }
        }
    }

    /**
     * 提交事务
     */
    public static void commitTransaction(){
        Connection  conn=getConnection();
        if (conn!=null){
            try {
                conn.commit();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("commit transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }

    /**
     * 回滚事务
     */
    public static void rollbackTransaction(){
        Connection conn=getConnection();
        if (conn!=null){
            try {
                conn.rollback();
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("rollback transaction failure",e);
                throw new RuntimeException(e);
            }finally {
                CONNECTION_HOLDER.remove();
            }
        }
    }
}
