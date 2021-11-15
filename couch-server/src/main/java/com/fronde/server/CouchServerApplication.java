package com.fronde.server;

import com.fronde.server.config.EnvironmentProcessor;
import com.fronde.server.config.HomePageRedirectValve;
import com.fronde.server.utils.SSLUtils;
import com.rits.cloning.Cloner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan("com.fronde.server")
public class CouchServerApplication {

  private static final Logger logger = LoggerFactory.getLogger(CouchServerApplication.class);

  public static String GENERATED_PROPERTIES_FILE = EnvironmentProcessor.GENERATED_PROPERTIES_FILE;

  @Value("${application.http.port}")
  private int applicationHttpPort;

  @Value("${server.port}")
  private int applicationTlsPort;

  @Value("${server.hostnameredirect.enabled}")
  boolean hostnameRedirectEnabled;

  @Autowired
  private HomePageRedirectValve redirectValve;

  @Bean
  public ServletWebServerFactory servletContainer() {
    TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
      @Override
      protected void postProcessContext(Context context) {
        SecurityConstraint securityConstraint = new SecurityConstraint();
        securityConstraint.setUserConstraint("CONFIDENTIAL");
        SecurityCollection collection = new SecurityCollection();
        collection.addPattern("/*");
        securityConstraint.addCollection(collection);
        context.addConstraint(securityConstraint);
      }
    };

    if (hostnameRedirectEnabled) {
      logger.info("Enabling Hostname Redirect Valve");
      tomcat.addEngineValves(redirectValve);
    }

    tomcat.addAdditionalTomcatConnectors(createStandardConnector());
    return tomcat;
  }

  private Connector createStandardConnector() {
    Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setPort(applicationHttpPort);
    connector.setRedirectPort(applicationTlsPort);
    return connector;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12, new SecureRandom());
    return passwordEncoder;
  }

  @Bean
  public Cloner cloner() {
    Cloner cloner = new Cloner();
    return cloner;
  }


  public static void main(String[] args) throws Exception {

    // Not ideal, and really needs to go, but need the certificate signing to settle down a bit first
    SSLUtils.trustAllCertificates();

    // Populate the passwords file if not already populated.
    popuplatePasswords();

    SpringApplication.run(CouchServerApplication.class, args);
  }

  public static void popuplatePasswords() {
    File passwordFile = new File(System.getProperty("user.dir"), GENERATED_PROPERTIES_FILE);
    logger.info("Checking for generated passwords file: " + passwordFile.getAbsolutePath());
    if (!passwordFile.exists()) {
      logger.info("File doesn't exist; creating new passwords.");
      PasswordGenerator generator = new PasswordGenerator();
      try (PrintWriter pw = new PrintWriter(new FileWriter(passwordFile))) {
        pw.print("couchbase.bucket.password=encrypted(");
        pw.print(EnvironmentProcessor.encrypt(generator.generatePassword()));
        pw.println(")");
        pw.print("couchbase.admin.password=encrypted(");
        pw.print(EnvironmentProcessor.encrypt(generator.generatePassword()));
        pw.println(")");
      } catch (IOException ex) {
        throw new RuntimeException("Could not create password file");
      }
    } else {
      logger.info("Generated properties file already exists.");
    }

  }

}
