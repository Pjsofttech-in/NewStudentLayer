package Layer.NewStudentManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NewStudentApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(NewStudentApplication.class, args);
	}

}
