package com.fronde.server.services.revision;

import com.fronde.server.domain.BaseEntity;
import java.util.Comparator;
import java.util.Date;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class RevisionComparator implements Comparator<BaseEntity> {

  @Override
  public int compare(BaseEntity o1, BaseEntity o2) {
    Date t1 = o1.getModifiedTime();
    Date t2 = o2.getModifiedTime();
    return t1.compareTo(t2);
  }
}
