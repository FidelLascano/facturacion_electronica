
package com.fhalcom.facturacion.config;
import io.swagger.v3.oas.annotations.*; import io.swagger.v3.oas.annotations.info.Info; import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(info=@Info(title="Facturación Ecuador Enterprise API", version="v1"), security=@SecurityRequirement(name="bearerAuth"))
@SecurityScheme(name="bearerAuth", type=SecuritySchemeType.HTTP, scheme="bearer", bearerFormat="JWT")
public class OpenApiConfig {}
