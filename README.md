# SUWIKI

수원대학교 학생들을 위한 강의평가 & 시간표 서비스

### About modules

프로젝트의 모듈은 다음 기준으로 나뉘어져 있습니다. (변경될 수 있습니다.)

- Application Module : 하나의 `API` end-point
- Single Module : 하나의 `jar` 단위. 기능에 따라 나눌 수 있다.
    - Support Module : 독립적으로 기능 ❌
    - Function Module : 독립적으로 기능 ⭕️
    - Domain Module : **특정 애그리거트를 담당**. 하위 엔티티를 포함할 수 있다.

모듈의 의존성은 각 부모 모듈에서 하나로 관리합니다. 부모 모듈에 작성된 `README.md`를 참고바랍니다. 
