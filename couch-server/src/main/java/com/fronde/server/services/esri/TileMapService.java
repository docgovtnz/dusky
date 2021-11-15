package com.fronde.server.services.esri;

import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;


@Component
public class TileMapService {

  @Value("${offline.maps.LINZ_geotiffs.alllayers.dir}")
  protected String linzGeotiffsAllLayersDir;


  public byte[] getTile(String mapType, String a, String b, String c) {

    try {
      byte[] imageData;
      switch (mapType) {
        case "Generic/newzealand":
        case "Imagery/newzealand":
          String tileRequest = "/tile/" + a + "/" + b + "/" + c;
          imageData = downloadTile(mapType, tileRequest);
          break;
        case "LINZ/geotiffs":
          imageData = loadExplodedCache(a, b, c);
          break;
        default:
          throw new RuntimeException("Unknown mapType: " + mapType);
      }

      return imageData;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private byte[] downloadTile(String mapType, String tileFileName) {
    String url =
        "https://services.arcgisonline.co.nz/arcgis/rest/services/" + mapType + "/MapServer"
            + tileFileName;
    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<byte[]> imageEntity = restTemplate.getForEntity(url, byte[].class);
    return imageEntity.getBody();
  }

  private byte[] loadExplodedCache(String a, String b, String c) throws IOException {
    String layerDir = leftPad("/L00", a);

    String bHexStr = Integer.toHexString(Integer.parseInt(b));
    String bpart = leftPad("/R00000000", bHexStr);

    String cHexStr = Integer.toHexString(Integer.parseInt(c));
    String cpart = leftPad("/C00000000", cHexStr) + ".jpg";

    File file = new File(linzGeotiffsAllLayersDir + layerDir + bpart + cpart);
    byte[] imageData = null;
    if (file.exists() && file.isFile() && file.canRead()) {
      imageData = FileUtils.readFileToByteArray(file);
    }
    return imageData;
  }

  private String leftPad(String base, String value) {
    return base.substring(0, base.length() - value.length()) + value;
  }

}
