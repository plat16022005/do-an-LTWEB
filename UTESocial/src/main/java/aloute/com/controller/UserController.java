package aloute.com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
    @GetMapping("/")
    public String home() {
        return "index"; // Tự động map tới /WEB-INF/jsp/index.jsp
    }
}
