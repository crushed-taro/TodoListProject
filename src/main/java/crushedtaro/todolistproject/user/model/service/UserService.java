package crushedtaro.todolistproject.user.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import crushedtaro.todolistproject.user.model.dao.UserMapper;
import crushedtaro.todolistproject.user.model.dto.AuthorityDTO;
import crushedtaro.todolistproject.user.model.dto.SignupDTO;
import crushedtaro.todolistproject.user.model.dto.UserAuthorityDTO;
import crushedtaro.todolistproject.user.model.dto.UserDTO;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private UserMapper userMapper;
    private PasswordEncoder encoder;

    @Autowired
    public UserService(UserMapper userMapper, PasswordEncoder encoder) {
        this.userMapper = userMapper;
        this.encoder = encoder;
    }

    /* 설명. signup은 DML(INSERT) 작업이므로 @Transactional 어노테이션 추가 */
    @Transactional
    public Integer regist(SignupDTO newUserDTO) {

        /* 목차. 1. tbl_user 테이블에 신규 사용자 정보 INSERT */
        newUserDTO.setPassword(encoder.encode(newUserDTO.getPassword()));   // 패스워드 암호화

        Integer result1 = null;

        try {
            result1 = userMapper.regist(newUserDTO);
        } catch (DuplicateKeyException e) {     /* 설명. 데이터 무결성 위반 (중복 키) 발생 시 처리 */
            result1 = 0;
            e.printStackTrace();
        } catch (BadSqlGrammarException e) {    /* 설명. SQL 문법 오류 발생 시 처리 */
            result1 = 0;
            e.printStackTrace();
        }
        System.out.println("#1 신규 사용자 정보 삽입 결과 : " + result1);

        /* 목차. 2. tbl_user_role 테이블에 사용자 별 권한 INSERT */
        /* 목차. 2-1. 사용자 PK값 조회
         *  위의 목차 1번에서 신규 사용자 정보가 성공적으로 INSERT 되었다면, 그 레코드에 적절한 PK 값이 부여되었을 것이다.
         *  해당 PK 값이 필요하기 때문에 조회한다.
         * */
        int maxUserCode = userMapper.findMaxUserCode();
        System.out.println("#2-1 현재 tbl_user의 PK 최대값 : " + maxUserCode);

        /* 목차. 2-2. tbl_user_role 테이블에 신규 등록된 사용자의 PK와 디폴트 권한(일반사용자) PK인 2를 조합하여 INESRT */
        Integer result2 = null;

        try {
            result2 = userMapper.registUserAuthority(new UserAuthorityDTO(maxUserCode, 2));     // 2: 일반사용자
        } catch (DuplicateKeyException e) {     /* 설명. 데이터 무결성 위반 (중복 키) 발생 시 처리 */
            result2 = 0;
            e.printStackTrace();
        } catch (BadSqlGrammarException e) {    /* 설명. SQL 문법 오류 발생 시 처리 */
            result2 = 0;
            e.printStackTrace();
        }
        System.out.println("#2-2 신규 사용자 및 권한 코드 삽입 결과 : " + result2);

        /* 목차. 3. 위 세 가지 원자 트랜잭션이 성공해야 단위 트랜잭션이 성공했다고 판단. */
        Integer finalResult = null;

        if (result1 == null || result2 == null) {
            finalResult = null;
        } else if (result1 == 1 && result2 == 1) {
            finalResult = 1;
        } else {
            finalResult = 0;
        }

        return finalResult;
    }

    public UserDTO findByUsername(String username) {

        UserDTO foundUser = userMapper.findByUsername(username);

        if (!Objects.isNull(foundUser)) {
            return foundUser;
        } else {
            return null;
        }
    }

    public List<AuthorityDTO> findAllAuthoritiesByUserCode(int userCode) {
        return userMapper.findAllAuthoritiesByUserCode(userCode);
    }
}
