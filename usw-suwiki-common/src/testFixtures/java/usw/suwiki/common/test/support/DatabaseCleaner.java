package usw.suwiki.common.test.support;

import usw.suwiki.common.test.Table;

import java.util.Set;

public interface DatabaseCleaner {
  void clean(Set<Table> tables);
}
