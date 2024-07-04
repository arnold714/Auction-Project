package com.example.demo.auth;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
public class SecurityConfiguration {
	private final TokenProvider provider;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		http.httpBasic(HttpBasicConfigurer::disable)
				.csrf(CsrfConfigurer::disable)  //post, put, delete 요청 안먹음
				.cors(Customizer.withDefaults())
				.authorizeHttpRequests((authz)-> authz
						.requestMatchers("/index_admin").hasRole("ADMIN")//hasRole은 자동을 붙어서 자격만 써주면 된다.
						.requestMatchers("index_seller").hasRole("SELLER")
						.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()//forward 요청은 모두 허용
						.requestMatchers("/auth/**", "/index_**").authenticated()  //url이 /auth/로 시작하면 인증을 요구
						.requestMatchers("/", "/join", "/error", "/login", "/idcheck").permitAll()
						.anyRequest().permitAll()
				)
				//토큰 처리하는 필터를 현재 필터 앞에 붙임
				.addFilterBefore(new JwtAuthenticationFilter(provider), UsernamePasswordAuthenticationFilter.class);
		//세션 정책을 stateless로 설정. 상태유지 안함.
		http.sessionManagement(configurer->configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}


}
