package usw.suwiki.api.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.domain.notice.dto.NoticeRequest;
import usw.suwiki.domain.notice.dto.NoticeResponse;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.domain.user.Role.ADMIN;
import static usw.suwiki.statistics.log.MonitorTarget.NOTICE;

@RestController
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
public class NoticeController {
  private final NoticeService noticeService;

  @Statistics(target = NOTICE)
  @GetMapping("/all")
  @ResponseStatus(OK)
  public ResponseForm getNotices(@RequestParam(required = false) Optional<Integer> page) {
    List<NoticeResponse.Simple> response = noticeService.getAllNotices(new PageOption(page));
    return new ResponseForm(response);
  }

  @Statistics(target = NOTICE)
  @GetMapping("/")
  @ResponseStatus(OK)
  public ResponseForm getNotice(@RequestParam Long noticeId) {
    NoticeResponse.Detail response = noticeService.getNotice(noticeId);
    return new ResponseForm(response);
  }

  @Authorize(ADMIN)
  @Statistics(target = NOTICE)
  @PostMapping("/")
  @ResponseStatus(OK)
  public String write(@Valid @RequestBody NoticeRequest.Create request) { // todo : admin api
    noticeService.write(request.getTitle(), request.getContent());
    return "success";
  }

  @Authorize(ADMIN)
  @Statistics(target = NOTICE)
  @PutMapping("/")
  @ResponseStatus(OK)
  public String updateNotice(@RequestParam Long noticeId, @Valid @RequestBody NoticeRequest.Update request) { // todo : admin api
    noticeService.update(noticeId, request.getTitle(), request.getContent());
    return "success";
  }

  @Authorize(ADMIN)
  @Statistics(target = NOTICE)
  @DeleteMapping("/")
  @ResponseStatus(OK)
  public String deleteNotice(@RequestParam Long noticeId) {
    noticeService.delete(noticeId);
    return "success";
  }
}


