package ipostu.mongo.demo;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@Component
@SpringBootApplication
public class MongoDemoApplication {


	public static void main(String[] args) {
		SpringApplication.run(MongoDemoApplication.class, args);
	}

}
