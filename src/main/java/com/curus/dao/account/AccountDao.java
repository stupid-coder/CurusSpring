package com.curus.dao.account;

import com.curus.dao.BaseDao;
import com.curus.model.database.Account;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;

/**
 * Created by stupid-coder on 23/1/16.
 */

public class AccountDao extends BaseDao<Account> {

    public boolean existsByPhone(String phone) {
        Number count = getJdbcTemplate().queryForObject(String.format("SELECT COUNT(*) FROM %s WHERE phone=?",tableName),Integer.class,phone);
        return ( count != null && count.intValue() > 0 ? true : false );
    }

    public int insert(String phone, String passwd) {
        Account account = new Account(phone, null, passwd,  null, null,
                null, null, null, null, TimeUtils.getTimestamp());
        return insert(account);
    }

    public Account selectByPhone(String phone) {
        return select( TypeUtils.getWhereHashMap("phone", phone) );
    }

    public Account selectById(Long id) {
        return select(TypeUtils.getWhereHashMap("id", id));
    }

    public int updatePasswd(Account account) {
        return getJdbcTemplate().update(String.format("UPDATE %s SET passwd=? WHERE id=?",tableName), account.getPasswd(), account.getId());
    }

    public int updatePhone(Account account) {
        return getJdbcTemplate().update(String.format("UPDATE %s SET phone=? WHERE id=?",tableName), account.getPhone(), account.getId());
    }

    public boolean checkByIdNumber(String id_number) {
        return select(TypeUtils.getWhereHashMap("id_number",id_number)) != null;
    }

}
