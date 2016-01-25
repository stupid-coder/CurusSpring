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
        Map<String,Object> where = new HashMap<String, Object>();
        try {
            where.put("id", entityClass.getDeclaredField("id").get(entity));
            processSql(SQL_UPDATE, entity, where,jdbcArgsOut);
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

    public int save(T entity) {
        try {
            if (entityClass.getDeclaredField("id").get(entity) == null) return insert(entity);
            else return update(entity);

        } catch (Exception e) {
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
        List<T> rs = getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE", entityClass.getSimpleName(), whereSql), rowMapper, args.toArray());
        if (rs.isEmpty()) return null;
        else return rs;
    }

    protected void processSql(int SQLTYPE, T entity, Map<String,Object> where,JdbcArgsOut jdbcArgsOut) throws Exception {
        List<String> sql = new ArrayList<String>();
        List<Object> args = new ArrayList<Object>();
        Field[] fields = entityClass.getDeclaredFields();
        if (fields==null) return;

        for (int i = 0; i < fields.length; ++i) {
            fields[i].setAccessible(true);

            if (Modifier.isStatic(fields[i].getModifiers())) continue;

            String name = fields[i].getName();
            Object value = fields[i].get(entity);

            if (where != null && where.containsKey(name) ) { continue; }
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

        jdbcArgsOut.sql = buildSql(SQLTYPE,entityClass.getSimpleName(),sql) + buildWhereSql(where,args);
        jdbcArgsOut.args = args.toArray();
    }

    protected String buildSql(int SQLTYPE, String table, List<String> items) {
        if ( SQLTYPE == SQL_INSERT ) return String.format("INSERT INTO %s (%s) VALUES (%s) ", table, StringUtils.join(items, ","), StringUtils.stripEnd(StringUtils.repeat("?,", items.size()), ","));
        else if ( SQLTYPE == SQL_UPDATE ) return String.format("UPDATE %s SET %s", table, StringUtils.join(items,","));
        else return String.format("DELETE FROM %s",table);
    }

    protected String buildWhereSql(Map<String,Object> where, List<Object> args) {
        List<String> w = new ArrayList<String>();
        w.add(" 1=? ");
        args.add(new Integer(1));
        for ( Map.Entry<String,Object> entry : where.entrySet() ) {
            w.add(String.format(" %s = ? ",entry.getKey()));
            args.add(entry.getValue());
        }
        return StringUtils.join(w,"AND");
    }
}
