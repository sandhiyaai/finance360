package com.myfinance.finance360;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Finance360Application {

	public static void main(String[] args) {
		SpringApplication.run(Finance360Application.class, args);
	}
}
