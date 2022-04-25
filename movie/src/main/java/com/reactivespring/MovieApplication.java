package com.reactivespring;

import com.reactivespring.service.KafkaProducerService;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableKafka
@EnableScheduling
@EnableEncryptableProperties
@RequiredArgsConstructor
@Slf4j
public class MovieApplication {

	private final KafkaProducerService producerService;

	public static void main(String[] args) {
		SpringApplication.run(MovieApplication.class, args);
	}

	@Component
	class Testing implements ApplicationListener<ApplicationReadyEvent> {


		@Override
		public void onApplicationEvent(ApplicationReadyEvent event) {

			producerService.sendMessage("This is ping test");
		}
	}
}
