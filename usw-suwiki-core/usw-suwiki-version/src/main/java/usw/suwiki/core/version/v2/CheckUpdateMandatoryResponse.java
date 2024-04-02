package usw.suwiki.core.version.v2;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CheckUpdateMandatoryResponse {
  private final Boolean isUpdateMandatory;
}
