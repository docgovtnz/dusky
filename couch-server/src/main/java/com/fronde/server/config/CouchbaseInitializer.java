package com.fronde.server.config;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.query.N1qlQuery;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * Reads secured properties and uses them to initialise the Couchbase DB.
 */
@Component
public class CouchbaseInitializer {

  /**
   * Logger.
   */
  private static final org.slf4j.Logger logger = LoggerFactory
      .getLogger(CouchbaseInitializer.class);

  /**
   * The base URI for couchbase APIs.
   */
  @Value("#{'http://${couchbase.host}:${couchbase.port}'}")
  private String uriBase;
  /**
   * The Authorization header in BASE64.
   */
  private String encodedAuthzHeader;

  /**
   * The REST Template for talking to the server.
   */
  private final RestTemplate template = new RestTemplate();

  @Value("${couchbase.bucket}")
  private String bucketName;

  @Value("${couchbase.bucket.password}")
  private String bucketPassword;

  @Value("${couchbase.admin.password}")
  private String adminPassword;


  @Value("#{${couchbase.init.createCluster.params}}")
  private Map<String, String> createClusterParams;
  @Value("#{${couchbase.init.storageMode.params}}")
  private Map<String, String> storageModeParams;
  @Value("#{${couchbase.init.services.params}}")
  private Map<String, String> servicesParams;
  @Value("#{${couchbase.init.stats.params}}")
  private Map<String, String> statsParams;
  @Value("#{${couchbase.init.userSetup.params}}")
  private Map<String, String> userSetupParams;
  @Value("#{${couchbase.init.createBucket.params}}")
  private Map<String, String> createBucketParams;
  @Value("#{${couchbase.init.createUser.params}}")
  private Map<String, String> createBucketUserParams;


  public CouchbaseInitializer() {
  }

  public static void main(String[] args) throws Exception {
    CouchbaseInitializer initializer = new CouchbaseInitializer();
    initializer.initialize();
  }

  /**
   * Initialise the database.
   */
  public void initialize() {
    try {
      String username = "Administrator";

      this.encodedAuthzHeader = Base64.getEncoder()
          .encodeToString((username + ":" + adminPassword).getBytes(StandardCharsets.UTF_8));

      // Create the cluster if it doesn't exist.
      if (!objectExists("/pools/default")) {
        logger.info("Creating cluster");
        invoke(HttpMethod.POST, "/pools/default", createClusterParams);
        invoke(HttpMethod.POST, "/settings/indexes", storageModeParams);
        invoke(HttpMethod.POST, "/node/controller/setupServices", servicesParams);
        invoke(HttpMethod.POST, "/settings/stats", statsParams);
        invoke(HttpMethod.POST, "/settings/web", userSetupParams);
      }

      // Create the user if it doesn't exist.
      if (!objectExists("/settings/rbac/users/local/" + bucketName)) {
        logger.info("Creating user");
        invoke(HttpMethod.PUT, "/settings/rbac/users/local/" + bucketName, createBucketUserParams);
      }

      // Create the bucket if it doesn't exist.
      if (!objectExists("/pools/default/buckets/" + bucketName)) {
        logger.info("Creating bucket");
        invoke(HttpMethod.POST, "/pools/default/buckets", createBucketParams);
      }

      // Wait for the bucket to start responding to queries before continuing.
      waitForBucketReady();
    } catch (ResourceAccessException ex) {
      logger.error("Failed to connect to Couchbase - are you sure it is running?", ex);
      throw new RuntimeException("Failed to initialize couchbase", ex);
    } catch (Exception ex) {
      throw new RuntimeException("Failed to initialize couchbase", ex);
    }
  }

  /**
   * Tests for the bucket to be ready. This loops a few times and throws an exception if the bucket
   * is still not ready after 30 seconds. This uses the Couchbase SDK, which seems to take longer to
   * recognise that the bucket is ready - i.e. we seem to be able to send N1ql queries to the query
   * service over HTTP before the bucket says it is ready.
   */
  private void waitForBucketReady() {
    Cluster cluster = CouchbaseCluster.fromConnectionString(uriBase);
    Bucket bucket = cluster.openBucket(bucketName, bucketPassword);
    boolean bucketReady = false;

    try {
      for (int i = 0; i < 30 && !bucketReady; i++) {
        try {
          Thread.sleep(1000);
        } catch (Exception ex) {
        }
        try {
          N1qlQuery q = N1qlQuery.simple("SELECT 1");
          bucket.query(q);
          logger.info("Bucket is now ready");
          bucketReady = true;
        } catch (Exception ex) {
          logger.warn("Bucket not yet ready");
        }
      }
      if (!bucketReady) {
        throw new RuntimeException("Bucket still not ready after waiting");
      }
    } finally {
      bucket.close();
      cluster.disconnect();
    }

  }

