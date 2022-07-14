package ru.belkov.SiteSearchEngine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SiteSearchEngineApplication {
	public static void main(String[] args) {
		SpringApplication.run(SiteSearchEngineApplication.class, args);
	}
}
