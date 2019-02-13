package com.example.generator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Description:
 * @Author: zyw
 * @Date: 2019/2/12
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry)
    {
        registry.addResourceHandler(new String[] { "/static/**" }).addResourceLocations(new String[] { "classpath:/static/" });
        super.addResourceHandlers(registry);
    }
}
