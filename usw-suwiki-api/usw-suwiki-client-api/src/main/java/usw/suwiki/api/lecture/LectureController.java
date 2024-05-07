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
import usw.suwiki.common.response.ApiResponse;
import usw.suwiki.core.secure.TokenAgent;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.schedule.service.LectureScheduleService;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.statistics.annotation.CacheStatics;
import usw.suwiki.statistics.annotation.Monitoring;

import static usw.suwiki.statistics.log.MonitorOption.LECTURE;

@RestController
@RequestMapping(value = "/lecture")
@RequiredArgsConstructor
public class LectureController {
  private final LectureService lectureService;
  private final LectureScheduleService lectureScheduleService;
  private final TokenAgent tokenAgent;

  @GetMapping("/current/cells/search") // (03.18) 이것만큼은 건들면 안된다.
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<LectureResponse.ScheduledLecture> searchTimetableCells(
    @RequestParam(required = false) Long cursorId,
    @RequestParam(required = false, defaultValue = "20") Integer size,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String major,
    @RequestParam(required = false) Integer grade
  ) {
    var response = lectureScheduleService.findPagedLecturesBySchedule(cursorId, size, keyword, major, grade);
    return ApiResponse.ok(response);
  }

  @Monitoring(option = LECTURE)
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public LectureResponse.Simples search(
    @RequestParam String searchValue,
    @RequestParam(required = false) String option,
    @RequestParam(required = false) Integer page,
    @RequestParam(required = false) String majorType
  ) {
    LectureSearchOption findOption = new LectureSearchOption(option, page, majorType);
    return lectureService.loadAllLecturesByKeyword(searchValue, findOption);
  }

  @CacheStatics
  @Cacheable(cacheNames = "lecture")
  @Monitoring(option = LECTURE)
  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public LectureResponse.Simples getMainPageLectures(
    @RequestParam(required = false) String option,
    @RequestParam(required = false) Integer page,
    @RequestParam(required = false) String majorType
  ) {
    LectureSearchOption findOption = new LectureSearchOption(option, page, majorType);
    return lectureService.loadAllLectures(findOption);
  }

  @Monitoring(option = LECTURE)
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<LectureResponse.Detail> getDetail(
    @RequestHeader String Authorization,
    @RequestParam Long lectureId
  ) {
    tokenAgent.validateRestrictedUser(Authorization);
    return ApiResponse.ok(lectureService.loadLectureDetail(lectureId));
  }
}
