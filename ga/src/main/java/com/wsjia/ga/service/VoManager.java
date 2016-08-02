package com.wsjia.ga.service;


import com.wsjia.ga.model.Vo;
import com.wsjia.ga.model.Vos;

import java.util.Map;

/**
 * Created by a on 2016/7/28.
 */
public interface VoManager {
    public Map<String, Vos> fetchVosMap();

    public Map<String, Vo> fetchVoMap();

    public Vos fetchVos(String vosName);

    public Vo fetchVo(String voName);
}
