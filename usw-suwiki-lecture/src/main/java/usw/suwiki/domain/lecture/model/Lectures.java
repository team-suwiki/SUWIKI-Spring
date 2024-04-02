package usw.suwiki.domain.lecture.model;

import usw.suwiki.domain.lecture.Lecture;

import java.util.List;

/**
 * 레거시 코드 호환용 객체
 *
 * @author hejow
 */
public record Lectures(
  List<Lecture> content,
  Long count
) {
}
