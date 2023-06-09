package pokatika.example.pokatika;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class PokatikaApplication {
	static{
		System.setProperty("spring.config.location", "classpath:/application-ipfs.yml, classpath:/application.yml");
	}
	public static void main(String[] args) {
		SpringApplication.run(PokatikaApplication.class, args);
		log.info("스프링 부트 실행 성공");
	}

}
