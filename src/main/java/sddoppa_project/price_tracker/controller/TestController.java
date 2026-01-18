package sddoppa_project.price_tracker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Application is running. Time: " + java.time.LocalDateTime.now();
    }

    @GetMapping("/test-parse")
    public String testParse(@RequestParam(required = false) String url) {
        if (url == null || url.isEmpty()) {
            return "Please provide url parameter: /test-parse?url=https://www.dns-shop.ru/...";
        }
        return "Will parse: " + url;
    }
}