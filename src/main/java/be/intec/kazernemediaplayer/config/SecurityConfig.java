package be.intec.kazernemediaplayer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


   /* @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Disable CSRF protection
               // .requiresChannel(channel -> channel.anyRequest().requiresSecure())  // Enforce HTTPS
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/media/upload", "/media/play/**").permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());  // Enable HTTP Basic authentication with default settings

        return http.build();
    }*/
  /* @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(authz -> authz
                       .requestMatchers("/**").permitAll()  // Permit all requests
               )
               .httpBasic(withDefaults());

       return http.build();
   }*/
   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
               .csrf(csrf -> csrf.disable())
               .authorizeHttpRequests(authz -> authz
                       .requestMatchers("/media/**").permitAll()  // Permit public access to media files
                       .anyRequest().authenticated()  // Require authentication for all other requests
               )
               .httpBasic(withDefaults());  // Enables HTTP Basic Authentication

       return http.build();
   }
}
