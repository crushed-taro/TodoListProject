package crushedtaro.todolistproject.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "crushedtaro.todolistproject")
@MapperScan(basePackages = "crushedtaro.todolistproject", annotationClass = Mapper.class)
public class TodoListProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(TodoListProjectApplication.class, args);
    }

}
