package usw.suwiki.api.user;

import org.springframework.boot.test.context.SpringBootTest;
import usw.suwiki.common.test.Table;
import usw.suwiki.common.test.support.AcceptanceTestSupport;

import java.util.Set;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerV2AcceptanceTest extends AcceptanceTestSupport {
  @Override
  protected Set<Table> targetTables() {
    return Set.of();
  }

  @Override
  protected void clean() {

  }
}