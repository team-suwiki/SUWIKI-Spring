package usw.suwiki.statistics.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ApiLoggerService {
  private final ApiLoggerRepository apiLoggerRepository;

  @Async
  @Transactional
  public void logApi(MonitorTarget option, Long processTime) {
    Optional<ApiLogger> apiLogger = apiLoggerRepository.findByCallDate(LocalDate.now());

    if (apiLogger.isEmpty()) {
      try {
        apiLoggerRepository.save(newApiStatistics(option, processTime));
      } catch (DataIntegrityViolationException exception) {
        log.error("Try to Create Duplicated Unique Key Exception message : {}", exception.getMessage());
        logApi(option, processTime);
      }
      return;
    }
    apiLoggerRepository.save(oldApiStatistics(apiLogger.get(), option, processTime));
  }

  private ApiLogger newApiStatistics(MonitorTarget option, Long processTime) {
    return switch (option) {
      case LECTURE -> ApiLogger.lecture(processTime);
      case EVALUATE_POSTS -> ApiLogger.evaluate(processTime);
      case EXAM_POSTS -> ApiLogger.exam(processTime);
      case USER -> ApiLogger.user(processTime);
      case NOTICE -> ApiLogger.notice(processTime);
      default -> new ApiLogger();
    };
  }

  private ApiLogger oldApiStatistics(ApiLogger apiLogger, MonitorTarget option, Long processTime) {
    return switch (option) {
      case LECTURE -> apiLogger.logLecture(processTime);
      case EVALUATE_POSTS -> apiLogger.logEvaluatePosts(processTime);
      case EXAM_POSTS -> apiLogger.logExamPosts(processTime);
      case USER -> apiLogger.logUser(processTime);
      case NOTICE -> apiLogger.logNotice(processTime);
      default -> apiLogger;
    };
  }
}
