package aloute.com.controller;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class TestImageController {

    @GetMapping("/test-image")
    public ResponseEntity<Resource> getImage() {
    	System.out.println(Paths.get("uploads/avatars/test.jpg").toAbsolutePath());
        try {
            Path path = Paths.get("uploads/avatars/test.jpg");
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}

