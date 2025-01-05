package com.doodler.quartzadmin;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.github.doodler.common.context.ServerProperties;
import lombok.RequiredArgsConstructor;

/**
 * @Description: WebSecurityConfig
 * @Author: Fred Feng
 * @Date: 20/12/2023
 * @Version 1.0.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final ServerProperties serverProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        String password = passwordEncoder().encode(serverProperties.getCredentials().getPassword());
        auth.inMemoryAuthentication().withUser(serverProperties.getCredentials().getUser())
                .password(password).roles("JOB_USER", "JOB_ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .antMatchers("/job/**").permitAll().antMatchers("/ui/**").authenticated().and()
                .formLogin().defaultSuccessUrl("/ui/").and().logout().logoutSuccessUrl("/login");
    }
}
