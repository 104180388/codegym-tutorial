package com.example.case_study_2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaseStudy2Application {

	public static void main(String[] args) {
		SpringApplication.run(CaseStudy2Application.class, args);
	}

}
