package usw.suwiki.domain.lecture.schedule.service;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.core.exception.ExceptionType;
import usw.suwiki.core.exception.LectureException;
import usw.suwiki.domain.lecture.Lecture;
import usw.suwiki.domain.lecture.LectureQueryRepository;
import usw.suwiki.domain.lecture.LectureRepository;
import usw.suwiki.domain.lecture.SemesterProvider;
import usw.suwiki.domain.lecture.dto.LectureResponse;
import usw.suwiki.domain.lecture.schedule.LectureSchedule;
import usw.suwiki.domain.lecture.schedule.LectureScheduleQueryRepository;
import usw.suwiki.domain.lecture.schedule.LectureScheduleRepository;
import usw.suwiki.domain.lecture.schedule.data.JsonLecture;
import usw.suwiki.domain.lecture.schedule.data.USWTermResolver;
import usw.suwiki.domain.lecture.schedule.model.LectureInfo;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LectureScheduleService {
  private final LectureScheduleQueryRepository lectureScheduleQueryRepository;
  private final LectureScheduleRepository lectureScheduleRepository;
  private final LectureQueryRepository lectureQueryRepository;
  private final LectureRepository lectureRepository;

  private final SemesterProvider semesterProvider;

  public LectureResponse.ScheduledLecture findPagedLecturesBySchedule(Long cursorId, int size, String keyword, String major, Integer grade) {
    return lectureScheduleQueryRepository.findCurrentSemesterLectures(cursorId, size, keyword, major, grade);
  }

  @Async
  @Transactional(propagation = Propagation.MANDATORY)
  public void bulkApplyJsonLectures(String filePath) {
    List<JsonLecture> jsonLectures = deserializeJsonFromPath(filePath).stream()
      .map(rawObject -> USWTermResolver.resolve((JSONObject) rawObject))
      .toList();

    deleteAllRemovedLectures(jsonLectures);
    deleteAllRemovedLectureSchedules(jsonLectures);
    jsonLectures.forEach(this::insertJsonLectureOrLectureSchedule);
  }

  private JSONArray deserializeJsonFromPath(String filePath) {
    try {
      Reader reader = new FileReader(filePath);
      JSONParser parser = new JSONParser();
      return (JSONArray) parser.parse(reader);
    } catch (IOException | ParseException ex) {
      ex.printStackTrace();
      throw new LectureException(ExceptionType.SERVER_ERROR); // todo: do not throw server error
    }
  }

  private void deleteAllRemovedLectures(List<JsonLecture> jsonLectures) {
    lectureRepository.findAllBySemesterContains(semesterProvider.current()).stream()
      .filter(lecture -> jsonLectures.stream().noneMatch(json -> lecture.isEquals(
        json.getLectureName(),
        json.getProfessor(),
        json.getMajorType(),
        json.getDividedClassNumber()
      )))
      .forEach(lecture -> {
        if (lecture.isOld()) {
          lecture.removeSemester(semesterProvider.current());
        } else {
          lectureRepository.delete(lecture);
        }
      });
  }

  private void deleteAllRemovedLectureSchedules(List<JsonLecture> jsonLectures) {
    List<Long> scheduleIds = lectureScheduleQueryRepository.findAllLectureInfosBySemester(semesterProvider.current()).stream()
      .filter(info -> jsonLectures.stream().noneMatch(jsonLecture -> jsonLecture.isInfoEquals(info)))
      .map(LectureInfo::scheduleId)
      .toList();

    lectureScheduleRepository.deleteAllByIdInBatch(scheduleIds);

//    검증 이후 삭제
//    List<LectureSchedule> schedules =
//      lectureScheduleQueryRepository.findAllSchedulesBySemesterContains(semesterProvider.semester()).stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
//        .filter(schedule -> jsonLectures.stream().noneMatch(jsonLecture -> jsonLecture.isLectureAndPlaceScheduleEqual(schedule)))
//        .toList();
//    lectureScheduleRepository.deleteAllInBatch(schedules);
  }

  private void insertJsonLectureOrLectureSchedule(JsonLecture jsonLecture) {
    Optional<Lecture> optionalLecture = lectureQueryRepository.findByExtraKeys(
      jsonLecture.getLectureName(),
      jsonLecture.getProfessor(),
      jsonLecture.getMajorType(),
      jsonLecture.getDividedClassNumber()
    );

    if (optionalLecture.isPresent()) {
      extendSemesterOfLecture(optionalLecture.get(), jsonLecture);
    } else {
      Lecture saved = lectureRepository.save(LectureScheduleMapper.toLecture(jsonLecture));
      saveLectureSchedule(saved.getId(), jsonLecture);
    }
  }

  private void extendSemesterOfLecture(Lecture lecture, JsonLecture jsonLecture) {
    lecture.addSemester(jsonLecture.getSelectedSemester());

    List<LectureInfo> infos = lectureScheduleQueryRepository.findAllLectureInfosById(lecture.getId());
    if (infos.stream().noneMatch(jsonLecture::isInfoEquals)) {
      saveLectureSchedule(lecture.getId(), jsonLecture);
    }
//    검증 이후 삭제
//    List<LectureSchedule> schedules = lectureScheduleQueryRepository.findAllByLectureId(lecture.getId());
//    if (schedules.stream().noneMatch(jsonLecture::isLectureAndPlaceScheduleEqual)) {
//      saveLectureSchedule(lecture.getId(), jsonLecture);
//    }
  }

  private void saveLectureSchedule(Long lectureId, JsonLecture jsonLecture) {
    if (jsonLecture.isValidPlaceSchedule()) {
      LectureSchedule schedule = new LectureSchedule(lectureId, jsonLecture.getPlaceSchedule(), semesterProvider.current());
      lectureScheduleRepository.save(schedule);
    }
  }

//  private List<LectureSchedule> resolveDeletedLectureScheduleList(
//    List<JsonLecture> jsonLectures,
//    List<LectureSchedule> currentSemesterLectureSchedules
//  ) {
//    return currentSemesterLectureSchedules.stream() // 기존의 스케줄이 삭제된 케이스 필터링 : O(N^2) 비교
//      .filter(it -> jsonLectures.stream().noneMatch(vo -> vo.isLectureAndPlaceScheduleEqual(it)))
//      .toList();
//  }
}
