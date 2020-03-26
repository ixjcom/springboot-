package com.example.demo;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/jsonp")
public class JsonpTestController {
    @RequestMapping(value = "/testJsonp",produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseData testJsonp(){
        ResponseData responseData = new ResponseData();
        List<Student> list = new ArrayList<>();
        Student student = new Student();
        student.setId("1");
        student.setName("durk");
        student.setSex("man");
        list.add(student);
        responseData.setData(list);
        responseData.setSuccess(true);
        return responseData;
    }
}
