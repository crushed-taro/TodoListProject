package crushedtaro.todolistproject.user.model.dto;

public class UserAuthorityDTO {

    private int userCode;
    private int authorityCode;

    public UserAuthorityDTO() {
    }

    public UserAuthorityDTO(int userCode, int authorityCode) {
        this.userCode = userCode;
        this.authorityCode = authorityCode;
    }

    public int getUserCode() {
        return userCode;
    }

    public void setUserCode(int userCode) {
        this.userCode = userCode;
    }

    public int getAuthorityCode() {
        return authorityCode;
    }

    public void setAuthorityCode(int authorityCode) {
        this.authorityCode = authorityCode;
    }

    @Override
    public String toString() {
        return "UserAuthorityDTO{" +
                "userCode=" + userCode +
                ", authorityCode=" + authorityCode +
                '}';
    }
}
