package com.wsjia.gw.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wsjia.ga.service.GaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by a on 2016/8/2.
 */

@RestController
@RequestMapping("/gw")
public class GwController {

    @Autowired
    GaService gaService;

    @RequestMapping("/listUser")
    public String listUser(HttpServletRequest request) throws Exception{
        //String resultVos = gaService.PlistVo(request.getParameter("queryJson"));
        String json = testjson();
        String resultVos = gaService.PlistVo(json);
        return resultVos;
    }

//    @RequestMapping("/test")
//    public String test() throws Exception{
//        GaServiceImpl gaService = new GaServiceImpl();
//        return gaService.FakeUserMs("", "", "", new HashMap<String, String>());
//    }
    private String testjson() throws Exception{
        Map<String,Object> map = new LinkedHashMap<>();
        map.put("qmsid","12345");
        map.put("currentPage","1");
        map.put("pageSize","20");
        Map<String,String> params = new LinkedHashMap<>();
        params.put("name","Jack");
        params.put("age","23");
        map.put("params",params);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

}
