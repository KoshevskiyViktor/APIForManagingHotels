package com.example.hotel_property_view;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(title = "Hotel Property View API", version = "1.0", description = "API for managing hotel properties and viewing related data."))
public class HotelPropertyViewApplication {

	public static void main(String[] args) {
		SpringApplication.run(HotelPropertyViewApplication.class, args);
	}
}
