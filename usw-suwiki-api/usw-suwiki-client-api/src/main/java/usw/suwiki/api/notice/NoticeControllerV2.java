package usw.suwiki.api.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.notice.dto.NoticeResponse;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.statistics.annotation.Statistics;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;
import static usw.suwiki.statistics.log.MonitorTarget.NOTICE;

@RestController
@RequestMapping(value = "/notices")
@RequiredArgsConstructor
public class NoticeControllerV2 {
  private final NoticeService noticeService;

  @Statistics(NOTICE)
  @GetMapping("/v2")
  @ResponseStatus(OK)
  public CommonResponse<List<NoticeResponse.Simple>> getAllNoticesV2(@RequestParam(required = false) Optional<Integer> page) {
    var response = noticeService.getAllNotices(PageOption.offset(page));
    return CommonResponse.ok(response);
  }

  @Statistics(NOTICE)
  @GetMapping("/v2/{noticeId}")
  @ResponseStatus(OK)
  public CommonResponse<NoticeResponse.Detail> getNoticeV2(@PathVariable Long noticeId) {
    var response = noticeService.getNotice(noticeId);
    return CommonResponse.ok(response);
  }
}
