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

/* 설명. Spring Security 사용자 정의:
 *  Spring Security 디폴트 설정 때문에 root context 요청 시 로그인 화면으로 강제 리다이렉트 된다.
 *  이를 수정하고자 사용자 정의 규칙을 추가하기 위한 Spring Security 전용 설정 클래스를 정의한다.
 * */

/* 설명. @Configuration:
 *  결국 Spring Security 관련 설정도 IoC 컨테이너가 인식해야 하는 설정 정보이므로 해당 어노테이션이 필요하다.
 * */
@Configuration
/* 설명. @EnableWebSecurity:
 *  Spring Security 설정을 활성화시키는 어노테이션.
 *  (모든 요청 URL이 Spring Security의 감시 하에 놓이며, Spring Security Filter Chain에 따라 특정 요청을 직접 제어함)
 *  기본적인 웹 기반 보안 기능들을 제공하며, 개발자는 HttpSecurity를 사용해 보안 설정을 커스터마이징 할 수 있다.
 *  이를 위해 아래에서 configure() 메소드를 구현할 수 있다.
 * */
@EnableWebSecurity
public class SecurityConfig {

    /* 설명. 아래의 filterChainConfigure() 메서드에서 로그인 실패 시 예외를 처리할 핸들러.
     *  exception 패키지 생성 후 핸들러를 작성한다.
     *  해당 핸들러는 @Configuration 어노테이션을 갖게 되므로 아래에서 생성자 의존성 주입을 한다.
     * */
    private AuthFailHandler authFailHandler;

    @Autowired
    public SecurityConfig(AuthFailHandler authFailHandler) {
        this.authFailHandler = authFailHandler;
    }

    /* 목차. 1-1. 사용자의 비밀번호를 BCrypt 암호화하기 위한 Bean 설정
     *  =================================================================================================
     *  BCryptPasswordEncoder를 사용한 이유(면접 질문 대비용)
     *   : BCrypt는 비밀번호 해싱(hashing)에 가장 많이 사용되는 알고리즘 중 하나이다.
     *   1. 보안성 : 해시 함수에 무작위 솔트(salt)를 적용하여 생성한다.
     *   2. 비용 증가 : 매개변수에 값을 주면 암호 생성 시간을 조절할 수 있어 무차별 공격을 어렵게 한다.
     *   3. 호환성 : 높은 보안 수준 및 데이터베이스에 저장하기 쉬운 특징.
     *   4. 알고리즘 신뢰성 : 논의 평가를 거친 알고리즘으로 보안 관련 로직에 문제없이 계속 사용 중.
     * */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* 목차. 1-2. @Bean WebSecurityCustomizer:
     * WebSecurityCustomizer는 Spring Security의 WebSecurity 객체를 커스터마이징하는데 사용된다.
     * 일반적인 위치에 있는 정적 리소스에 대한 요청을 Spring Security에서 무시하도록 하는 설정.
     * 이는 CSS, JavaScript, 이미지 파일 등 정적 리소스에 대한 접근을 항상 허용되도록 설정하는 것이 일반적이다.
     * (메서드 내에서 web 변수로 WebSecurity 객체에 접근할 수 있다)
     * */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        /* 설명. 여기서 말하는 정적 리소스의 위치는 "src/main/resources/static"다. */
        return web -> web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    /* 목차. 1-3. 해당 메서드에서 애플리케이션에 특정한 보안 설정을 정의한다.
     *  HttpSecurity 객체를 사용하여 다양한 보안 설정을 커스터마이징 할 수 있지만, 여기서는 아래와 같은 설정만 진행한다.
     *   1. 접근 제어 : 어떤 URL이 보호되는지, 어떤 역할/권한이 어떤 URL에 접근할 수 있는지 등
     *   2. 로그인 관리 : 사용자 정의 로그인 페이지, 로그인 성공/실패 핸들러 설정 등
     *   3. 로그아웃 관리 : 로그아웃 URL, 성공 핸들러, 로그아웃 후 쿠키 삭제 등
     *   4. 세션 관리 : 세션 고정 보호, 세션 만료, 세션 최대 허용 수 등
     *   5. CSRF : Cross-Site Request Forgery 보호 설정
     * */
    @Bean
    public SecurityFilterChain filterChainConfigure(HttpSecurity http) throws Exception {

        /* 설명. HttpSecurity와 SecurityFilterChain
         * 1. HttpSecurity:
         *  메서드 파라미터로 전달되는 HttpSecurity는 Spring Security에서 제공하는 보안 설정을 구성하는 주요 클래스이다.
         *  이를 통해 애플리케이션의 보안 규칙을 정의할 수 있으며, 다양한 설정을 체이닝 방식으로 적용할 수 있다.
         * ==============================================================================================================
         * 2. SecurityFilterChain:
         *  메서드 반환형인 SecurityFilterChain은 HttpSecurity 설정이 적용된 필터 체인을 의미한다.
         *  HttpSecurity 객체를 사용하여 보안 규칙을 정의하고, 최종적으로 SecurityFilterChain을 반환하여
         *  필터들을 체인으로 연결한다.
         *  이로 인해 보안 설정이 실제로 적용되며, 다양한 요청에 대해 정의된 보안 규칙들이 적용된다.
         * */

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
