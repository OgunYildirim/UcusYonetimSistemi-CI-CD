package com.ucusyonetim.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Flight Management System API", version = "1.0.0", description = "Uçak Bileti Satış ve Yönetim Sistemi REST API Dokümantasyonu", contact = @Contact(name = "Flight Management Team", email = "support@flightmanagement.com")), servers = {
        @Server(description = "Local Development Server", url = "http://localhost:8080")
})
@SecurityScheme(name = "Bearer Authentication", type = SecuritySchemeType.HTTP, bearerFormat = "JWT", scheme = "bearer", in = SecuritySchemeIn.HEADER)
public class OpenApiConfig {
}
