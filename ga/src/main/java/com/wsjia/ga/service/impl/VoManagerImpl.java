package com.wsjia.ga.service.impl;

import com.wsjia.ga.model.Field;
import com.wsjia.ga.model.Item;
import com.wsjia.ga.model.Vo;
import com.wsjia.ga.model.Vos;
import com.wsjia.ga.service.VoManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by a on 2016/7/28.
 */
@Service
public class VoManagerImpl implements VoManager {
    private static Map<String, Vos> vosMap = new HashMap<>();
    private static Map<String, Vo> voMap = new HashMap<>();

    static {
        String xmlurl = "/system/**/*.xml";
        readXmlFiles(xmlurl);
    }

    public static void readXmlFiles(String url){
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Logger logger = Logger.getLogger(VoManagerImpl.class);
        try {
            Resource[] xmlFiles = resolver.getResources(url);
            if (xmlFiles != null) {
                for (Resource resource : xmlFiles) {
                    logger.info("开始解析文件：" + resource.getURL());
                    initXmlFiles(new SAXReader().read(resource.getInputStream()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public static void initXmlFiles(Document document) {
        List<Node> nodeList = document.selectNodes("/vos");
        for (Node node : nodeList) {
            Vos vos = new Vos();
            vos.setName(node.selectSingleNode("@name").getText());
            String model = node.selectSingleNode("@model") == null? "" : node.selectSingleNode("@model").getText();
            vos.setModel(model);
            /*生成voMap*/
            LinkedHashMap<String, Vo> voMap = createVoMap(vos, node);
            vos.setVoMap(voMap);
            vosMap.put(vos.getName(), vos);
        }
    }

    /*生成voMap*/
    private static LinkedHashMap<String, Vo> createVoMap(Vos vos, Node node) {
        LinkedHashMap<String, Vo> tempVOMap = new LinkedHashMap<String, Vo>();
        List<Node> voNodeList = node.selectNodes("vo");
        for (Node voNode : voNodeList) {
            Vo vo = new Vo();
            vo.setName(voNode.selectSingleNode("@name").getText());
            String label = voNode.selectSingleNode("@label") == null? "" : voNode.selectSingleNode("@label").getText();
            String url = voNode.selectSingleNode("@url") == null? "" : voNode.selectSingleNode("@url").getText();
            vo.setLabel(label);
            vo.setUrl(url);
            vo.setVos(vos);
            /*生成fieldMap*/
            List<Node> fieldNodeList = voNode.selectNodes("field");
            LinkedHashMap<String, Field> fieldMap = new LinkedHashMap<String, Field>();
            fieldMap = createFieldMap(fieldNodeList, fieldMap);
            vo.setFieldMap(fieldMap);
            voMap.put(vo.getName(), vo);
            tempVOMap.put(vo.getName(), vo);
        }
        return tempVOMap;
    }

    /*生成fieldMap*/
    private static LinkedHashMap<String, Field> createFieldMap(List<Node> fieldNodeList, LinkedHashMap<String, Field> fieldMap) {

        if (fieldNodeList != null && fieldNodeList.size() > 0) {
            for (Node fieldNode : fieldNodeList) {
                String name = fieldNode.selectSingleNode("@name").getText();
                String type = fieldNode.selectSingleNode("@type") == null ? "" : fieldNode.selectSingleNode("@type").getText();
                String source = fieldNode.selectSingleNode("@source") == null ? "" : fieldNode.selectSingleNode("@source").getText();
                String url = fieldNode.selectSingleNode("@url") == null ? "" : fieldNode.selectSingleNode("@url").getText();
                String key = fieldNode.selectSingleNode("@key") == null ? "" : fieldNode.selectSingleNode("@key").getText();
                Field field = new Field();
                field.setName(name);
                field.setType(type);
                field.setSource(source);
                field.setUrl(url);
                field.setKey(key);
                /*生成itemMap*/
                List<Node> itemNodeList = fieldNode.selectNodes("item");
                ArrayList<Item> items = new ArrayList<Item>();
                items = createItemList(itemNodeList, items);
                field.setItems(items);
                fieldMap.put(field.getName(), field);
            }
        }
        return fieldMap;
    }

    /*生成itemMap*/
    private static ArrayList<Item> createItemList(List<Node> itemNodeList, ArrayList<Item> items) {
        if (itemNodeList != null && itemNodeList.size() > 0) {
            for (Node itemNode : itemNodeList) {
                String label = itemNode.selectSingleNode("@label").getText();
                String value = itemNode.selectSingleNode("@value").getText();
                Item item = new Item();
                item.setLabel(label);
                item.setValue(value);
                items.add(item);
            }
        }
        return items;
    }

    @Override
    public Map<String, Vos> fetchVosMap() {
        return vosMap;
    }

    @Override
    public Map<String, Vo> fetchVoMap() {
        return voMap;
    }

    @Override
    public Vos fetchVos(String vosName) {
        return vosMap.get(vosName);
    }

    @Override
    public Vo fetchVo(String voName) {
        return voMap.get(voName);
    }
}