  /**
   * Exchange gives an exception if we get a 404. This catches the exception and returns a 404
   * response cod.e
   *
   * @param requestEntity The request.
   * @param responseType  The response type.
   * @param <T>           The type of the response.
   * @return A response object with the status code.
   */
  private <T> ResponseEntity<T> safeExchange(RequestEntity<?> requestEntity,
      Class<T> responseType) {
    try {
      return template.exchange(requestEntity, responseType);
    } catch (HttpClientErrorException ex) {
      if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
        return new ResponseEntity<T>(ex.getStatusCode());
      } else {
        throw ex;
      }
    }
  }

  /**
   * Call the API to check if the object exists.
   *
   * @param uri The URI to check.
   * @return true if exists; otherwise false.
   */
  private boolean objectExists(String uri) {
    ResponseEntity<Object> resp = getObject(uri);
    return resp.getStatusCode() == HttpStatus.OK;
  }

  /**
   * Sends a GET request to the target URI.
   *
   * @param uri The URI.
   * @return The response object.
   */
  private ResponseEntity<Object> getObject(String uri) {
    RequestEntity<Void> entity = RequestEntity
        .get(safeURI(uriBase + uri))
        .header("Authorization", "Basic " + encodedAuthzHeader)
        .build();
    return safeExchange(entity, Object.class);
  }

  /**
   * Invoke an API call with the parameters from the configuraiton file.
   *
   * @param method The method - POST or PUT.
   * @param params The parameters to send.
   */
  private void invoke(HttpMethod method, String uri, Map<String, String> params) {
    try {
      // Invoke as PUT or POST.
      ResponseEntity<Object> response = null;
      switch (method) {
        case POST:
          response = post(uri, params);
          break;
        case PUT:
          response = put(uri, params);
          break;
        default:
          throw new UnsupportedOperationException("Method " + method + " is not supported");
      }

      // Check that the response was positive.
      if (response.getStatusCode() != HttpStatus.OK
          && response.getStatusCode() != HttpStatus.ACCEPTED) {
        throw new RuntimeException("Initialisation failed with status code: "
            + response.getStatusCode().value()
            + " / "
            + response.getStatusCode().getReasonPhrase());
      }
    } catch (HttpClientErrorException ex) {
      logger.error("Failed to invoke method");
      logger.error("Server response: " + ex.getResponseBodyAsString());
      throw ex;
    }
  }

  /**
   * Sends a POST request to the URI using x-www-form-url-encoded to pass the parameters.
   *
   * @param uri    The URI to send the request to.
   * @param params The parameters.
   * @return The response object.
   */
  private ResponseEntity<Object> post(String uri, Map<String, String> params) {
    RequestEntity<String> entity = RequestEntity
        .post(safeURI(uriBase + uri))
        .header("Authorization", "Basic " + encodedAuthzHeader)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .body(getParamsString(params));

    return template.exchange(entity, Object.class);
  }

  /**
   * Sends a PUT request to the URI using x-www-form-url-encoded to pass the parameters.
   *
   * @param uri    The URI to send the request to.
   * @param params The parameters.
   * @return The response object.
   */
  private ResponseEntity<Object> put(String uri, Map<String, String> params) {
    RequestEntity<String> entity = RequestEntity
        .put(safeURI(uriBase + uri))
        .header("Authorization", "Basic " + encodedAuthzHeader)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .body(getParamsString(params));

    return template.exchange(entity, Object.class);
  }

  /**
   * Converts the exception from the URI constructor to a runnable exception.
   *
   * @param uri The URI string.
   * @return A URI object.
   */
  private URI safeURI(String uri) {
    try {
      return new URI(uri);
    } catch (URISyntaxException ex) {
      throw new RuntimeException("Invalid URI: " + uri, ex);
    }
  }

  /**
   * Converts params from a Map to a URL encoded string suitable for x-www-form-urlencoded
   * submission.
   *
   * @param params The parameters.
   * @return The map as a x-www-form-urlencoded.
   */
  private static String getParamsString(Map<String, String> params) {
    StringBuilder result = new StringBuilder();

    for (Map.Entry<String, String> entry : params.entrySet()) {
      result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
      result.append("=");
      result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
      result.append("&");
    }

    String resultString = result.toString();
    return resultString.length() > 0
        ? resultString.substring(0, resultString.length() - 1)
        : resultString;
  }

}
