package com.wsjia.ga.service.impl;

import com.wsjia.ga.model.Field;
import com.wsjia.ga.model.Vo;
import com.wsjia.ga.model.Vos;
import com.wsjia.ga.service.GaService;
import com.wsjia.ga.service.VoManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by a on 2016/8/2.
 */
@Service
public class GaServiceImpl implements GaService {

    @Autowired
    VoManager voManager;

    @Override
    public List<Object> PlistVo(List<Object> resultEntityList) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
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
        Class voClass = Class.forName(vo.getModel());
        //remoteFields存储需要去其他微服务查询的属性
        ArrayList<Field> remoteFields = new ArrayList<>();
        //resultVoList用来存储最终将返回的vo数据集合
        ArrayList<Object> resultVoList = new ArrayList<>();
        //将查询结果列表转换成VO对象列表的json
        for (Object entity:resultEntityList){
            Object voObject = voClass.newInstance();
            java.lang.reflect.Field[] vofields = voClass.getDeclaredFields();
            java.lang.reflect.Field[] entityfields = entity.getClass().getDeclaredFields();
            for (String key:vo.getFieldMap().keySet()) {
                Field field = vo.getFieldMap().get(key);
                if (field.getUrl() == null || "".equals(field.getUrl())) {
                    String[] source = field.getSource().split("\\.");
                    if (source.length > 1) {
                        PropertyDescriptor entitypd1 = new PropertyDescriptor(source[0], entity.getClass());
                        Method voGetMethod1 = entitypd1.getReadMethod();
                        Object tempObject = voGetMethod1.invoke(entity);
                        PropertyDescriptor entitypd2 = new PropertyDescriptor(source[1], tempObject.getClass());
                        Method voGetMethod2 = entitypd2.getReadMethod();
                        Object object = voGetMethod2.invoke(tempObject);
                        PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                        Method voSetMethod = vopd.getWriteMethod();
                        voSetMethod.invoke(voObject, object);
                    } else {
                        PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                        Method voSetMethod = vopd.getWriteMethod();
                        PropertyDescriptor entitypd = new PropertyDescriptor(field.getSource(), entity.getClass());
                        Method entityGetMethod = entitypd.getReadMethod();
                        voSetMethod.invoke(voObject, entityGetMethod.invoke(entity));
                    }
                } else {
                    remoteFields.add(field);
                }
            }
            resultVoList.add(voObject);
        }
        //查询需要去其他微服务获取的数据
        if (remoteFields.size() > 0) {
            for (Object tempVo:resultVoList) {
                for (Field remoteField:remoteFields) {
                    String voValueName = remoteField.getName();
                    String remoteFieldUrl = remoteField.getUrl();
                    String[] key = remoteField.getKey().split("=");
                    String[] source = remoteField.getSource().split("\\.");
                    String hql = "FROM " + source[0] + " WHERE " + key[0] + "=:" + key[1];
                    LinkedHashMap<String, Object> queryParamMap = new LinkedHashMap<>();
                    //queryParamMap.put(key[1], tempVo.get(key[1]));
                    PropertyDescriptor vogetpd = new PropertyDescriptor(key[1], voClass);
                    Method voGetMethod = vogetpd.getReadMethod();
                    queryParamMap.put(key[1], voGetMethod.invoke(tempVo));
                    //调用微服务获得查询结果
                    //Map<String, Object> remoteResultMap = restTemplate.postForObject(remoteFieldUrl, null, Map.class);
                    Map<String, Object> remoteResultMap = testData();
                    PropertyDescriptor vosetpd = new PropertyDescriptor(voValueName, voClass);
                    Method voSetMethod = vosetpd.getWriteMethod();
                    voSetMethod.invoke(tempVo, (String) remoteResultMap.get(source[1]));
                }
            }
        }
        return resultVoList;
    }

    @Override
    public List<Object> NewPlistVo(Map<String, Object> conditions) throws Exception{
        RestTemplate restTemplate = new RestTemplate();
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
        Class voClass = Class.forName(vo.getModel());
        List<Object> resultEntityList = restTemplate.postForObject(vo.getUrl() + "list",conditions,List.class);
        //remoteFields存储需要去其他微服务查询的属性
        Map<String,ArrayList<Field>> remoteFields = new LinkedHashMap<>();
//        ArrayList<Field> remoteFields = new ArrayList<>();
        //resultVoList用来存储最终将返回的vo数据集合
        ArrayList<Object> resultVoList = new ArrayList<>();
        //将查询结果列表转换成VO对象列表的json
        for (Object entity:resultEntityList){
            Object voObject = voClass.newInstance();
            for (String key:vo.getFieldMap().keySet()) {
                Field field = vo.getFieldMap().get(key);
                if (field.getUrl() == null || "".equals(field.getUrl())) {
                    String[] source = field.getSource().split("\\.");
                    if (source.length > 2) {
//                        PropertyDescriptor entitypd1 = new PropertyDescriptor(source[0], entity.getClass());
//                        Method voGetMethod1 = entitypd1.getReadMethod();
//                        Object tempObject2 = voGetMethod1.invoke(entity);
//                        PropertyDescriptor entitypd2 = new PropertyDescriptor(source[1], tempObject2.getClass());
//                        Method voGetMethod2 = entitypd2.getReadMethod();
//                        Object tempObject3 = voGetMethod2.invoke(tempObject2);
//                        PropertyDescriptor entitypd3 = new PropertyDescriptor(source[2], tempObject3.getClass());
//                        Method voGetMethod3 = entitypd3.getReadMethod();
//                        Object object = voGetMethod2.invoke(tempObject3);
                        LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)entity;
                        Object object = ((LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>)mapEntity.get(source[0])).get(source[1])).get(source[2]);
                        PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                        Method voSetMethod = vopd.getWriteMethod();
                        voSetMethod.invoke(voObject, object);
                    } else if (source.length > 1) {
//                        PropertyDescriptor entitypd1 = new PropertyDescriptor(source[0], entity.getClass());
//                        Method voGetMethod1 = entitypd1.getReadMethod();
//                        Object tempObject = voGetMethod1.invoke(entity);
//                        PropertyDescriptor entitypd2 = new PropertyDescriptor(source[1], tempObject.getClass());
//                        Method voGetMethod2 = entitypd2.getReadMethod();
//                        Object object = voGetMethod2.invoke(tempObject);
                        LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)entity;
                        Object object = ((LinkedHashMap<String, Object>)mapEntity.get(source[0])).get(source[1]);
                        PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                        Method voSetMethod = vopd.getWriteMethod();
                        voSetMethod.invoke(voObject, object);
                    } else {
//                        PropertyDescriptor entitypd = new PropertyDescriptor(field.getSource(), entity.getClass());
//                        Method entityGetMethod = entitypd.getReadMethod();
//                        Object object = entityGetMethod.invoke(entity);
                        LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)entity;
                        Object object = mapEntity.get(source[0]);
                        PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                        Method voSetMethod = vopd.getWriteMethod();
                        voSetMethod.invoke(voObject, object);
                    }
                } else {
                    if (remoteFields.containsKey(field.getUrl())) {
                        remoteFields.get(field.getUrl()).add(field);
                    } else {
                        ArrayList<Field> tempFields = new ArrayList<Field>();
                        tempFields.add(field);
                        remoteFields.put(field.getUrl(), tempFields);
                    }
                }
            }
            resultVoList.add(voObject);
        }
        //查询需要去其他微服务获取的数据
        if (remoteFields.size() > 0) {
            for (Object tempVo:resultVoList) {
                for (String url:remoteFields.keySet()) {
                    String[] key = remoteFields.get(url).get(0).getKey().split("=");
                    String[] source = remoteFields.get(url).get(0).getSource().split("\\.");
                    String hql = "FROM " + source[0] + " WHERE " + key[0] + "=:" + key[1];
                    LinkedHashMap<String, Object> queryParamMap = new LinkedHashMap<>();
                    PropertyDescriptor vogetpd = new PropertyDescriptor(key[1], voClass);
                    Method voGetMethod = vogetpd.getReadMethod();
                    queryParamMap.put(key[1], voGetMethod.invoke(tempVo));
                    Map<String, Object> conditionList = new LinkedHashMap<>();
                    conditionList.put("hql", hql);
                    conditionList.put("params", queryParamMap);
                    //从其他微服务获取一个对象
                    Object remoteObject = restTemplate.postForObject(url, conditionList, Object.class);
                    for (Field field:remoteFields.get(url)) {
                        String[] tempSource = field.getSource().split("\\.");
                        if (tempSource.length > 3) {
//                            PropertyDescriptor entitypd1 = new PropertyDescriptor(source[1], remoteObject.getClass());
//                            Method voGetMethod1 = entitypd1.getReadMethod();
//                            Object tempObject2 = voGetMethod1.invoke(remoteObject);
//                            PropertyDescriptor entitypd2 = new PropertyDescriptor(source[2], tempObject2.getClass());
//                            Method voGetMethod2 = entitypd2.getReadMethod();
//                            Object tempObject3 = voGetMethod2.invoke(tempObject2);
//                            PropertyDescriptor entitypd3 = new PropertyDescriptor(source[3], tempObject3.getClass());
//                            Method voGetMethod3 = entitypd3.getReadMethod();
//                            Object object = voGetMethod2.invoke(tempObject3);
                            LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)remoteObject;
                            Object object = ((LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>)mapEntity.get(tempSource[1])).get(tempSource[2])).get(tempSource[3]);
                            PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                            Method voSetMethod = vopd.getWriteMethod();
                            voSetMethod.invoke(tempVo, object);
                        } else if (tempSource.length > 2) {
//                            PropertyDescriptor entitypd1 = new PropertyDescriptor(source[1], remoteObject.getClass());
//                            Method voGetMethod1 = entitypd1.getReadMethod();
//                            Object tempObject = voGetMethod1.invoke(remoteObject);
//                            PropertyDescriptor entitypd2 = new PropertyDescriptor(source[2], tempObject.getClass());
//                            Method voGetMethod2 = entitypd2.getReadMethod();
//                            Object object = voGetMethod2.invoke(tempObject);
                            LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)remoteObject;
                            Object object = ((LinkedHashMap<String, Object>)mapEntity.get(tempSource[1])).get(tempSource[2]);
                            PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                            Method voSetMethod = vopd.getWriteMethod();
                            voSetMethod.invoke(tempVo, object);
                        } else {
//                            PropertyDescriptor entitypd = new PropertyDescriptor(source[1], remoteObject.getClass());
//                            Method entityGetMethod = entitypd.getReadMethod();
//                            Object object = entityGetMethod.invoke(remoteObject);
                            LinkedHashMap<String, Object> mapEntity = (LinkedHashMap<String, Object>)remoteObject;
                            Object object = mapEntity.get(tempSource[1]);
                            PropertyDescriptor vopd = new PropertyDescriptor(field.getName(), voClass);
                            Method voSetMethod = vopd.getWriteMethod();
                            voSetMethod.invoke(tempVo, object);
                        }
                    }
                }
            }
        }
        return resultVoList;
    }

    @Override
    public ArrayList listHeadService() {
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
        ArrayList result = new ArrayList<>();
        for (String key:vo.getFieldMap().keySet()) {
            Field field = vo.getFieldMap().get(key);
            result.add(field);
        }
        return result;
    }


    //伪造调用微服务
    private Map<String, Object> testData(){
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id","010");
        map.put("name","fff");
        map.put("other","ooo");
        return map;
    }
}