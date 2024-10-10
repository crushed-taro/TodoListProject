package crushedtaro.todolistproject.exception;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;

/* 설명. 사용자의 로그인 실패 시 실패 요청을 커스텀 하기 위한 핸들러이다.
 *  원래 AuthenticationFailureHandler 구현해야 하지만, 기존에 구현이 되었는 SimpleUrlAuthenticationFailureHandler을
 *  상속받아 응답 메시지와 페이지 경로를 설정할 수 있게 하도록 재정의를 하는 것이다.
 *  상속 후 onAuthenticationFailure() 메서드를 오버라이딩 하여 로그인 실패 시, 수행해야 할 로직을 재정의한다.
 *  이 때, 페이지 경로와 커스텀을 할 수 있도록 만들어주는 메서드는 setDefaultFailureUrl("경로") 메서드 이다.
 *  =======================================================================================================
 *  패키지 구조:
 *  AuthenticationFailureHandle(interface) -> SimpleUrlAuthenticationFailureHandler(class) -> AuthFailHandler
 * */
@Configuration
public class AuthFailHandler extends SimpleUrlAuthenticationFailureHandler {

    /* 설명 : onAuthenticationFailure 메소드가 호출될 defaultFailureUrl인 경우 redirect를 수행하는 AuthenticationFailureHandler.
     *  속성이 설정되어 있지 않은 경우 실패를 일으킨 AuthenticationException의 오류 메시지와 함께 클라이언트에게 401 오류를 응답한다.
     * */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
            throws IOException, ServletException {

        String errorMessage = null;

        /* 설명. 사용자의 인증 요청 진행 중, 발생한 예외의 타입에 따라 다양한 종류의 에러 메시지를 작성한다. */
        if (exception instanceof BadCredentialsException) {

            /* 설명. DB에 저장된 인증 정보, 즉 아이디가 존재하지 않거나 비밀번호가 틀린 경우의 에러 메시지 설정 */
            errorMessage = "[Auth-Failed] 아이디가 존재하지 않거나 비밀번호가 일치하지 않습니다.";
        } else if (exception instanceof InternalAuthenticationServiceException) {

            /* 설명. 서버 내부에서 사용자 정보를 검증하는 과정에서 발생하는 에러에 대한 메시지 설정 */
            errorMessage = "[Auth-Failed] 서버에서 오류가 발생되었습니다.";
        } else if (exception instanceof UsernameNotFoundException) {

            /* 설명. 사용자 정보가 DB에 없는 경우의 에러 메시지 설정(여기서 email은 ID에 입력하는 username을 의미) */
            errorMessage = "[Auth-Failed] 존재하지 않는 이메일 입니다.";
        } else if (exception instanceof AuthenticationCredentialsNotFoundException) {

            /* 설명. 보안 컨텍스트에 인증 객체가 존재하지 않거나,
             * 인증 정보가 없는 상태에서 보안 처리된 리소스에 접근하는 경우의 에러 메시지 설정 */
            errorMessage = "[Auth-Failed] 인증 요청이 거부되었습니다.";
        } else {

            /* 설명. 그 외의 알 수 없는 오류에 대한 에러 메시지 설정 */
            errorMessage = "[Auth-Failed] 알 수 없는 오류로 로그인 요청을 처리할 수 없습니다.";
        }

        /* 설명. URL을 안전하게 인코딩 하는데 사용되는 유틸로 문자열을 URL에 사용가능한 형식으로 인코딩할 수 있다. */
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");

        /* 설명. 로그인 실패 시 redirect 될 URL 설정 (실패 메시지를 query param으로 전달) */
        setDefaultFailureUrl("/auth/fail?message=" + errorMessage);

        super.onAuthenticationFailure(request, response, exception);
    }
}
