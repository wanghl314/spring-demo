package com.whl.spring.bean;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;


public class FileInfo implements Serializable {
    @JsonProperty("Name")
    private String name;

    @JsonProperty("Size")
    private long size;

    @JsonProperty("Last modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastModified;

    public FileInfo() {

    }

    public FileInfo(String name, long size, Date lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = lastModified;
    }

    public FileInfo(String name, long size, long lastModified) {
        this.name = name;
        this.size = size;
        this.lastModified = new Date(lastModified);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

}
