package com.frederic.project.libraryclient.models;

import java.io.Serializable;

/**
 * Created by fk101 on 2017/09/13.
 */

public class Administrator implements Serializable{

    private Integer id;
    private String name;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
