package usw.suwiki.api.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.response.ApiResponse;
import usw.suwiki.common.response.ResponseForm;
import usw.suwiki.core.exception.AccountException;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.schedule.service.LectureScheduleService;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.statistics.annotation.ApiLogger;
import usw.suwiki.statistics.annotation.CacheStatics;

@RestController
@RequestMapping(value = "/lecture")
@RequiredArgsConstructor
public class LectureController {
  private final LectureService lectureService;
  private final LectureScheduleService lectureScheduleService;
  private final JwtAgent jwtAgent;

  @ApiLogger(option = "lecture")
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public LectureResponse.Simples searchLectureApi(
    @RequestParam String searchValue,
    @RequestParam(required = false) String option,
    @RequestParam(required = false) Integer page,
    @RequestParam(required = false) String majorType
  ) {
    LectureSearchOption findOption = new LectureSearchOption(option, page, majorType);
    return lectureService.loadAllLecturesByKeyword(searchValue, findOption);
  }

  @GetMapping("/current/cells/search") // todo: (03.18) 이것만큼은 건들면 안된다.
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<LectureResponse.Lectures> searchLectureCells(
    @RequestParam(required = false) Long cursorId,
    @RequestParam(required = false, defaultValue = "20") Integer size,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String major,
    @RequestParam(required = false) Integer grade
  ) {
    LectureResponse.Lectures response =
      lectureScheduleService.findPagedLecturesBySchedule(cursorId, size, keyword, major, grade);
    return ApiResponse.ok(response);
  }

  @CacheStatics
  @Cacheable(cacheNames = "lecture")
  @ApiLogger(option = "lecture")
  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public LectureResponse.Simples findAllLectureApi(
    @RequestParam(required = false) String option,
    @RequestParam(required = false) Integer page,
    @RequestParam(required = false) String majorType
  ) {
    LectureSearchOption findOption = new LectureSearchOption(option, page, majorType);
    return lectureService.loadAllLectures(findOption);
  }

  @ApiLogger(option = "lecture")
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ResponseForm findLectureByLectureId(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId
  ) {
    if (jwtAgent.isRestrictedUser(Authorization)) {
      throw new AccountException(ExceptionType.USER_RESTRICTED);
    }

    return new ResponseForm(lectureService.loadLectureDetail(lectureId));
  }
}
