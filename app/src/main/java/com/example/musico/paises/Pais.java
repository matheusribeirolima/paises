package com.example.musico.paises;

import android.graphics.Bitmap;

import java.io.Serializable;

public class Pais implements Serializable{
    private Integer id;
    private String shortname;
    private String longname;
    private String callingCode;
    private Bitmap flag;
    private String data;

    public Pais() {}

    public Pais(Integer id, String shortname, String longname, String callingCode, Bitmap flag, String data) {
        this.id = id;
        this.shortname = shortname;
        this.longname = longname;
        this.callingCode = callingCode;
        this.flag = flag;
        this.data = data;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getShortname() {
        return shortname;
    }

    public void setShortname(String shortname) {
        this.shortname = shortname;
    }

    public String getLongname() {
        return longname;
    }

    public void setLongname(String longname) {
        this.longname = longname;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }

    public Bitmap getFlag() { return flag; }

    public void setFlag(Bitmap flag) { this.flag = flag; }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
