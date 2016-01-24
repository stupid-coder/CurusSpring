package com.curus.dao;

import com.curus.model.Account;
import com.curus.utils.TimeUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by stupid-coder on 23/1/16.
 */
public class AccountDao extends BaseDao<Account> {

    public boolean existsByPhone(String phone) {
        Number count = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM account WHERE phone=?",Integer.class,phone);
        return ( count != null && count.intValue() > 0 ? true : false );
    }

    public int insert(String phone, String passwd) {
        return getJdbcTemplate().update("INSERT account(phone,passwd,create_time) VALUES(?,?,?)", phone, passwd, TimeUtils.getTimestamp());
    }

    public Account selectByPhone(String phone) {
        return getJdbcTemplate().queryForObject("SELECT * FROM account WHERE phone=?", ParameterizedBeanPropertyRowMapper.newInstance(Account.class),phone);
    }

    public Account selectById(Long id) {
        return getJdbcTemplate().queryForObject("SELECT * FROM account WHERE id=?", ParameterizedBeanPropertyRowMapper.newInstance(Account.class),id);
    }

    public int updatePasswd(Account account) {
        return getJdbcTemplate().update("UPDATE account SET passwd=? WHERE id=?", Integer.class, account.getPasswd(), account.getId());
    }

    public int updatePhone(Account account) {
        return getJdbcTemplate().update("UPDATE account SET phone=? WHERE id=?", Integer.class, account.getPhone(), account.getId());
    }

//    public int update(Account account) {
//        List<String> sb = new ArrayList<String>();
//        List<Object> ob = new ArrayList<Object>();
//
//        if ( account.getName() != null) sb.add(String.format(" name = '%s' ", account.getName()));
//        if ( account.getGender() != null) sb.add(String.format(" gender = %d ", account.getGender()));
//        if ( account.getBirth() != null) sb.add(String.format(" birth = '%s' ", account.getBirth()));
//        if ( account.getId_number() != null) sb.add(String.format(" id_number = '%s' ", account.getId_number()));
//        if ( account.getAddress() != null) sb.add(String.format(" address = '%s' ", account.getAddress()));
//        if ( account.getOther_contact() != null) sb.add(String.format(" other_contact = '%s' ", account.getOther_contact()));
//        if ( account.getIs_exp_user() != null) sb.add(String.format(" is_exp_user = %d ", account.getIs_exp_user()));
//
//        String sql = String.format("UPDATE account SET %s WHERE id='%s'", StringUtils.join(sb, ","),account.getId());
//
//        return getJdbcTemplate().update(sql,Integer.class);
//
//    }
}
