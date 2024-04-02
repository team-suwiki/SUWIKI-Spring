package usw.suwiki.core.version.v2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/version")
@RequiredArgsConstructor
public class ClientAppVersionController {
  private final ClientAppVersionService clientAppVersionService;

  @GetMapping("/update-mandatory")
  public ResponseEntity<CheckUpdateMandatoryResponse> checkIsUpdateMandatory(
    @RequestParam String os,
    @RequestParam Integer versionCode
  ) {
    return ResponseEntity.ok(clientAppVersionService.checkIsUpdateMandatory(os, versionCode));
  }
}
