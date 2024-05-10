package usw.suwiki.api.timetable;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.annotation.Authorize;
import usw.suwiki.auth.core.annotation.Login;
import usw.suwiki.common.response.ApiResponse;
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
  public ApiResponse<?> createTimetable(@Login Long userId, @Valid @RequestBody TimetableRequest.Description request) {
    timetableService.create(userId, request);
    return ApiResponse.success();
  }

  @Authorize
  @PostMapping("/bulk")
  @ResponseStatus(OK)
  public ApiResponse<?> bulkInsert(@Login Long userId, @RequestBody List<TimetableRequest.Bulk> requests) {
    timetableService.bulkInsert(userId, requests);
    return ApiResponse.success();
  }

  @Authorize
  @PutMapping("/{timetableId}")
  @ResponseStatus(OK)
  public ApiResponse<?> updateTimetable(
    @Login Long userId,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.Description request
  ) {
    timetableService.update(userId, timetableId, request);
    return ApiResponse.success();
  }

  @Authorize
  @DeleteMapping("/{timetableId}")
  @ResponseStatus(OK)
  public ApiResponse<?> deleteTimetable(@Login Long userId, @PathVariable Long timetableId) {
    timetableService.delete(userId, timetableId);
    return ApiResponse.success();
  }

  @Authorize
  @GetMapping
  @ResponseStatus(OK)
  public ApiResponse<List<TimetableResponse.Simple>> getMyAllTimetables(@Login Long userId) {
    var response = timetableService.getMyAllTimetables(userId);
    return ApiResponse.ok(response);
  }

  @Authorize
  @GetMapping("/{timetableId}")
  @ResponseStatus(OK)
  public ApiResponse<TimetableResponse.Detail> getTimetable(@PathVariable Long timetableId) {
    var response = timetableService.loadTimetable(timetableId);
    return ApiResponse.ok(response);
  }

  @Authorize
  @PostMapping("/{timetableId}/cells")
  @ResponseStatus(CREATED)
  public ApiResponse<?> insertCell(
    @Login Long userId,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.Cell request
  ) {
    timetableService.addCell(userId, timetableId, request);
    return ApiResponse.success();
  }

  @Authorize
  @PutMapping("/{timetableId}/cells/{cellIdx}")
  @ResponseStatus(OK)
  public ApiResponse<?> updateCell(
    @Login Long userId,
    @PathVariable Long timetableId,
    @PathVariable int cellIdx,
    @Valid @RequestBody TimetableRequest.UpdateCell request
  ) {
    timetableService.updateCell(userId, timetableId, cellIdx, request);
    return ApiResponse.success();
  }

  @Authorize
  @DeleteMapping("/{timetableId}/cells/{cellIdx}")
  @ResponseStatus(OK)
  public ApiResponse<?> deleteCell(
    @Login Long userId,
    @PathVariable Long timetableId,
    @PathVariable int cellIdx
  ) {
    timetableService.deleteCell(userId, timetableId, cellIdx);
    return ApiResponse.success();
  }
}
