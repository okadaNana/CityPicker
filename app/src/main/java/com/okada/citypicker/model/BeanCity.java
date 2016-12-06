package com.okada.citypicker.model;

import java.util.List;

/**
 * 市
 */
public class BeanCity {

	private String name;
	private int code;
	private List<BeanDistrict> list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<BeanDistrict> getList() {
        return list;
    }

    public void setList(List<BeanDistrict> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return name;
    }
}