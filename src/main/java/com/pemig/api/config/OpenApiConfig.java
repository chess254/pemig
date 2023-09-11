package com.pemig.api.config;

import com.pemig.api.util.Const;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info =
        @Info(
            title = "Logicea CARD api task",
            contact =
                @Contact(
                    name = "Caleb Achesa",
                    email = "lae2006a@gmail.com"
                )
        ),
    servers = {
      @Server(url = "http://localhost:8080", description = "dev"),
    })
public class OpenApiConfig {

  @Bean
  public OpenAPI customizeOpenAPI() {
    final String securitySchemeName = "bearerAuth";
    return new OpenAPI()
        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
        .components(
            new Components()
                .addSchemas(
                    "FilterMap",
                    new Schema<Map<String, String>>()
                        .addProperty(Const.NAME_FILTER_STRING, new StringSchema().example("LoanDetails Name"))
                        .addProperty(Const.COLOR_FILTER_STRING, new StringSchema().example("#000000"))
                        .addProperty(
                            Const.STATUS_FILTER_STRING, new StringSchema().example("TODO"))
                        .addProperty(Const.CREATING_USER_FILTER_STRING, new StringSchema().example("user@test.com"))
                        .addProperty(
                            Const.BEGIN_CREATION_DATE_FILTER_STRING,
                            new StringSchema().example("12/12/2022 00:00:00.000"))
                        .addProperty(
                            Const.END_CREATION_DATE_FILTER_STRING,
                            new StringSchema().example("12/13/2022 01:00:00.000")))
                .addSchemas(
                    "FullCardDto",
                    new Schema<Map<String, String>>()
                        .addProperty(Const.NAME_FILTER_STRING, new StringSchema().example("LoanDetails Name"))
                        .addProperty(
                            "description", new StringSchema().example("Brief description for card"))
                        .addProperty(Const.COLOR_FILTER_STRING, new StringSchema().example("#FFFFFF"))
                        .addProperty(Const.STATUS_FILTER_STRING, new StringSchema().example("TODO"))
                        .addProperty(
                            "createdBy", new StringSchema().example("user@test.com"))
                        .addProperty(
                            "createdDateTime",
                            new StringSchema().example("12/13/2022 00:00:00.000"))
                        .addProperty(
                            "lastModifiedBy", new StringSchema().example("user@test.com"))
                        .addProperty(
                            "lastModifiedDateTime",
                            new StringSchema().example("12/13/2022 01:00:00.000")))
                .addSecuritySchemes(
                    securitySchemeName,
                    new SecurityScheme()
                        .name(securitySchemeName)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .description(
                            "Jwt token should be provided. See hw from the  \"authenticate\" endpoint.")
                        .bearerFormat("JWT")));
  }
}
