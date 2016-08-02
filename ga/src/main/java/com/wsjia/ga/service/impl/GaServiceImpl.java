package com.wsjia.ga.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsjia.ga.model.Field;
import com.wsjia.ga.model.Vo;
import com.wsjia.ga.model.Vos;
import com.wsjia.ga.service.GaService;
import com.wsjia.ga.service.VoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by a on 2016/8/2.
 */
@Service
public class GaServiceImpl implements GaService {

    @Autowired
    VoManager voManager;

    @Override
    public String PlistVo(String json) throws Exception{
        //取到vo对象
        Vos vos = voManager.fetchVos("vomodel");
        String model = vos.getModel();
        Vo vo = null;
        for (String key:vos.getVoMap().keySet()) {
            if (vos.getVoMap().get(key) != null) {
                vo = vos.getVoMap().get(key);
                break;
            }
        }
        //解析json，获取查询条件信息
        ObjectMapper objectMapper = new ObjectMapper();
        LinkedHashMap<String, Object> jsonMap = objectMapper.readValue(json, LinkedHashMap.class);
        //查询的url
        String url = (String) vo.getUrl();
        //查询条件组合的ID
        String qmsid = (String) jsonMap.get("qmsid");
        //当前页
        String currentPage = (String) jsonMap.get("currentPage");
        //每页多少行
        String pageSize = (String) jsonMap.get("pageSize");
        //参数列表
        LinkedHashMap<String,String> params = (LinkedHashMap<String, String>)jsonMap.get("params");
        //调用微服务获取到查询结果JSON串
        String resultJson = DoMSList(url + "/plist" + model, qmsid, currentPage, pageSize, params);
        ArrayList<LinkedHashMap<String, Object>> resultJsonList = objectMapper.readValue(resultJson, ArrayList.class);
        //存储需要去其他微服务查询的属性
        ArrayList<Field> remoteFields = new ArrayList<>();
        //用来存储最终将返回的vo数据集合
        ArrayList<LinkedHashMap<String, String>> resultVoList = new ArrayList<>();
        //将查询结果列表转换成VO对象列表的json
        for (LinkedHashMap<String, Object> linkedHashMap:resultJsonList) {
            //用来存储一条vo数据，包含多对属性:值
            LinkedHashMap<String, String> voValue = new LinkedHashMap<>();
            for (String key:vo.getFieldMap().keySet()) {
                Field field = vo.getFieldMap().get(key);
                //如果没有url属性，则说明field是此微服务查询的内容
                if (field.getUrl() == null || "".equals(field.getUrl())){
                    //如果source有“.”，说明是嵌套的属性，需做处理
                    String[] source = field.getSource().split("\\.");
                    if(source.length > 1){
                        Map map = (Map) linkedHashMap.get(source[0]);
                        voValue.put(field.getName(), (String) map.get(source[1]));
                    }else{
                        voValue.put(field.getName(), (String) linkedHashMap.get(field.getSource()));
                    }
                }else{
                    remoteFields.add(field);
                }
            }
            resultVoList.add(voValue);
        }
        //查询需要去其他微服务获取的数据
        if (remoteFields.size() > 0) {
            for (LinkedHashMap<String, String> tempVo:resultVoList) {
                //遍历需要去其他表查询的属性
                for (Field remoteField:remoteFields){
                    String name = remoteField.getName();
                    String remoteFieldUrl = remoteField.getUrl();
                    String[] key = remoteField.getKey().split("=");
                    String[] source = remoteField.getSource().split("\\.");
                    String hql = "FROM " + source[0] + " WHERE " + key[0] + "=:" + key[1];
                    LinkedHashMap<String, Object> queryParamMap = new LinkedHashMap<>();
                    queryParamMap.put(key[1], tempVo.get(key[1]));
                    String remoteJsonResult = DoMSFind(remoteFieldUrl, hql, queryParamMap);
                    LinkedHashMap<String, Object> remoteJsonResultMap = objectMapper.readValue(remoteJsonResult, LinkedHashMap.class);
                    tempVo.put(name, (String) remoteJsonResultMap.get(source[1]));
                }
            }
        }
        return objectMapper.writeValueAsString(resultVoList);
    }

    //伪造调用微服务
    private String DoMSList (String url, String qmsid, String currentPage, String pageSize, Map<String, String> params) throws Exception{
        List<Map<String,Object>> list = new ArrayList<>();
        for (int i=0;i<2;i++){
            Map<String, Object> deptMap = new HashMap<>();
            deptMap.put("name","deptname"+i);
            deptMap.put("type","1"+i);
            deptMap.put("father","");
            Map<String, Object> map = new HashMap<>();
            map.put("id","12345"+i);
            map.put("name","tom"+i);
            map.put("age","3"+i);
            map.put("role","工程师"+i);
            map.put("dept",deptMap);
            list.add(map);
        }
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(list);
        return json;
    }
    private String DoMSFind (String url, String hql, LinkedHashMap<String, Object> queryParamMap) throws Exception{
        Map<String, Object> deptMap = new HashMap<>();
        deptMap.put("name","deptname");
        deptMap.put("type","1");
        deptMap.put("father","");
        Map<String, Object> map = new HashMap<>();
        map.put("id","12345");
        map.put("name","tom");
        map.put("age","3");
        map.put("role","工程师");
        map.put("dept",deptMap);
        map.put("other","abc");
        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(map);
        return json;
    };
}
