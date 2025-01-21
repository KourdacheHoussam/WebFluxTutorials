package com.reactive.learning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.reactive.learning.crud"})
public class LearningReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearningReactiveApplication.class, args);
	}

}
