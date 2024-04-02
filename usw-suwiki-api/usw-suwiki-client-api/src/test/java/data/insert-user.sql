INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (1, 'user1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user1@suwon.ac.kr',
        0, 0, 'ADMIN',
        0, 0, 0, 100,
        now(), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (2, 'user2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge',
        'user2@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        now(), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (3, 'user3', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user3@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        now(), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (4, 'user4', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'user4@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        now(), null, now(), now());


INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (5, 'soonIsolationUser1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(DATE_SUB(NOW(), INTERVAL 11 MONTH), INTERVAL 1 DAY), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (6, 'soonIsolationUser2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'peter6081@gmail.com',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_ADD(DATE_SUB(NOW(), INTERVAL 11 MONTH), INTERVAL 1 DAY), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (7, 'isolationTarget1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 12 MONTH), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (8, 'isolationTarget2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', 'peter6081@gmail.com',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 12 MONTH), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (9, 'soonAutoDeletedUser1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge',
        '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 35 MONTH), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (10, 'soonAutoDeletedUser2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge',
        '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 35 MONTH), null, now(), now());


INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (11, 'deleteTarget1', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 37 MONTH), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (12, 'deleteTarget2', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 37 MONTH), null, now(), now());

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (13, null, null, null,
        0, 0, 'USER',
        0, 0, 0, 100,
        DATE_SUB(NOW(), INTERVAL 37 MONTH), null, now(), now());

INSERT INTO user_isolation(id, user_idx, login_id, password, email, last_login, requested_quit_date)
VALUES (1, 13, '자고있던 유저', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        DATE_SUB(NOW(), INTERVAL 37 MONTH), null);

INSERT INTO user(id, login_id, password, email,
                 restricted_count, restricted, role,
                 written_evaluation, written_exam, view_exam_count, point,
                 last_login, requested_quit_date, created_at, updated_at)
VALUES (14, 'adminUser', '$2a$10$zkHb4uI39RHMdn4OdGRAg.1vQwDl1K4h3DCkSsiWN.vnhBw2Ux7Ge', '18018008@suwon.ac.kr',
        0, 0, 'ADMIN',
        0, 0, 0, 100,
        NOW(), null, now(), now());
