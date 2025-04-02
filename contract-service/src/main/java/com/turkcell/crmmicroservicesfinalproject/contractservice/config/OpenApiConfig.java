package com.turkcell.crmmicroservicesfinalproject.contractservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI contractServiceOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8030");
        localServer.setDescription("Local Development Environment");

        Contact contact = new Contact();
        contact.setName("Turkcell CRM Microservices");
        contact.setEmail("support@turkcell.com");
        contact.setUrl("https://github.com/bothuany/crm-microservices-turkcell-final-project");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("Contract Service API")
                .version("1.0.0")
                .contact(contact)
                .description("This API exposes endpoints to manage subscription contracts.")
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}