package com.project.pc_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PcBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PcBackendApplication.class, args);
//		App on Render now
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

}
