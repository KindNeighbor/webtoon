package com.example.webtoon.config;

import com.example.webtoon.security.CustomUserDetailsService;
import com.example.webtoon.security.JwtAccessDeniedHandler;
import com.example.webtoon.security.JwtAuthenticationEntryPoint;
import com.example.webtoon.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    prePostEnabled = true
)
public class SecurityConfig extends WebSecurityConfigurerAdapter{
    

    private final CustomUserDetailsService customUserDetailService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public JwtAuthenticationFilter JwtAuthenticationFilter(){
        return new JwtAuthenticationFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder
            .userDetailsService(customUserDetailService)
            .passwordEncoder(passwordEncoder());
    }

    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors() //Cross Origin Resource Sharing
                    .and()
                .csrf()
                    .disable() //rest api이므로 csrf 보안이 필요 없으므로 disable 처리
                .exceptionHandling() //예외처리
                    .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                    .accessDeniedHandler(jwtAccessDeniedHandler)
                    .and()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션은 필요없으므로 생성안함
                    .and()
                .authorizeRequests() //다음 리퀘스트에 대한 사용권한 체크
                    .antMatchers("/api/signin", "/api/signup")
                        .permitAll()
                    .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**")
                        .permitAll()
                    .anyRequest()
                        .authenticated(); //그 외 나머지 요청은 모두 인증된 회원만 접근가능

        // Add our custom JWT security filter
        http.addFilterBefore(JwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

    }
}