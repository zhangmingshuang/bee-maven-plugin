package com.nascent.maven.plugin.bee.test;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @author zhangmsh
 * @version 1.0.0
 * @date 2020/6/17
 */
@RestController
@RequestMapping("/api/Employee")
public class Test2Controller {

    @PostMapping
    public String test() {
        return "s";
    }
}
