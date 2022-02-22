package com.samsung.portalserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PortalserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalserverApplication.class, args);
	}
}
