package be.intec.kazernemediaplayer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve files from the media directory
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:///D:/KazerneMediaPlayer%20Songs%202024/");

        // Serve files from the movies directory
        registry.addResourceHandler("/movies/**")
                .addResourceLocations("file:///D:/KazerneMediaPlayer%20Songs%202024/");
    }
}
