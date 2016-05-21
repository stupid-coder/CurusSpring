package com.curus.httpio.response.im;

import java.util.List;

/**
 * Created by stupid-coder on 5/21/16.
 */
public class ImListResponseData {

    public class ImListItem {
        private String name;
        private Integer is_self;
        private String acid;

        public ImListItem(String name, Integer is_self, String acid) {
            this.name = name;
            this.is_self = is_self;
            this.acid = acid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getIs_self() {
            return is_self;
        }

        public void setIs_self(Integer is_self) {
            this.is_self = is_self;
        }

        public String getAcid() {
            return acid;
        }

        public void setAcid(String acid) {
            this.acid = acid;
        }

        @Override
        public String toString() {
            return "ImListItem{" +
                    "name='" + name + '\'' +
                    ", is_self=" + is_self +
                    ", acid='" + acid + '\'' +
                    '}';
        }
    }

    private List<ImListItem> contacts;

    public List<ImListItem> getContacts() {
        return contacts;
    }

    public void setContacts(List<ImListItem> contacts) {
        this.contacts = contacts;
    }

    @Override
    public String toString() {
        return "ImListResponseData{" +
                "contacts=" + contacts +
                '}';
    }
}
