package aloute.com.controller;
import aloute.com.service.SearchService;
import jakarta.servlet.http.HttpSession; 
import aloute.com.entity.User; 

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus; 
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam("q") String query, HttpSession session) {
        
        // BƯỚC 1: Thêm đoạn kiểm tra session quen thuộc
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Vui lòng đăng nhập để thực hiện chức năng này."));
        }
        
        // BƯỚC 2: Xử lý logic như cũ nếu đã đăng nhập
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        List<Map<String, Object>> results = searchService.searchAll(query);
        return ResponseEntity.ok(results);
    }
}
