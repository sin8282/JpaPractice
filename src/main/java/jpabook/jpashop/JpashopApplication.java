package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;
import java.util.UUID;

//jpaAuditing을 사용할려면 해당 어노테이션을 선언해야함.
@EnableJpaAuditing
@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	@Bean
	public AuditorAware<String> auditorProvider() {
		//uuid에는 원래 아이디가 들어갈 자리다. 없으면 없는대로 있으면 삽입으로
		return () -> Optional.of(UUID.randomUUID().toString());
	}
}
