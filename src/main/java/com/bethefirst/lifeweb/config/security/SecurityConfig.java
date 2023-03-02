package com.bethefirst.lifeweb.config.security;

import com.bethefirst.lifeweb.repository.campaign.LocalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
	private final LocalRepository localRepository;

	@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        // 시큐리티의 검증에서 제외됩니다.
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/images/**", "/js/**",
                        "/webjars/**","/error","/favicon.ico");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                // 401 , 403 Exception 핸들링
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

				//
//                .and()
//                .headers()
//                .frameOptions()
//                .sameOrigin()

                // 세션을 사용하지 않습니다
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				//
                .and()
                .formLogin().disable() //폼을 사용하지 않습니다.
                .httpBasic().disable() //basic 방식을 사용하지 않습니다.

                // 요청 접근제한 설정
                .authorizeHttpRequests(authorize -> authorize
						// sns, campaign-categories, campaign-types, locals
						.requestMatchers(HttpMethod.GET,"/sns", "/campaign-categories", "/campaign-types", "/locals").permitAll()//전체조회
						.requestMatchers("/sns/**", "/campaign-categories/**", "/campaign-types/**", "/locals/**").hasRole("ADMIN")
						// members 회원
						.requestMatchers(HttpMethod.POST, "/members/**").anonymous()//회원가입,로그인
						.requestMatchers(HttpMethod.GET, "/members").hasRole("ADMIN")//전체조회
						.requestMatchers(HttpMethod.PUT, "/members/{memberId}/password").permitAll()//비밀번호변경
						.requestMatchers(HttpMethod.PUT, "/members/{memberId}/point").hasRole("ADMIN")//포인트수정
						.requestMatchers(HttpMethod.GET, "/members/confirmation-email", "/members/nickname", "/members/email").anonymous()//인증메일발송, 중복체크
						.requestMatchers("/members/**").authenticated()//members 기본 인증완료
						// campaigns 캠페인
						.requestMatchers(HttpMethod.GET,"/campaigns/**").permitAll()//조회
						.requestMatchers("/campaigns/**").hasRole("ADMIN")//campaigns 기본 관리자
						// applications 신청서
						.requestMatchers(HttpMethod.GET, "/applications/**").authenticated()//조회
						.requestMatchers("/applications/**").hasRole("ADMIN")//applications 기본 관리자
						// applicants 신청자
						.requestMatchers("/applicants/status").hasRole("ADMIN")//신청자 상태 수정
						.requestMatchers("/applicants/**").authenticated()//applicants 기본 인증완료
						// reviews 리뷰
						.requestMatchers(HttpMethod.GET, "/reviews").permitAll()//전체조회
						.requestMatchers("/reviews/**").authenticated()//reviews 기본 인증완료

						.requestMatchers("/restdocs/**").denyAll()//restdocs 문서 작성
						.anyRequest().permitAll()
//						.anyRequest().denyAll()
				)

                // 스프링시큐리티가 동작하기 전 토큰작업이 먼저 실행됩니다
                .apply(new JwtSecurityConfig(tokenProvider));

        return httpSecurity.build();
    }

}
