package com.medical.onepay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

//import com.medical.onepay.config.onePay.OnePayProperties;

@SpringBootApplication
//@EnableConfigurationProperties(OnePayProperties.class)
@EnableScheduling
public class MedicalApplication {

	public static void main(String[] args) {
        System.setProperty("javax.xml.parsers.SAXParserFactory", "com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl");
		SpringApplication.run(MedicalApplication.class, args);
	}

}
