package com.github.solanej.controller;

import com.github.solanej.ResponseData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

     @GetMapping("/test")
     public ResponseData test() {
         return ResponseData.success("Test endpoint is working!");
     }
}
