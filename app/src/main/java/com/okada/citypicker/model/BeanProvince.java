package com.okada.citypicker.model;

import java.util.List;

/**
 * 省
 */
public class BeanProvince {

	private String name;
	private int code;
	private List<BeanCity> list;

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

    public List<BeanCity> getList() {
        return list;
    }

    public void setList(List<BeanCity> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return name;
    }
}