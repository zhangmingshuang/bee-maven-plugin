package com.nascent.maven.plugin.bee.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/12
 */
@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/1")
    public String t1(String param1, Integer param2) {
        return "t1";
    }

    @PostMapping("/t2")
    public String t2(int a) {
        return "i2";
    }

    @PostMapping
    public String t3(String a, long b) {
        return "i3";
    }
}
