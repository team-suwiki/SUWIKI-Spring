package usw.suwiki.core.version.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.lecture.service.LectureService;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/suwiki")
@RestController
@RequiredArgsConstructor
public class VersionController {
  private final LectureService lectureService;

  @GetMapping("/version")
  public ResponseEntity<VersionResponseDto> findVersionSuwiki() {
    HttpHeaders header = new HttpHeaders();
    float version = SuwikiVersion.VERSION;
    VersionResponseDto dto = new VersionResponseDto(version);
    return new ResponseEntity<>(dto, header, HttpStatus.valueOf(200));
  }

  @GetMapping("/majorType")
  public ResponseEntity<ResponseForm> findAllMajorType() {
    HttpHeaders header = new HttpHeaders();
    List<String> list = lectureService.loadMajorTypes();
    ResponseForm data = new ResponseForm(list);
    return new ResponseEntity<>(data, header, HttpStatus.valueOf(200));
  }
}
