package co.uniajc.agrovalle.agrovalleconnect;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class AgrovalleConnectApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgrovalleConnectApplication.class, args);
	}

}
