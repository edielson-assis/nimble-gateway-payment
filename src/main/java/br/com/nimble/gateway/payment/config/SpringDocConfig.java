package br.com.nimble.gateway.payment.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SpringDocConfig {

	@Value("${springdoc.server.url}")
	private String springDocServer;

	@Value("${springdoc.server.description}")
	private String springDocServerDescription;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.components(new Components()
					.addSecuritySchemes("bearer-key",
						new SecurityScheme()
							.type(SecurityScheme.Type.HTTP)
							.scheme("bearer")
							.bearerFormat("JWT")))
				.addServersItem(new Server()
					.url(springDocServer)
					.description(springDocServerDescription))
				.info(new Info()
					.title("Nimble API")
					.version("v1.0.0")
					.description("Nimble's payment gateway. Rest API to manage payments.")
					.termsOfService("https://www.nimble.com.br/termos-a-serem-definidos")
					.license(new License()
							.name("Apache 2.0")
							.url("https://github.com/edielson-assis/nimble-gateway-payment/blob/main/LICENSE"))
					.contact(new Contact()
							.name("Edielson Assis")
							.email("grizos.ed@gmail.com")
							.url("https://www.nimble.com.br")));
	}
}