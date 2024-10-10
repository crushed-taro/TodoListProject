package crushedtaro.todolistproject.auth.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import crushedtaro.todolistproject.user.model.dto.AuthorityDTO;
import crushedtaro.todolistproject.user.model.dto.UserDTO;
import crushedtaro.todolistproject.user.model.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        /* 목차. 1. tbl_user 테이블로부터 주어진 username으로 레코드 검색 */
        UserDTO foundUser = userService.findByUsername(username);
        // System.out.println("#1. username으로 검색된 사용자 : " + foundUser);

        if (Objects.isNull(foundUser)) {
            throw new UsernameNotFoundException("회원정보가 존재하지 않습니다.");
        }

        /* 목차. 2. 검색된 사용자의 PK 값을 사용해 tbl_user_role 테이블로부터 해당 사용자가 인가받을 수 있는 모든 권한을 조회 */
        int userCode = foundUser.getUserCode();
        List<AuthorityDTO> authorities = userService.findAllAuthoritiesByUserCode(userCode);
        // System.out.println("#2. 사용자가 인가받을 권한 : " + authorities);

        /* 목차. 3. 사용자가 인가받을 모든 권한(List<UserRole>)을 foundUser에 추가 */
        foundUser.setUserAuthorities(authorities);
        // System.out.println("#3. 완성된 UserDetails 타입의 사용자 정보 : " + foundUser);

        /* 목차. 4. 사용자의 모든 인증/인가 정보가 담긴 UserDetails 타입의 데이터 반환 */
        return foundUser;
    }
}
