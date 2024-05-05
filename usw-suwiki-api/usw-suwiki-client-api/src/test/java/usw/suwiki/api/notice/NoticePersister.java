package usw.suwiki.api.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import usw.suwiki.domain.notice.Notice;
import usw.suwiki.domain.notice.NoticeRepository;


@Component
@RequiredArgsConstructor
public class NoticePersister {
  private final NoticeRepository noticeRepository;

  public NoticeBuilder builder() {
    return new NoticeBuilder();
  }

  public final class NoticeBuilder {

    private String title;
    private String content;

    public NoticeBuilder setTitle(String title) {
      this.title = title;
      return this;
    }

    public NoticeBuilder setContent(String content) {
      this.content = content;
      return this;
    }

    public Notice save() {
      Notice notice = new Notice(
        (title == null ? "공지사항 제목" : title),
        (content == null ? "공지사항 내용" : content)
      );

      noticeRepository.save(notice);
      return notice;
    }

  }
}
