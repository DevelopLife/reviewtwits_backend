package com.developlife.reviewtwits.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.*;

/**
 * @author ghdic
 * @since 2023/02/20
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private DateTimeFormatConfig dateTimeFormatConfig;

    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = { "classpath:/static/", "classpath:/public/", "classpath:/",
            "classpath:/resources/", "classpath:/META-INF/resources/", "classpath:/META-INF/resources/webjars/" };

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // /에 해당하는 url mapping을 /common/test로 forward한다.
        // registry.addViewController( "/" ).setViewName( "forward:/index" );
        // 우선순위를 가장 높게 잡는다.
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080", "http://localhost:3000", "http://43.201.141.63", "https://reviewtwits.mcv.kr",
                    "https://localhost:8080", "https://localhost:3000", "https://43.201.141.63", "https://reviewtwits.shop")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("headers")
                .allowCredentials(true)
                .maxAge(3000);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        dateTimeFormatConfig.registerFormatters(registry);
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }
}
