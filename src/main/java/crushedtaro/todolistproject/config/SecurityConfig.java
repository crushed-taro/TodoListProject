package crushedtaro.todolistproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import crushedtaro.todolistproject.exception.AuthFailHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private AuthFailHandler authFailHandler;

    @Autowired
    public SecurityConfig(AuthFailHandler authFailHandler) {
        this.authFailHandler = authFailHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Bean
    public SecurityFilterChain filterChainConfigure(HttpSecurity http) throws Exception {

        // #1. 서버의 리소스에 접근 가능한 권한을 설정
        http.authorizeHttpRequests(auth -> {
            // 로그인, 회원가입, 실패 페이지와 홈은 모두에게 허용
            auth.requestMatchers("/auth/login","/user/signup","/auth/fail","/").permitAll();
            // '/admin/*' 엔드포인트는 ADMIN 권한을 가진 사용자만 접근 허용
            auth.requestMatchers("/admin/*").hasAnyAuthority("ADMIN");
            // '/user/*' 엔드포인트는 USER 권한을 가진 사용자만 접근 허용
            auth.requestMatchers("/user/*").hasAnyAuthority("USER");
            // 나머지 요청은 모두 인증된 사용자만 접근 가능
            auth.anyRequest().authenticated();
        // #2. <form> 태그를 사용한 로그인 관련 설정
        }).formLogin(login -> {
            // 로그인 페이지 경로 설정(로그인 페이지에 해당되는 핸들러 매핑이 존재해야 함)
            login.loginPage("/auth/login");
            // 사용자 id 입력 필드 (form 데이터 input의 name과 일치)
            login.usernameParameter("user");
            // 사용자 pass 입력 필드 (form 데이터 input의 name과 일치)
            login.passwordParameter("pass");
            // 로그인 성공시 이동할 페이지(로그인 성공 페이지에 해당되는 핸들러 매핑이 존재해야 함)
            login.defaultSuccessUrl("/",true);
            // 로그인 실패 시, 이 예외를 처리할 핸들러 지정(직접 제작한 핸들러 사용)
            login.failureHandler(authFailHandler);
        // #3. 로그아웃 요청 시 관련 처리 설정
        }).logout(logout -> {
            // 로그아웃 경로 설정
            logout.logoutRequestMatcher(new AntPathRequestMatcher("/auth/logout"));
            // 로그아웃 시 클라이언트의 JSESSIONID 쿠키 삭제
            logout.deleteCookies("JSESSIONID");
            // 세션을 무효화(소멸)하는 설정
            logout.invalidateHttpSession(true);
            // 로그아웃 성공 시 이동할 페이
            logout.logoutSuccessUrl("/");
        // #4. 애플리케이션 내 세션 관리 설정
        }).sessionManagement(session -> {
            // 동시 세션 수(허용 개수)를 1개로 제한하는 설정
            session.maximumSessions(1);
            // 세션 만료 시 이동할 페이지
            session.invalidSessionUrl("/");
        // #5. CSRF(Cross-Site Request Forgery: 사이트간 요청 위조) 관련 설정
        }).csrf(csrf ->
            // CSRF 보호 비활성화
            csrf.disable()
        );

        /* 설명. 위 5개 커스텀 설정을 마친 최종 HttpSecurity 객체를 빌드하여 반환. */
        return http.build();
    }
}
