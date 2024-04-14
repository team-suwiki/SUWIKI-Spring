package usw.suwiki.comon.test.support.detail;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.comon.test.Table;
import usw.suwiki.comon.test.support.DatabaseCleaner;

import java.util.Set;

@Component
@Qualifier("MySqlDatabaseCleaner")
class MySqlDatabaseCleaner implements DatabaseCleaner {
  private static final String OFF_REFERENTIAL_INTEGRITY = "SET FOREIGN_KEY_CHECKS = 0;";
  private static final String ON_REFERENTIAL_INTEGRITY = "SET FOREIGN_KEY_CHECKS = 1;";
  private static final String TRUNCATE = "TRUNCATE TABLE ";

  @PersistenceContext
  protected EntityManager entityManager;

  @Override
  @Transactional
  public void clean(Set<Table> tables) {
    entityManager.createNativeQuery(OFF_REFERENTIAL_INTEGRITY).executeUpdate();
    tables.forEach(table -> entityManager.createNativeQuery(TRUNCATE + table.toLow()).executeUpdate());
    entityManager.createNativeQuery(ON_REFERENTIAL_INTEGRITY).executeUpdate();
  }
}