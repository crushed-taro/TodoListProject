package crushedtaro.todolistproject.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import crushedtaro.todolistproject.user.model.dto.SignupDTO;
import crushedtaro.todolistproject.user.model.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/signup")
    public void signup() {}

    @PostMapping("/signup")
    public ModelAndView signup(ModelAndView mv,
                               @ModelAttribute SignupDTO newUserDTO) {

        /* 설명.
         *  요청 속 form 데이터를 일일이 파싱하지 않고 커맨드 객체로 받아냈기 때문에
         *  회원가입 폼(SignupDTO) 통째로 서비스로 넘겨서 비즈니스 로직 호출 가능.
         *  =======================================================================================================
         *  이 때, Service Layer로 세부 비즈니스 로직(DML)을 호출한 후 그 반환형을 int가 아닌 Integer로 설정했다.
         *  이는 DML 작업 결과에 대한 Java 기반 애플리케이션 측 null값 처리를 위해서다.
         *  1. null일 때:
         *    - 데이터 삽입(DML) 작업 실패.
         *    - 중복된 사용자가 이미 존재하거나, 데이터베이스 연결 문제 또는 기타 예외가 발생한 경우.
         *  2. 0일 때:
         *    - 데이터 삽입(DML) 작업은 성공, 하지만 실제로 레코드가 삽입되지는 않았음.
         *    - 비즈니스 로직 자체에 문제가 있거나, DDL 또는 DML 쿼리 확인 필요.
         *  3. 1일 때:
         *    - 이터 삽입(DML) 작업 성공, 정상적으로 1개의 레코드가 삽입됨.
         * */
        Integer result = userService.regist(newUserDTO);

        /* 설명. 비즈니스 로직(회원 가입) 성공 여부에 따라 Dispatcher Servlet에 반환할 View와 Model을 세팅 */
        String message = null;

        if (result == null ) {
            message ="이미 해당 정보로 가입된 회원이 존재합니다.";
            System.out.println(message);
            mv.setViewName("user/signup");
        } else if(result == 0) {
            message="회원가입에 실패하였습니다. 다시 시도해주세요.";
            System.out.println(message);
            mv.setViewName("user/signup");
        } else if(result >= 1) {
            message = "회원가입이 성공적으로 완료되었습니다.";
            System.out.println(message);
            /* 설명. 포워드 시키고 싶을 때(추천) */
            mv.setViewName("auth/login");
            /* 설명. 리다이렉트 시키고 싶을 때
             *  리다이렉트를 시키면 최종적으로 보게 될 View의 URL에 아래 addObject()로 추가되는 메시지가
             *  쿼리스트링으로 표현될 것이다.
             *  이를 감추려면 RedirectAttribute와 addFlashAttribute를 사용해야 한다.
             * */
//            mv.setViewName("redirect:/auth/login");
        } else {
            message = "알 수 없는 오류가 발생했습니다. 관리자에게 문의하세요.";
            System.out.println(message);
            mv.setViewName("auth/signup");
        }

        mv.addObject("message", message);

        return mv;
    }
}
