package com.example.webtoon.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.HttpAuthenticationScheme;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;


@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    private static final String REFERENCE = "Bearer 토큰 값";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .securityContexts(Collections.singletonList(securityContext()))
            .securitySchemes(List.of(bearerAuthSecurityScheme()))
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.example.webtoon"))
            .paths(PathSelectors.any())
            .build();
    }

    private SecurityContext securityContext() {
        return springfox.documentation
            .spi.service.contexts.SecurityContext
            .builder()
            .securityReferences(defaultAuth())
            .operationSelector(operationContext -> true)
            .build();
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        authorizationScopes[0] = authorizationScope;
        return List.of(new SecurityReference(REFERENCE, authorizationScopes));
    }

    private HttpAuthenticationScheme bearerAuthSecurityScheme() {
        return HttpAuthenticationScheme.JWT_BEARER_BUILDER
            .name(REFERENCE).build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("Webtoon Backend API")
            .description("Backend API 문서")
            .version("1.0")
            .build();
    }
}
