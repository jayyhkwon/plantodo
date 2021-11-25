package demo.plantodo;

import demo.plantodo.domain.Member;
import demo.plantodo.repository.MemberRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlantodoApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlantodoApplication.class, args);
	}

}
