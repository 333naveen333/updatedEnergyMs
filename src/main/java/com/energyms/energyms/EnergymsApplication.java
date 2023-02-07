package com.energyms.energyms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EnergymsApplication {
	public static void main(String[] args) {
		SpringApplication.run(EnergymsApplication.class, args);
	}  		
}


//.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
//.\bin\windows\kafka-server-start.bat .\config\server.properties
//.\bin\windows\kafka-console-consumer.bat --topic device_topic --from-beginning --bootstrap-server localhost:9092
