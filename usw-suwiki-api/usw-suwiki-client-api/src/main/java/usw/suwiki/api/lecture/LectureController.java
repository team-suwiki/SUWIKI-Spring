package usw.suwiki.api.lecture;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.dto.LectureSearchOption;
import usw.suwiki.domain.lecture.schedule.service.LectureScheduleService;
import usw.suwiki.domain.lecture.service.LectureService;
import usw.suwiki.statistics.annotation.CacheStatics;
import usw.suwiki.statistics.annotation.Statistics;

import static usw.suwiki.statistics.log.MonitorTarget.LECTURE;

@RestController
@RequestMapping(value = "/lecture")
@RequiredArgsConstructor
public class LectureController {
  private final LectureService lectureService;
  private final LectureScheduleService lectureScheduleService;

  @GetMapping("/current/cells/search") // (03.18) 이것만큼은 건들면 안된다.
  @ResponseStatus(HttpStatus.OK)
  public CommonResponse<LectureResponse.ScheduledLecture> searchTimetableCells(
    @RequestParam(required = false) Long cursorId,
    @RequestParam(required = false, defaultValue = "20") Integer size,
    @RequestParam(required = false) String keyword,
    @RequestParam(required = false) String major,
    @RequestParam(required = false) Integer grade
  ) {
    var response = lectureScheduleService.findPagedLecturesBySchedule(cursorId, size, keyword, major, grade);
    return CommonResponse.ok(response);
  }

  @Statistics(LECTURE)
  @GetMapping("/search")
  @ResponseStatus(HttpStatus.OK)
  public CommonResponse<LectureResponse.Simples> search(
    @RequestParam String keyword,
    @ModelAttribute LectureSearchOption option
  ) {
    var response = lectureService.loadAllLecturesByKeyword(keyword, option);
    return CommonResponse.ok(response);
  }

  @CacheStatics
  @Cacheable(cacheNames = "lecture")
  @Statistics(LECTURE)
  @GetMapping("/all")
  @ResponseStatus(HttpStatus.OK)
  public CommonResponse<LectureResponse.Simples> getMainPageLectures(@ModelAttribute LectureSearchOption option) {
    var response = lectureService.loadAllLectures(option);
    return CommonResponse.ok(response);
  }

  @Authorize
  @Statistics(LECTURE)
  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public CommonResponse<LectureResponse.Detail> getDetail(@RequestParam Long lectureId) {
    var response = lectureService.loadLectureDetail(lectureId);
    return CommonResponse.ok(response);
  }
}
