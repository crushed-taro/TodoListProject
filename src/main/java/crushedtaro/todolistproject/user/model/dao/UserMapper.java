package crushedtaro.todolistproject.user.model.dao;

import org.apache.ibatis.annotations.Mapper;
import crushedtaro.todolistproject.user.model.dto.AuthorityDTO;
import crushedtaro.todolistproject.user.model.dto.SignupDTO;
import crushedtaro.todolistproject.user.model.dto.UserAuthorityDTO;
import crushedtaro.todolistproject.user.model.dto.UserDTO;

import java.util.List;

@Mapper
public interface UserMapper {

    int regist(SignupDTO signupDTO);

    UserDTO findByUsername(String username);

    int findMaxUserCode();

    int registUserAuthority(UserAuthorityDTO userAuthorityDTO);

    List<AuthorityDTO> findAllAuthoritiesByUserCode(int userCode);
}
