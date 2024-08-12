package be.intec.kazernemediaplayer.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface SecurityConfigImpl {
    void configure(HttpSecurity http) throws Exception;
}
