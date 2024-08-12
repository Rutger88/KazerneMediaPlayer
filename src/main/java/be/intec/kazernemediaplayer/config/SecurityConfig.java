package be.intec.kazernemediaplayer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }}
   /* @Bean
    public AuthenticationSuccessHandler oauth2SuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("redirect:/afterlogin");
        };
    }

    @Bean
    public AuthenticationFailureHandler oauth2FailureHandler() {
        return (request, response, exception) -> {
            response.sendRedirect("/login?error=true");
        };
    }
}
   /* @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeRequests(authorizeRequests ->
                        authorizeRequests
                                .requestMatchers(HttpMethod.POST, "/users/register").permitAll()
                                .requestMatchers(HttpMethod.DELETE, "/users/**").authenticated()
                                .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.realmName("YourRealmName"));

        return http.build();
    }*/

