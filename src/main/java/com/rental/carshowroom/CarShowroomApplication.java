package com.rental.carshowroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.rental.carshowroom.repository")
@ComponentScan
public class CarShowroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(CarShowroomApplication.class, args);
	}
}
