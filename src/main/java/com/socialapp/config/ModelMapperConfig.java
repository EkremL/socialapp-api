package com.socialapp.config;

import org.modelmapper.ModelMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//! Projenin birçok yerinde manuel mapping yerine ModelMapper tercih ettim. Bu sayede kod tekrarı azaldı ve Dto - Entity dönüşümlei otomatik hale geldi.
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }
}
