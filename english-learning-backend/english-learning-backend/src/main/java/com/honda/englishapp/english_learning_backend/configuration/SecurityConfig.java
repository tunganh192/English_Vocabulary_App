package com.honda.englishapp.english_learning_backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity // tự động được bật
@EnableMethodSecurity
public class SecurityConfig {

    private final String[]  PUBLIC_ENDPOINTS_METHOD_POST = {
            "/user",
            "/auth/login", "/auth/introspect", "/auth/logout","/auth/refresh"};

    private final String[]  PUBLIC_ENDPOINTS_METHOD_GET = {
            "/category/system-generated/parents",
            "/category/system-generated/subcategories/{parentId}",
            "/word/user/category/{categoryId}",
            "/word/count/system-parent-category/{categoryId}",
            "/word/count/category/{categoryId}"

    };

    @Autowired
    private CustomJwtDecoder customJwtDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeHttpRequests(request -> request
                        // Công khai
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINTS_METHOD_POST).permitAll()
                        .requestMatchers(HttpMethod.GET, PUBLIC_ENDPOINTS_METHOD_GET).permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .accessDeniedHandler(new CustomAccessDeniedHandler())  // Cấu hình CustomAccessDeniedHandler
                );

        httpSecurity.oauth2ResourceServer(oauth2 ->//Kích hoạt chức năng resource server trong cấu hình bảo mật HTTP. Chỉ định rằng JWT sẽ được sử dụng để xác thực.  (Đăng kí 1 authentication provider)
            oauth2.jwt(jwtConfigurer ->
                    jwtConfigurer.decoder(customJwtDecoder)  // chịu trách nhiệm verifyToken
                            .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                    .authenticationEntryPoint(new JwtAuthenticationEntryPoint())  // khi lỗi ở filter thì sẽ vào entry point  .  chỉ dùng duy nhất ở đây nên không nhất thiết cần tạo bean
        );

        httpSecurity.csrf(AbstractHttpConfigurer::disable); // bỏ config này đi. nó tác dụng bảo vệ end-point lại tấn công Attack Cross
        return httpSecurity.build();

    }

    //định nghĩa của Bean chịu trách nhiệm chuyển đổi một Jwt (đã được giải mã và xác minh)
    // thành một đối tượng Authentication mà Spring Security có thể hiểu và sử dụng cho việc ủy quyền
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter(); //là 1 converter cụ thể được cung cấp bởi Spring Security để trích xuất các quyền từ các claims trong JWT. mặc định, tìm kiếm một claim có tên là "scope" hoặc "scp" (theo chuẩn OAuth 2.0)
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");  //thêm tiền tố "ROLE_" vào mỗi quyền (authority) được trích xuất từ JWT

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(10);

    }
}
