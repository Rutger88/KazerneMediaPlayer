package be.intec.kazernemediaplayer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:4200")  // or your frontend's URL
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedHeaders("*")
                        .allowCredentials(true);
            }
        };
    }
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
