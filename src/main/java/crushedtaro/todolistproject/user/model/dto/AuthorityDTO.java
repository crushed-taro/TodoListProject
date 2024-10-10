package crushedtaro.todolistproject.user.model.dto;

public class AuthorityDTO {

    private int code;               // 권한식별코드
    private String name;            // 권한명
    private String description;     // 권한설명

    public AuthorityDTO() {
    }

    public AuthorityDTO(int code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AuthorityDTO{" +
                "code=" + code +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
