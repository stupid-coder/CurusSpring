package com.curus.dao.account;

import com.curus.dao.BaseDao;
import com.curus.model.Account;
import com.curus.utils.TimeUtils;
import com.curus.utils.TypeUtils;
import com.curus.utils.constant.CommonConst;

/**
 * Created by stupid-coder on 23/1/16.
 */

public class AccountDao extends BaseDao<Account> {

    public boolean existsByPhone(String phone) {
        Number count = getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM account WHERE phone=?",Integer.class,phone);
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
        return getJdbcTemplate().update("UPDATE account SET passwd=? WHERE id=?", Integer.class, account.getPasswd(), account.getId());
    }

    public int updatePhone(Account account) {
        return getJdbcTemplate().update("UPDATE account SET phone=? WHERE id=?", Integer.class, account.getPhone(), account.getId());
    }

}
