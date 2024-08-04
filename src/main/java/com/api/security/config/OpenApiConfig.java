package com.api.security.config;


import java.io.IOException;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;

import com.api.security.utils.ReadJsonFileToObject;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.responses.ApiResponse;

@OpenAPIDefinition
@Configuration
@SecurityScheme(name = "bearerAuth", description = "JWT auth description", scheme = "bearer", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {

	@Bean
	public OpenAPI baseOpenAPI() throws IOException {

		ReadJsonFileToObject readJsonFileToObject = new ReadJsonFileToObject();

		ApiResponse badRequestAPI = new ApiResponse().content(
				new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
								new Example()
										.value(readJsonFileToObject.read().get("badRequestResponse").toString()))))
				.description("Bad Request!");

		ApiResponse badCredentialsAPI = new ApiResponse().content(
				new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
								new Example().value(
										readJsonFileToObject.read().get("badCredentialsResponse").toString()))))
				.description("Bad Credentials!");

		ApiResponse forbiddenAPI = new ApiResponse().content(
				new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
								new Example()
										.value(readJsonFileToObject.read().get("forbiddenResponse").toString()))))
				.description("Forbidden!");

		ApiResponse unprocessableEntityAPI = new ApiResponse().content(
				new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
								new Example()
										.value(readJsonFileToObject.read().get("unprocessableEntityResponse").toString()))))
				.description("unprocessableEntity!");

		ApiResponse internalServerErrorAPI = new ApiResponse().content(
				new Content().addMediaType(MediaType.APPLICATION_JSON_VALUE,
						new io.swagger.v3.oas.models.media.MediaType().addExamples("default",
								new Example()
										.value(readJsonFileToObject.read().get("internalServerError").toString()))))
				.description("Internal Server Error!");

		Components components = new Components();
		components.addResponses("BadRequest", badRequestAPI);
		components.addResponses("badcredentials", badCredentialsAPI);
		components.addResponses("forbidden", forbiddenAPI);
		components.addResponses("unprocessableEntity", unprocessableEntityAPI);
		components.addResponses("internalServerError", internalServerErrorAPI);

		return new OpenAPI()
				.components(components)
				.info(new Info().title("Api Security")
						.version("V0.0.1")
						.description("API para praticar spring secutiry com JWT")
						.contact(new Contact().name("Gabriel Martins(Biti)").email("gabriel-pms@hotmai.com")));
	}

	
	@Bean
	public GroupedOpenApi productApi() {
		String[] paths = { "/api/product/**" };
		return GroupedOpenApi.builder()
				.group("Products")
				.pathsToMatch(paths)
				.build();
	}

	@Bean
	public GroupedOpenApi AuthApi() {
		String[] paths = { "/api/auth/**" };
		return GroupedOpenApi.builder()
				.group("Authentication")
				.pathsToMatch(paths)
				.build();
	}
}