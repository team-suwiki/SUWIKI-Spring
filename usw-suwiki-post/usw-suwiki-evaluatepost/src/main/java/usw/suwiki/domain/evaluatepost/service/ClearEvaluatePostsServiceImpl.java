package usw.suwiki.domain.evaluatepost.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import usw.suwiki.domain.evaluatepost.EvaluatePost;
import usw.suwiki.domain.evaluatepost.EvaluatePostRepository;
import usw.suwiki.domain.user.service.ClearEvaluatePostsService;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
class ClearEvaluatePostsServiceImpl implements ClearEvaluatePostsService {
  private final EvaluatePostRepository evaluatePostRepository;

  @Override
  public void clear(Long userId) {
    List<EvaluatePost> evaluatePosts = evaluatePostRepository.findAllByUserIdx(userId);
    evaluatePostRepository.deleteAllInBatch(evaluatePosts);
  }
}
