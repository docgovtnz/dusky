package com.fronde.server.config;


import java.net.URL;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

  private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {"classpath:/resources/"};

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/esri/**").addResourceLocations(CLASSPATH_RESOURCE_LOCATIONS)
        .resourceChain(false).addResolver(new ResourceResolver() {
      @Override
      public Resource resolveResource(HttpServletRequest request, String requestPath,
          List<? extends Resource> locations, ResourceResolverChain chain) {
        URL resource = StaticResourceConfiguration.class.getResource("/" + requestPath);
        return new UrlResource(resource);
      }

      @Override
      public String resolveUrlPath(String resourcePath, List<? extends Resource> locations,
          ResourceResolverChain chain) {
        throw new UnsupportedOperationException("Not implemented");
      }
    });
  }
}