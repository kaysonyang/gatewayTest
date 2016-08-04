package com.wsjia.ga.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a on 2016/8/2.
 */
public interface GaService {
    public List<Object> PlistVo(List<Object> resultEntityList) throws Exception;
    public List<Object> NewPlistVo(Map<String, Object> conditions) throws Exception;
    public ArrayList listHeadService();
}
