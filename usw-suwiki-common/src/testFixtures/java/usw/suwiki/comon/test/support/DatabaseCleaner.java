package usw.suwiki.comon.test.support;

import usw.suwiki.comon.test.Table;

import java.util.Set;

public interface DatabaseCleaner {
  void clean(Set<Table> tables);
}
