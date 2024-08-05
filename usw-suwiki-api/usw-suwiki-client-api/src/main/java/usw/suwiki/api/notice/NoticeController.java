package usw.suwiki.api.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.CommonResponse;
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

  @Statistics(NOTICE)
  @GetMapping("/all")
  @ResponseStatus(OK)
  public CommonResponse<List<NoticeResponse.Simple>> getNotices(@RequestParam(required = false) Optional<Integer> page) {
    var response = noticeService.getAllNotices(PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Statistics(NOTICE)
  @GetMapping("/")
  @ResponseStatus(OK)
  public CommonResponse<NoticeResponse.Detail> getNotice(@RequestParam Long noticeId) {
    var response = noticeService.getNotice(noticeId);
    return CommonResponse.ok(response);
  }

  @Authorize(ADMIN)
  @Statistics(NOTICE)
  @PostMapping("/")
  @ResponseStatus(OK)
  public CommonResponse<?> write(@Valid @RequestBody NoticeRequest.Create request) { // todo : admin api
    noticeService.write(request.getTitle(), request.getContent());
    return CommonResponse.success();
  }

  @Authorize(ADMIN)
  @Statistics(NOTICE)
  @PutMapping("/")
  @ResponseStatus(OK)
  public CommonResponse<?> updateNotice(@RequestParam Long noticeId, @Valid @RequestBody NoticeRequest.Update request) { // todo : admin api
    noticeService.update(noticeId, request.getTitle(), request.getContent());
    return CommonResponse.success();
  }

  @Authorize(ADMIN)
  @Statistics(NOTICE)
  @DeleteMapping("/")
  @ResponseStatus(OK)
  public CommonResponse<?> deleteNotice(@RequestParam Long noticeId) {
    noticeService.delete(noticeId);
    return CommonResponse.success();
  }
}


