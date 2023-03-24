package no.ntnu.bachelor.voicepick;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class VoicePickApplication {

	public static void main(String[] args) {
		SpringApplication.run(VoicePickApplication.class, args);
	}

}
