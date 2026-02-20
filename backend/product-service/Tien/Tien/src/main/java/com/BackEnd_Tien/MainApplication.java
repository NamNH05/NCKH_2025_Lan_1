package com.BackEnd_Tien;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan(basePackages = {"com.BackEnd_Tien", "com.example.product_service"})
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
		System.out.print("Helo world");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}

