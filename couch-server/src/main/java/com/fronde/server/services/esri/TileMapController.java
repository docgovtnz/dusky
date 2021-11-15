package com.fronde.server.services.esri;

import com.fronde.server.services.authorization.PublicAPI;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/esri")
public class TileMapController {

  @Autowired
  protected TileMapService tileMapService;

  @RequestMapping(value = "/{mapTypeA}/{mapTypeB}/MapServer", method = RequestMethod.GET)
  @PublicAPI
  public ResponseEntity<String> getMapData(@PathVariable(value = "mapTypeA") String mapTypeA,
      @PathVariable(value = "mapTypeB") String mapTypeB) throws IOException {

    String mapType = mapTypeA + "/" + mapTypeB;
    String jsonResource = "/esri/" + mapType.replace("/", "_") + ".json";
    String json = IOUtils.toString(getClass().getResourceAsStream(jsonResource));

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<String> responseEntity = new ResponseEntity<>(json, headers, HttpStatus.OK);
    return responseEntity;
  }

  @RequestMapping(value = "/{mapTypeA}/{mapTypeB}/MapServer/tile/{a}/{b}/{c}", method = RequestMethod.GET)
  @PublicAPI
  public ResponseEntity<byte[]> getTile(@PathVariable(value = "mapTypeA") String mapTypeA,
      @PathVariable(value = "mapTypeB") String mapTypeB,
      @PathVariable(value = "a") String a,
      @PathVariable(value = "b") String b,
      @PathVariable(value = "c") String c) {

    String mapType = mapTypeA + "/" + mapTypeB;
    byte[] imageData = tileMapService.getTile(mapType, a, b, c);

    if (imageData != null) {
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.IMAGE_JPEG);
      ResponseEntity<byte[]> responseEntity = new ResponseEntity<>(imageData, headers,
          HttpStatus.OK);
      return responseEntity;
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}
