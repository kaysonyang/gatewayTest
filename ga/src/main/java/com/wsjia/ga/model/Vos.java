package com.wsjia.ga.model;

import java.util.LinkedHashMap;

/**
 * Created by a on 2016/7/28.
 */
public class Vos {
    private String name;
    private String model;
    private LinkedHashMap<String, Vo> voMap;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LinkedHashMap<String, Vo> getVoMap() {
        return voMap;
    }

    public void setVoMap(LinkedHashMap<String, Vo> voMap) {
        this.voMap = voMap;
    }
}
