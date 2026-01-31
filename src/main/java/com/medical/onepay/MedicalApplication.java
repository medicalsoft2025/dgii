package com.medical.onepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import com.medical.onepay.config.onePay.OnePayProperties;

@SpringBootApplication
//@EnableConfigurationProperties(OnePayProperties.class)
public class MedicalApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedicalApplication.class, args);
	}

}
