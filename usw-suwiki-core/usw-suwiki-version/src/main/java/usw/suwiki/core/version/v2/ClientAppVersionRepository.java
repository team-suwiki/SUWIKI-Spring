package usw.suwiki.core.version.v2;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientAppVersionRepository extends JpaRepository<ClientAppVersion, Long> {
  Optional<ClientAppVersion> findFirstByOsAndVitalTrueOrderByVersionCodeDesc(ClientOS os);
}
