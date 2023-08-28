package com.logicea.cardtask;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Central Spring Boot application class. Runs the entire container.
 *
 * @author caleb
 */
@SpringBootApplication
public class CardsTaskApplication {

  public static void main(String[] args) {
    SpringApplication.run(CardsTaskApplication.class, args);
  }
}
