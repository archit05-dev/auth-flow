package com.archit.authflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuthflowApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuthflowApplication.class, args);
	}

}
