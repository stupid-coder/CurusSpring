package com.curus.dao;

import com.alibaba.fastjson.JSONObject;
import com.curus.model.database.InternalData;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;


/**
 * Created by stupid-coder on 6/9/16.
 */
public class InternalDataDao extends BaseDao<InternalData> {

    public InternalDataDao(DataSource source) {
        this.setDataSource(source);
    }


    public InternalData select(Long patient_id, Integer cate) {
        RowMapper<InternalData> rowMapper = BeanPropertyRowMapper.newInstance(InternalData.class);

        List<InternalData> internalDataList = getJdbcTemplate().query(String.format("SELECT * FROM %s WHERE patient_id = ? AND cate = ?", tableName),
                rowMapper, patient_id, cate);
        if ( internalDataList.size() == 0 ) {
            return null;
        } else {
            return internalDataList.get(0);
        }
    }

    public void save(Long patient_id,Integer cate, JSONObject internal_data) {
        InternalData internalData = new InternalData();
        internalData.setCate(cate);
        internalData.setData(internal_data.toJSONString());
        if ( internal_data.containsKey("id") ) internalData.setId(internal_data.getLong("id"));
        else internalData.setPatient_id(patient_id);
        save(internalData,"id");
    }

}
