package aloute.com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"aloute.com.controller", "aloute.com.service", "aloute.com.repository", "aloute.com.config"})
public class UteSocialApplication {

	public static void main(String[] args) {
		SpringApplication.run(UteSocialApplication.class, args);
	}

}
