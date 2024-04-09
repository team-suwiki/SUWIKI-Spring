package usw.suwiki.api.notice;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.pagination.PageOption;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.notice.dto.NoticeRequest;
import usw.suwiki.domain.notice.dto.NoticeResponse;
import usw.suwiki.domain.notice.service.NoticeService;
import usw.suwiki.statistics.annotation.ApiLogger;

import java.util.List;
import java.util.Optional;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping(value = "/notice")
@RequiredArgsConstructor
public class NoticeController {
  private final NoticeService noticeService;
  private final JwtAgent jwtAgent;

  @ApiLogger(option = "notice")
  @GetMapping("/all")
  @ResponseStatus(OK)
  public ResponseForm findNoticesApi(@RequestParam(required = false) Optional<Integer> page) {
    List<NoticeResponse.Simple> response = noticeService.getAllNotices(new PageOption(page));
    return new ResponseForm(response);
  }

  @ApiLogger(option = "notice")
  @GetMapping("/")
  @ResponseStatus(OK)
  public ResponseForm findNoticeApi(@RequestParam Long noticeId) {
    NoticeResponse.Detail response = noticeService.getNotice(noticeId);
    return new ResponseForm(response);
  }

  @ApiLogger(option = "notice")
  @PostMapping("/")
  @ResponseStatus(OK)
  public String write(
    @RequestHeader String Authorization,
    @Valid @RequestBody NoticeRequest.Create request
  ) {
    jwtAgent.validateJwt(Authorization);
    validateAdmin(Authorization);
    noticeService.write(request.getTitle(), request.getContent());
    return "success";
  }

  @ApiLogger(option = "notice")
  @PutMapping("/")
  @ResponseStatus(OK)
  public String updateNotice(
    @RequestHeader String Authorization,
    @RequestParam Long noticeId,
    @Valid @RequestBody NoticeRequest.Update request
  ) {
    jwtAgent.validateJwt(Authorization);
    validateAdmin(Authorization);
    noticeService.update(noticeId, request.getTitle(), request.getContent());

    return "success";
  }

  @ApiLogger(option = "notice")
  @DeleteMapping("/")
  @ResponseStatus(OK)
  public String deleteNotice(
    @RequestHeader String Authorization,
    @RequestParam Long noticeId
  ) {
    jwtAgent.validateJwt(Authorization);
    validateAdmin(Authorization);
    noticeService.delete(noticeId);

    return "success";
  }

  private void validateAdmin(String authorization) {
    if (!(jwtAgent.parseRole(authorization).equals("ADMIN"))) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }
  }
}


