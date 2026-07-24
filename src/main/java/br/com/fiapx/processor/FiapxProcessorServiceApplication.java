package br.com.fiapx.processor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class FiapxProcessorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FiapxProcessorServiceApplication.class, args);
    }
}
