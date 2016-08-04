package com.wsjia.ga.GaController;

import com.wsjia.ga.model.CustomerCaseView;
import com.wsjia.ga.model.Test;
import com.wsjia.ga.service.GaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.TagUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by a on 2016/8/4.
 */
@RestController
@RequestMapping("/ga")
public class GaController {

    @Autowired
    GaService gaService;

    @RequestMapping("/test")
    public List<Test> test (Map<String, Object> conditions) throws Exception{
        List<Test> list = new ArrayList<>();
        for (Object object:gaService.NewPlistVo(conditions)) {
            list.add((Test)object);
        }
        return list;
    }

    @RequestMapping("/list")
    public List<Test> plistCustomerCaseView () throws Exception{
        List<Test> list = new ArrayList<>();
        Map<String, Object> conditions = new LinkedHashMap<>();
        for (Object object:gaService.NewPlistVo(conditions)) {
            list.add((Test)object);
        }
        return list;
    }

    @RequestMapping("/listHead")
    public ArrayList listHead(){
        ArrayList list = gaService.listHeadService();
        return list;
    }

}
