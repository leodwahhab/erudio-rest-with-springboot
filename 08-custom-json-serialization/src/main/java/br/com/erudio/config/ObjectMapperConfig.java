package br.com.erudio.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        mapper.setFilterProvider(new SimpleFilterProvider() // passa como parametro um objeto que providencia filtros
                .addFilter("personFilter", SimpleBeanPropertyFilter // adiciona um filtro x para esse objeto
                        .serializeAllExcept("sensitiveData") // filtro x é condicional, serializará todos os campos do DTO exceto os marcado como sensitiveData
        ));

        return mapper;
    }
}
