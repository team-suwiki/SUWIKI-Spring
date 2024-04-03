CREATE TABLE IF NOT EXISTS api_logger
(
    id                             bigint auto_increment primary key,
    call_date                      date             null,
    lecture_api_call_time          bigint default 0 null,
    lecture_api_process_avg        bigint default 0 null,
    evaluate_posts_api_call_time   bigint default 0 null,
    evaluate_posts_api_process_avg bigint default 0 null,
    exam_posts_api_call_time       bigint default 0 null,
    exam_posts_api_process_avg     bigint default 0 null,
    user_api_call_time             bigint default 0 null,
    user_api_process_avg           bigint default 0 null,
    notice_api_call_time           bigint default 0 null,
    notice_api_process_avg         bigint default 0 null,
    unique key (call_date)
);

CREATE TABLE IF NOT EXISTS blacklist_domain
(
    id            bigint auto_increment primary key,
    user_idx      bigint       null,
    hashed_email  varchar(200) null,
    banned_reason varchar(100) null,
    judgement     varchar(100) null,
    expired_at    datetime     null,
    created_at    datetime     null,
    updated_at    datetime     null
);

CREATE TABLE IF NOT EXISTS client_app_version
(
    client_app_version_id bigint auto_increment primary key,
    create_date           datetime(6)   null,
    modified_date         datetime(6)   null,
    description           varchar(2000) null,
    is_vital              bit           not null,
    os                    varchar(255)  not null,
    version_code          int           not null,
    unique (os, version_code)
);

CREATE TABLE IF NOT EXISTS confirmation_token
(
    id           bigint auto_increment primary key,
    user_idx     bigint       null,
    token        varchar(300) null,
    created_at   datetime     null,
    expires_at   datetime     null,
    confirmed_at datetime     null
);

CREATE TABLE IF NOT EXISTS evaluate_post_report
(
    id                 bigint auto_increment primary key,
    evaluate_idx       bigint       null,
    reported_user_idx  bigint       null,
    reporting_user_idx bigint       null,
    professor          varchar(100) null,
    lecture_name       varchar(100) null,
    reported_date      datetime     null,
    content            varchar(255) null
);

CREATE TABLE IF NOT EXISTS exam_post_report
(
    id                 bigint auto_increment primary key,
    exam_idx           bigint       null,
    reported_user_idx  bigint       null,
    reporting_user_idx bigint       null,
    professor          varchar(100) null,
    lecture_name       varchar(100) null,
    reported_date      datetime     null,
    content            varchar(255) null
);

CREATE TABLE IF NOT EXISTS lecture
(
    id                         bigint auto_increment primary key,
    semester_list              varchar(100) null,
    place_schedule             varchar(100) null,
    lecture_name               varchar(200) null,
    professor                  varchar(100) null,
    grade                      int          null,
    lecture_type               varchar(100) null,
    lecture_code               varchar(100) null,
    evaluate_type              varchar(100) null,
    dicl_no                    varchar(100) null,
    major_type                 varchar(100) null,
    point                      text         null,
    cappr_type                 text         null,
    posts_count                int          null,
    lecture_total_avg          float        null,
    lecture_satisfaction_avg   float        null,
    lecture_honey_avg          float        null,
    lecture_team_avg           float        null,
    lecture_learning_avg       float        null,
    lecture_difficulty_avg     float        null,
    lecture_homework_avg       float        null,
    lecture_satisfaction_value float        null,
    lecture_honey_value        float        null,
    lecture_team_value         float        null,
    lecture_learning_value     float        null,
    lecture_difficulty_value   float        null,
    lecture_homework_value     float        null,
    modified_date              datetime(6)  null,
    create_date                datetime(6)  null
);

CREATE TABLE IF NOT EXISTS lecture_schedule
(
    id             bigint auto_increment primary key,
    place_schedule varchar(255) not null,
    lecture_id     bigint       null,
    create_date    datetime(6)  null,
    modified_date  datetime(6)  null,
    semester       varchar(255) not null,
    foreign key (lecture_id) references lecture (id)
);

CREATE TABLE IF NOT EXISTS notice
(
    id            bigint auto_increment primary key,
    title         text     null,
    content       text     null,
    create_date   datetime null,
    modified_date datetime null
);

CREATE TABLE IF NOT EXISTS refresh_token
(
    id       bigint auto_increment primary key,
    payload  varchar(300) null,
    user_idx bigint       null
);

