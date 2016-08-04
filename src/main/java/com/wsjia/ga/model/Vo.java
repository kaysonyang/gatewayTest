package com.wsjia.ga.model;

import java.util.LinkedHashMap;

/**
 * Created by a on 2016/7/28.
 */
public class Vo {
    private String name;
    private String label;
    private Vos vos;
    private String url;
    private String model;
    private LinkedHashMap<String, Field> fieldMap;

    public Vo() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Vos getVos() {
        return vos;
    }

    public void setVos(Vos vos) {
        this.vos = vos;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LinkedHashMap<String, Field> getFieldMap() {
        return fieldMap;
    }

    public void setFieldMap(LinkedHashMap<String, Field> fieldMap) {
        this.fieldMap = fieldMap;
    }
}
