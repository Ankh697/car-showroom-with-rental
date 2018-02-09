package com.rental.carshowroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@EnableJpaRepositories("com.rental.carshowroom.repository")
@ComponentScan
@EntityScan(
		basePackageClasses = { CarShowroomApplication.class, Jsr310JpaConverters.class }
)
@EnableWebMvc
public class CarShowroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarShowroomApplication.class, args);
	}

}
