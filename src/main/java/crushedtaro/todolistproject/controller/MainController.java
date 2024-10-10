package crushedtaro.todolistproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/* 설명. Root Context 및 /main에 대한 이동 경로 설정. */
@Controller
public class MainController {

    /* 설명. 메인 화면으로 보내기 위한 URL 매핑 */
    @GetMapping(value = {"/", "/main"})
    public ModelAndView main(ModelAndView mv) {
        mv.setViewName("main/main");
        return mv;
    }

    /* 설명. 관리자 권한 설정 체크 */
    @GetMapping("/admin/page")
    public ModelAndView admin(ModelAndView mv) {
        mv.setViewName("admin/admin");
        return mv;
    }

    /* 설명. 유저 권한 설정 체크 */
    @GetMapping("/user/page")
    public ModelAndView user(ModelAndView mv) {
        mv.setViewName("user/user");
        return mv;
    }
}
