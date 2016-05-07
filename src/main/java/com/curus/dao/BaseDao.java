package com.curus.dao;


import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;


/**
 * Created by stupid-coder on 24/1/16.
 */
public class BaseDao<T> extends JdbcDaoSupport {
    public static final int SQL_INSERT = 0;
    public static final int SQL_UPDATE = 1;
    public static final int SQL_DELETE = 2;

    private Class<T> entityClass;
    protected String tableName;
    public BaseDao() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>)type.getActualTypeArguments()[0];
        tableName = entityClass.getSimpleName();
    }

    class JdbcArgsOut {
        String sql;
        List<Object> args;
    }

    private int processRs(int[] rs) {
        int total_rs = 0;
        for ( int r : rs ) total_rs += r;
        return total_rs;
    }

    public int update(T entity, String id) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        Map<String,Object> where = new HashMap<String, Object>();
        try {
            Field field = entityClass.getDeclaredField(id);
            field.setAccessible(true);
            where.put(id, field.get(entity));
            processSql(SQL_UPDATE, entity, where,jdbcArgsOut);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return getJdbcTemplate().update(jdbcArgsOut.sql, jdbcArgsOut.args.toArray());
    }

    public int insert(T entity) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        try {
            processSql(SQL_INSERT, entity, null, jdbcArgsOut);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return getJdbcTemplate().update(jdbcArgsOut.sql,jdbcArgsOut.args.toArray());
    }

    public int insert(List<T> entitys) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        List<Object[]> batch_args = new ArrayList<Object[]>();
        if ( entitys == null || entitys.size() == 0) return 0;
        else try {
            for ( T entity : entitys ) {
                processSql(SQL_INSERT,entity,null,jdbcArgsOut);
                batch_args.add(jdbcArgsOut.args.toArray());
            }
            return processRs(getJdbcTemplate().batchUpdate(jdbcArgsOut.sql,batch_args));
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int save(T entity, String id) {
        try {
            Field field = entityClass.getDeclaredField(id);
            field.setAccessible(true);
            if (field.get(entity) == null) return insert(entity);
            else return update(entity,id);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public T select(Map<String,Object> where) {
        List<T> rs = selectAll(where);
        if (rs == null) return null;
        else return rs.get(0);
    }

    public List<T> selectAll(Map<String,Object> where) {
        List<Object> args = new ArrayList<Object>();
        String whereSql = buildWhereSql(where,args);
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        List<T> rs = getJdbcTemplate().query(String.format("SELECT * FROM %s %s", tableName, whereSql), rowMapper, args.toArray());
        if (rs.isEmpty()) return null;
        else return rs;
    }

    public int delete(Map<String,Object> where) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        try {
            processSql(SQL_DELETE, null, where, jdbcArgsOut);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return getJdbcTemplate().update(jdbcArgsOut.sql,jdbcArgsOut.args.toArray());
    }

    protected void processSql(int SQLTYPE, T entity, Map<String,Object> where,JdbcArgsOut jdbcArgsOut) throws Exception {
        jdbcArgsOut.args = buildArgs(entity,where);
        jdbcArgsOut.sql = buildSql(SQLTYPE,entity,where) + buildWhereSql(where,jdbcArgsOut.args);
    }

    protected List<Object> buildArgs(T entity, Map<String,Object> where) throws Exception {
        List<Object> args = new ArrayList<Object>();
        Field[] fields = entityClass.getDeclaredFields();
        if (fields==null) return null;

        for (int i = 0; entity != null && i < fields.length; ++i) {
            fields[i].setAccessible(true);
            if (Modifier.isStatic(fields[i].getModifiers())) continue;

            String name = fields[i].getName();
            Object value = fields[i].get(entity);

            if (where != null && where.containsKey(name) ) continue;
            else if (name.compareTo("id") == 0 || value == null) continue;

            args.add(value);
        }
        return args;
    }

    protected String buildSql(int SQLTYPE, T entity, Map<String,Object> where) throws Exception {
        List<String> sqlFields = new ArrayList<String>();
        Field[] fields = entityClass.getDeclaredFields();
        if (fields==null) return null;

        for (int i = 0; entity != null && i < fields.length; ++i) {
            fields[i].setAccessible(true);

            if (Modifier.isStatic(fields[i].getModifiers())) continue;

            String name = fields[i].getName();
            Object value = fields[i].get(entity);

            if (where != null && where.containsKey(name) ) continue;
            else if (name.compareTo("id") == 0 || value == null) continue;

            switch (SQLTYPE) {
                case SQL_INSERT:
                    sqlFields.add(name); break;
                case SQL_UPDATE:
                    sqlFields.add(name + "=?"); break;
                default:
                    break;
            }
        }

        if ( SQLTYPE == SQL_INSERT ) return String.format("INSERT INTO %s(%s) VALUES(%s)", tableName, StringUtils.join(sqlFields, ","), StringUtils.stripEnd(StringUtils.repeat("?,", sqlFields.size()), ","));
        else if ( SQLTYPE == SQL_UPDATE ) return String.format("UPDATE %s SET %s", tableName, StringUtils.join(sqlFields,","));
        else return String.format("DELETE FROM %s",tableName);
    }

    protected String buildWhereSql(Map<String,Object> where, List<Object> args) {
        List<String> w = new ArrayList<String>();
        if ( where == null || where.size() == 0) {
            return "";
        }
        for ( Map.Entry<String,Object> entry : where.entrySet() ) {
            w.add(String.format(" %s = ? ",entry.getKey()));
            args.add(entry.getValue());
        }
        return String.format(" WHERE %s", StringUtils.join(w, "AND"));
    }
}
