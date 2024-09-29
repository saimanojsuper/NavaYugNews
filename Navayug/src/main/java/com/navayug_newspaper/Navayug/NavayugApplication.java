package com.navayug_newspaper.Navayug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NavayugApplication {

	public static void main(String[] args) {
		SpringApplication.run(NavayugApplication.class, args);
	}

}
