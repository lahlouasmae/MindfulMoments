package com.example.demo;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class VideoController {

    private final Path videoDir = Paths.get("uploads/videos");

    @GetMapping("/uploads/videos/{filename}")
    public Resource getVideo(@PathVariable String filename) {
        try {
            Path videoPath = videoDir.resolve(filename);
            Resource resource = new UrlResource(videoPath.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Video not found or is unreadable");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching video", e);
        }
    }
}
