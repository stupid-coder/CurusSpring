package com.curus.dao;


import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by stupid-coder on 24/1/16.
 */
public class BaseDao<T> extends JdbcDaoSupport {
    public static final int SQL_INSERT = 0;
    public static final int SQL_UPDATE = 1;
    public static final int SQL_DELETE = 2;

    private Class<T> entityClass;

    public BaseDao() {
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        entityClass = (Class<T>)type.getActualTypeArguments()[0];
    }

    class JdbcArgsOut {
        String sql;
        Object[] args;
    }

    public int update(T entity) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        try {
            processSql(SQL_UPDATE, entity, "id", jdbcArgsOut);
        } catch (Exception e) {
            return 0;
        }
        return getJdbcTemplate().update(jdbcArgsOut.sql, jdbcArgsOut.args);
    }

    public int insert(T entity) {
        JdbcArgsOut jdbcArgsOut = new JdbcArgsOut();
        try {
            processSql(SQL_INSERT, entity, null, jdbcArgsOut);
        } catch (Exception e) {
            return 0;
        }
        return getJdbcTemplate().update(jdbcArgsOut.sql,jdbcArgsOut.args);
    }

    public T select(Long id) {
        String sql = String.format("SELECT * FROM %s WHERE %s=?",entityClass.getSimpleName(),id);
        RowMapper<T> rowMapper = BeanPropertyRowMapper.newInstance(entityClass);
        List<T> rs = getJdbcTemplate().query(sql,rowMapper,id);
        if (rs.isEmpty())return null;
        else return rs.get(0);
    }

    protected void processSql(int SQLTYPE, T entity, String where,JdbcArgsOut jdbcArgsOut) throws Exception {
        List<String> sql = new ArrayList<String>();
        List<Object> args = new ArrayList<Object>();
        Field[] fields = entityClass.getDeclaredFields();
        Object whereObj = null;
        if (fields==null) return;

        for (int i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);

            if (Modifier.isStatic(fields[i].getModifiers())) continue;

            String name = fields[i].getName();
            Object value = fields[i].get(entity);

            if (where != null && name.compareTo(where) == 0) { whereObj = value; continue;}
            else if (name.compareTo("id") == 0 || value == null) continue;

            switch (SQLTYPE) {
                case SQL_INSERT:
                    sql.add(name); args.add(value);
                    break;
                case SQL_UPDATE:
                    sql.add(name + "=?"); args.add(value);
                    break;
                default:
                    break;
            }
        }
        if (whereObj!=null) args.add(whereObj);

        jdbcArgsOut.sql = buildSql(SQLTYPE,entityClass.getSimpleName(),sql) + buildWhereSql(where);
        jdbcArgsOut.args = args.toArray();
    }

    protected String buildSql(int SQLTYPE, String table, List<String> items) {
        if ( SQLTYPE == SQL_INSERT ) return String.format("INSERT INTO %s (%s) VALUES (%s) ", table, StringUtils.join(items, ","), StringUtils.stripEnd(StringUtils.repeat("?,", items.size()), ","));
        else if ( SQLTYPE == SQL_UPDATE ) return String.format("UPDATE %s SET %s", table, StringUtils.join(items,","));
        else return String.format("DELETE FROM %s",table);
    }

    protected String buildWhereSql(String where) {
        if (where!=null && !where.isEmpty()) {
            return String.format("WHERE %s=?",where);
        } else return "";
    }
}
