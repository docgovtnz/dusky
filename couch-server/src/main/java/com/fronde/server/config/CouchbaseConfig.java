package com.fronde.server.config;

import com.couchbase.client.java.env.CouchbaseEnvironment;
import com.couchbase.client.java.env.DefaultCouchbaseEnvironment;
import java.util.Collections;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.couchbase.config.AbstractCouchbaseConfiguration;
import org.springframework.data.couchbase.config.BeanNames;
import org.springframework.data.couchbase.core.convert.MappingCouchbaseConverter;
import org.springframework.data.couchbase.repository.support.IndexManager;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
@Configuration
public class CouchbaseConfig extends AbstractCouchbaseConfiguration {

  @Value("${couchbase.host}")
  protected String couchbaseHost;

  @Value("${couchbase.port}")
  protected Integer couchbasePort;

  @Value("${couchbase.bucket}")
  protected String couchbaseBucket;

  @Value("${couchbase.bucket.password}")
  protected String couchbaseBucketPassword;

  @Autowired
  private CouchbaseInitializer initializer;

  public CouchbaseConfig() {
    super();

    // This has to be set as a System property, it can't be put into application.properties.
    // This seems like a reasonable place for it but changes to intialization order over time might mean it stops working
    System.setProperty("org.springframework.data.couchbase.useISOStringConverterForDate", "true");
  }

  @Override
  @Bean(destroyMethod = "shutdown", name = BeanNames.COUCHBASE_ENV)
  public CouchbaseEnvironment couchbaseEnvironment() {
    // Initialise the DB first.
    initializer.initialize();

    return super.couchbaseEnvironment();
  }

  @Override
  protected CouchbaseEnvironment getEnvironment() {
    // This overrides the way the environment gets builts and enables us to specify our own low-level properties
    return DefaultCouchbaseEnvironment.builder().queryTimeout(180000).build();
  }

  @Override
  protected List<String> getBootstrapHosts() {
    return Collections.singletonList(couchbaseHost);
  }

  @Override
  protected String getBucketName() {
    return couchbaseBucket;
  }

  @Override
  protected String getBucketPassword() {
    return couchbaseBucketPassword;
  }

  @Override
  public IndexManager indexManager() {
    return new IndexManager(false, true, true);
  }

  @Override
  public String typeKey() {
    return MappingCouchbaseConverter.TYPEKEY_SYNCGATEWAY_COMPATIBLE;
  }
}
