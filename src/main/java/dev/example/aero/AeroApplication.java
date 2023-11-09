package dev.example.aero;

import dev.example.aero.dao.AirportDao;
import dev.example.aero.model.Airport;
import dev.example.aero.repository.AirportRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AeroApplication {

	public static void main(String[] args) {
		SpringApplication.run(AeroApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String[] args){
		return runner -> {
			System.out.println("hello Aero!");
		};
	}
}
