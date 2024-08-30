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
                        .allowedOrigins("http://localhost:4200")  // Frontend origin
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("Authorization", "Content-Type", "Accept", "Cache-Control", "Expires", "Pragma")  // Allow required headers
                        .exposedHeaders("Authorization")
                        .allowCredentials(true)
                        .maxAge(3600000);  // Cache preflight response for 1 hour
            }
        };
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/media/**")
                .addResourceLocations("file:///D:/KazerneMediaPlayer%20Songs%202024/");
        registry.addResourceHandler("/movies/**")
                .addResourceLocations("file:///D:/KazerneMediaPlayer%20Songs%202024/");
    }
}
