package com.futbol.demo.config;

import org.springframework.context.annotation.Configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	//Añadimos las rutas de las fotos
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/logos/**", "/avatars/**")
               .addResourceLocations("file:logos/", "file:avatars/") // Ruta relativa
               .setCachePeriod(0);
    }
}