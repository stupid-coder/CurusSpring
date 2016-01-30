package com.curus.model;

import com.curus.utils.TimeUtils;
import com.curus.utils.constant.CommonConst;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by stupid-coder on 26/1/16.
 *
 *
 `id` int(11) NOT NULL AUTO_INCREMENT,
 `create_time` datetime NOT NULL,
 `mfrom` varchar(20) NOT NULL,
 `mto_id` int(11) DEFAULT NULL,
 `title` varchar(20) NOT NULL,
 `content` longtext NOT NULL,
 `extra` longtext,
 `read` tinyint(1) NOT NULL,
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Timestamp create_time;
    private String mfrom;
    private Long mto_id;
    private String title;
    private String content;
    private String extra;
    private Integer read;

    public Message(String mfrom, Long mto_id, String title, String content, String extra) {
        this.create_time = TimeUtils.getTimestamp();
        this.mfrom = mfrom;
        this.mto_id = mto_id;
        this.title = title;
        this.content = content;
        this.extra = extra;
        this.read = CommonConst.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Timestamp create_time) {
        this.create_time = create_time;
    }

    public String getMfrom() {
        return mfrom;
    }

    public void setMfrom(String mfrom) {
        this.mfrom = mfrom;
    }

    public Long getMto_id() {
        return mto_id;
    }

    public void setMto_id(Long mto_id) {
        this.mto_id = mto_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getRead() {
        return read;
    }

    public void setRead(Integer read) {
        this.read = read;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", create_time=" + create_time +
                ", mfrom='" + mfrom + '\'' +
                ", mto_id=" + mto_id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", extra='" + extra + '\'' +
                ", read=" + read +
                '}';
    }
}
