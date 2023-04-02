package com.adatb.bookaround;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class BookAroundApplication {

	private static final Logger logger = LogManager.getLogger(BookAroundApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BookAroundApplication.class, args);
		logger.info("Logging from main");
	}

}
