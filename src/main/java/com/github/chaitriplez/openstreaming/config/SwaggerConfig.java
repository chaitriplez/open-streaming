package com.github.chaitriplez.openstreaming.config;

import com.google.common.base.Optional;
import java.util.Objects;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ParameterBuilderPlugin;
import springfox.documentation.spi.service.contexts.ParameterContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Setter
@Configuration
@EnableSwagger2
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig {

  @Autowired private OpenStreamingProperties osProp;

  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .groupName("Open Streaming")
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
        .paths(PathSelectors.any())
        .build();
  }

  @Bean
  public ParameterBuilderPlugin parameterBuilderPlugin() {
    return new ParameterBuilderPlugin() {
      @Override
      public void apply(ParameterContext context) {
        Optional<PathVariable> pv =
            context.resolvedMethodParameter().findAnnotation(PathVariable.class);
        if (pv.isPresent()) {
          if (Objects.equals("brokerId", pv.get().name())) {
            context.parameterBuilder().defaultValue(osProp.getBrokerId());
          }
          if (Objects.equals("accountNo", pv.get().name())) {
            context.parameterBuilder().defaultValue(osProp.getAccountNo());
          }
        }
      }

      @Override
      public boolean supports(DocumentationType delimiter) {
        return true;
      }
    };
  }
}