CREATE TABLE IF NOT EXISTS restricting_user
(
    id                 bigint auto_increment primary key,
    user_idx           bigint       null,
    restricting_date   datetime     null,
    restricting_reason varchar(100) null,
    judgement          varchar(100) null,
    created_at         datetime     null,
    updated_at         datetime     null
);

CREATE TABLE IF NOT EXISTS user
(
    id                  bigint auto_increment primary key,
    login_id            varchar(50)            null,
    password            varchar(100)           null,
    email               varchar(50)            null,
    restricted_count    int                    null,
    restricted          tinyint(1)             null,
    role                enum ('USER', 'ADMIN') null,
    written_evaluation  int                    null,
    written_exam        int                    null,
    view_exam_count     int                    null,
    point               int                    null,
    last_login          datetime               null,
    requested_quit_date datetime               null,
    created_at          datetime               null,
    updated_at          datetime               null
);

CREATE TABLE IF NOT EXISTS evaluate_post
(
    id                bigint auto_increment primary key,
    lecture_name      varchar(100) null,
    selected_semester varchar(100) null,
    professor         varchar(100) null,
    satisfaction      float        null,
    learning          float        null,
    honey             float        null,
    total_avg         float        null,
    team              int          null,
    difficulty        int          null,
    homework          int          null,
    content           text         null,
    lecture_id        bigint       null,
    user_idx          bigint       null,
    create_date       datetime     null,
    modified_date     datetime     null,
    foreign key (lecture_id) references lecture (id) on update cascade on delete cascade,
    foreign key (user_idx) references user (id)
);

create index lecture_id on evaluate_post (lecture_id);

create index user_idx on evaluate_post (user_idx);

CREATE TABLE IF NOT EXISTS exam_post
(
    id                bigint auto_increment primary key,
    lecture_name      varchar(200) null,
    selected_semester varchar(100) null,
    professor         varchar(100) null,
    exam_info         varchar(100) null,
    exam_type         varchar(100) null,
    exam_difficulty   varchar(100) null,
    content           text         null,
    create_date       datetime     null,
    modified_date     datetime     null,
    user_idx          bigint       null,
    lecture_id        bigint       null,
    foreign key (lecture_id) references lecture (id) on update cascade on delete cascade,
    foreign key (user_idx) references user (id)
);

create index lecture_id on exam_post (lecture_id);

create index user_idx on exam_post (user_idx);

CREATE TABLE IF NOT EXISTS favorite_major
(
    id         bigint auto_increment primary key,
    user_idx   bigint       null,
    major_type varchar(100) null,
    foreign key (user_idx) references user (id)
);

create index user_idx on favorite_major (user_idx);

CREATE TABLE IF NOT EXISTS timetable
(
    timetable_id  bigint auto_increment primary key,
    create_date   datetime(6)  null,
    modified_date datetime(6)  null,
    name          varchar(200) not null,
    semester      varchar(255) not null,
    year          int          not null,
    user_id       bigint       not null,
    foreign key (user_id) references user (id)
);

CREATE TABLE IF NOT EXISTS timetable_cells
(
    timetable_id   bigint      NOT NULL,
    cell_idx       integer     not null,
    lecture_name   varchar(50) NOT NULL,
    professor_name varchar(50) NOT NULL,
    location       varchar(50) NOT NULL,
    day            varchar(15) NOT NULL,
    color          varchar(15) NOT NULL,
    start_period   integer     NOT NULL,
    end_period     integer     NOT NULL,
    primary key (timetable_id, cell_idx)
);

CREATE TABLE IF NOT EXISTS user_isolation
(
    id                  bigint auto_increment primary key,
    user_idx            bigint       null,
    login_id            varchar(50)  null,
    password            varchar(100) null,
    email               varchar(50)  null,
    last_login          datetime     null,
    requested_quit_date datetime     null,
    unique key (login_id, email)
);

CREATE TABLE IF NOT EXISTS view_exam
(
    id            bigint auto_increment primary key,
    user_idx      bigint      null,
    lecture_id    bigint      null,
    create_date   datetime    null,
    modified_date datetime(6) null,
    foreign key (user_idx) references user (id),
    foreign key (lecture_id) references lecture (id) on update cascade on delete cascade
);

create index lecture_id on view_exam (lecture_id);

create index user_idx on view_exam (user_idx);
