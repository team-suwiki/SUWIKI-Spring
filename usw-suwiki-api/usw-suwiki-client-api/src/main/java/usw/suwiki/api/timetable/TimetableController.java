package usw.suwiki.api.timetable;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import usw.suwiki.auth.core.jwt.JwtAgent;
import usw.suwiki.common.request.BulkRequest;
import usw.suwiki.common.response.ApiResponse;
import usw.suwiki.domain.lecture.timetable.dto.TimetableRequest;
import usw.suwiki.domain.lecture.timetable.dto.TimetableResponse;
import usw.suwiki.domain.lecture.timetable.service.TimetableService;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/timetables")
@RequiredArgsConstructor
public class TimetableController {
  private final TimetableService timetableService;
  private final JwtAgent jwtAgent;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<?> createTimetable(
    @RequestHeader String authorization,
    @Valid @RequestBody TimetableRequest.Description request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    timetableService.create(userId, request);
    return ApiResponse.success();
  }

  @PostMapping("/bulk")
  public ApiResponse<?> bulkCreateTimetables(
    @RequestHeader String authorization,
    @Valid @RequestBody BulkRequest<TimetableRequest.Bulk> request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    timetableService.bulkCreate(userId, request.getBulk());
    return ApiResponse.success();
  }

  @PutMapping("/{timetableId}")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<?> updateTimetable(
    @PathVariable Long timetableId,
    @RequestHeader String authorization,
    @Valid @RequestBody TimetableRequest.Description request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    timetableService.update(userId, timetableId, request);
    return ApiResponse.success();
  }

  @DeleteMapping("/{timetableId}")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<?> deleteTimetable(
    @PathVariable Long timetableId,
    @RequestHeader String authorization
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);

    timetableService.delete(userId, timetableId);
    return ApiResponse.success();
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<List<TimetableResponse.Simple>> getMyAllTimetables(@RequestHeader String authorization) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    return ApiResponse.ok(timetableService.getMyAllTimetables(userId));
  }

  @GetMapping("/{timetableId}")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<TimetableResponse.Detail> getTimetable(
    @RequestHeader String authorization,
    @PathVariable Long timetableId
  ) {
    jwtAgent.validateJwt(authorization);
    return ApiResponse.ok(timetableService.loadTimetable(timetableId));
  }

  @PostMapping("/{timetableId}/cells")
  @ResponseStatus(HttpStatus.CREATED)
  public ApiResponse<?> insertCell(
    @RequestHeader String authorization,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.Cell request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    timetableService.addCell(userId, timetableId, request);
    return ApiResponse.success();
  }

  @PutMapping("/{timetableId}/cells")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<?> updateCell(
    @RequestHeader String authorization,
    @PathVariable Long timetableId,
    @Valid @RequestBody TimetableRequest.UpdateCell request
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    timetableService.updateCell(userId, timetableId, request);
    return ApiResponse.success();
  }

  @DeleteMapping("/{timetableId}/cells/{cellIdx}")
  @ResponseStatus(HttpStatus.OK)
  public ApiResponse<?> deleteCell(
    @RequestHeader String authorization,
    @PathVariable Long timetableId,
    @PathVariable int cellIdx
  ) {
    jwtAgent.validateJwt(authorization);
    Long userId = jwtAgent.getId(authorization);
    timetableService.deleteCell(userId, timetableId, cellIdx);
    return ApiResponse.success();
  }
}
