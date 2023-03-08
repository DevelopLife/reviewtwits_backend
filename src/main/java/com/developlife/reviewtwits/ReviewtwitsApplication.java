package com.developlife.reviewtwits;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class ReviewtwitsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewtwitsApplication.class, args);
	}

}
