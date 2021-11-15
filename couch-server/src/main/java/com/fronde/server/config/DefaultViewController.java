package com.fronde.server.config;

import com.fronde.server.services.authorization.PublicAPI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 */
@Controller
public class DefaultViewController {

  @RequestMapping({
      "/app/**",
  })
  @PublicAPI
  public String index() {
    return "forward:/";
  }
}

