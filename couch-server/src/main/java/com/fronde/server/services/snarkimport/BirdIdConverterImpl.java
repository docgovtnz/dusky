package com.fronde.server.services.snarkimport;

import com.fronde.server.domain.BirdEntity;
import com.fronde.server.services.bird.BirdService;
import com.fronde.server.services.snarkimport.processor.BirdIdConverter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BirdIdConverterImpl implements BirdIdConverter {

  private final Map<IslandChannelPair, List<BirdEntity>> cache = new HashMap<>();
  private final BirdService birdService;

  public BirdIdConverterImpl(BirdService birdService) {
    this.birdService = birdService;
  }

  @Override
  public List<BirdEntity> convertUhfId(String island, int uhfId) {
    IslandChannelPair pair = new IslandChannelPair(island, uhfId);
    List<BirdEntity> birdList = cache.get(pair);
    if (birdList == null) {
      birdList = birdService.findBirdsByTransmitter(island, uhfId, null);
      cache.put(pair, birdList);
    }
    return birdList;
  }

  private static class IslandChannelPair {

    private final String island;
    private final Integer uhfId;

    public IslandChannelPair(String island, Integer uhfId) {
      this.island = island;
      this.uhfId = uhfId;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      IslandChannelPair that = (IslandChannelPair) o;
      return Objects.equals(island, that.island) &&
          Objects.equals(uhfId, that.uhfId);
    }

    @Override
    public int hashCode() {
      return Objects.hash(island, uhfId);
    }

  }

}
