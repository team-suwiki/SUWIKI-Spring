package usw.suwiki.api.timetable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import usw.suwiki.auth.core.annotation.Authenticated;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.common.response.CommonResponse;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.lecture.timetable.dto.TimetableResponse;
import usw.suwiki.domain.lecture.timetable.service.TimetableService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/timetables")
@RequiredArgsConstructor
public class TimetableController {
  private final TimetableService timetableService;

  @Authorize
  @PostMapping
  @ResponseStatus(CREATED)
  public CommonResponse<?> createTimetable(@Authenticated Long userId, @Valid @RequestBody TimetableRequest.Description request) {
    timetableService.create(userId, request);
    return CommonResponse.success();
  }

  @Authorize
  @PostMapping("/bulk")
  @ResponseStatus(OK)
  public CommonResponse<?> bulkInsert(@Authenticated Long userId, @RequestBody List<TimetableRequest.Bulk> requests) {
    timetableService.bulkInsert(userId, requests);
    return CommonResponse.success();
  }

  @Authorize
  @PutMapping("/{timetableId}")
  @ResponseStatus(OK)
  public CommonResponse<?> updateTimetable(
    @Authenticated Long userId,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.Description request
  ) {
    timetableService.update(userId, timetableId, request);
    return CommonResponse.success();
  }

  @Authorize
  @DeleteMapping("/{timetableId}")
  @ResponseStatus(OK)
  public CommonResponse<?> deleteTimetable(@Authenticated Long userId, @PathVariable Long timetableId) {
    timetableService.delete(userId, timetableId);
    return CommonResponse.success();
  }

  @Authorize
  @GetMapping
  @ResponseStatus(OK)
  public CommonResponse<List<TimetableResponse.Simple>> getMyAllTimetables(@Authenticated Long userId) {
    var response = timetableService.getMyAllTimetables(userId);
    return CommonResponse.ok(response);
  }

  @Authorize
  @GetMapping("/{timetableId}")
  @ResponseStatus(OK)
  public CommonResponse<TimetableResponse.Detail> getTimetable(@PathVariable Long timetableId) {
    var response = timetableService.loadTimetable(timetableId);
    return CommonResponse.ok(response);
  }

  @Authorize
  @PostMapping("/{timetableId}/cells")
  @ResponseStatus(CREATED)
  public CommonResponse<?> addCell(
    @Authenticated Long userId,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.Cell request
  ) {
    timetableService.addCell(userId, timetableId, request);
    return CommonResponse.success();
  }

  @Authorize
  @PutMapping("/{timetableId}/cells/{cellIdx}")
  @ResponseStatus(OK)
  public CommonResponse<?> updateCell(
    @Authenticated Long userId,
    @PathVariable Long timetableId,
    @PathVariable int cellIdx,
    @Valid @RequestBody TimetableRequest.UpdateCell request
  ) {
    timetableService.updateCell(userId, timetableId, cellIdx, request);
    return CommonResponse.success();
  }

  @Authorize
  @DeleteMapping("/{timetableId}/cells/{cellIdx}")
  @ResponseStatus(OK)
  public CommonResponse<?> deleteCell(
    @Authenticated Long userId,
    @PathVariable Long timetableId,
    @PathVariable int cellIdx
  ) {
    timetableService.deleteCell(userId, timetableId, cellIdx);
    return CommonResponse.success();
  }
}
