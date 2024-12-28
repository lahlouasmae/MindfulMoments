package com.example.demo;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/users/**")
                .addResourceLocations("file:uploads/users/");
        registry.addResourceHandler("/uploads/images/**")
                .addResourceLocations("file:uploads/images/");
             
    registry.addResourceHandler("/uploads/videos/**")
           .addResourceLocations("file:/app/uploads/videos/");
           
    // Pour les anciens fichiers (chemin original)
    registry.addResourceHandler("/uploads/**")
           .addResourceLocations("file:uploads/");
    }

@Override
public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**")
            .allowedOrigins("http://localhost", "http://localhost:80")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true);
}

}