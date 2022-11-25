-- Based on api.madgrades.com schema.
-- Use: mysqldump --no-data madgrades -p --compact
CREATE TABLE `courses` (
  `uuid` varchar(255) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  UNIQUE KEY `index_courses_on_uuid` (`uuid`)
);
CREATE TABLE `grade_distributions` (
  `course_offering_uuid` varchar(255) DEFAULT NULL,
  `section_number` int(11) DEFAULT NULL,
  `a_count` int(11) DEFAULT NULL,
  `ab_count` int(11) DEFAULT NULL,
  `b_count` int(11) DEFAULT NULL,
  `bc_count` int(11) DEFAULT NULL,
  `c_count` int(11) DEFAULT NULL,
  `d_count` int(11) DEFAULT NULL,
  `f_count` int(11) DEFAULT NULL,
  `s_count` int(11) DEFAULT NULL,
  `u_count` int(11) DEFAULT NULL,
  `cr_count` int(11) DEFAULT NULL,
  `n_count` int(11) DEFAULT NULL,
  `p_count` int(11) DEFAULT NULL,
  `i_count` int(11) DEFAULT NULL,
  `nw_count` int(11) DEFAULT NULL,
  `nr_count` int(11) DEFAULT NULL,
  `other_count` int(11) DEFAULT NULL,
  `gpa` decimal(7, 5) DEFAULT NULL,
  KEY `index_grade_distributions_on_course_offering_uuid` (`course_offering_uuid`),
  KEY `index_grade_distributions_on_section_number` (`section_number`)
);
CREATE TABLE `instructors` (
  `id` int(11) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  UNIQUE KEY `index_instructors_on_id` (`id`)
);
CREATE TABLE `rooms` (
  `uuid` varchar(255) DEFAULT NULL,
  `facility_code` varchar(255) DEFAULT NULL,
  `room_code` varchar(255) DEFAULT NULL,
  UNIQUE KEY `index_rooms_on_uuid` (`uuid`)
);
CREATE TABLE `schedules` (
  `uuid` varchar(255) DEFAULT NULL,
  `start_time` int(11) DEFAULT NULL,
  `end_time` int(11) DEFAULT NULL,
  `mon` tinyint(1) DEFAULT NULL,
  `tues` tinyint(1) DEFAULT NULL,
  `wed` tinyint(1) DEFAULT NULL,
  `thurs` tinyint(1) DEFAULT NULL,
  `fri` tinyint(1) DEFAULT NULL,
  `sat` tinyint(1) DEFAULT NULL,
  `sun` tinyint(1) DEFAULT NULL,
  UNIQUE KEY `index_schedules_on_uuid` (`uuid`)
);
CREATE TABLE `sections` (
  `uuid` varchar(255) DEFAULT NULL,
  `course_offering_uuid` varchar(255) DEFAULT NULL,
  `section_type` varchar(255) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `room_uuid` varchar(255) DEFAULT NULL,
  `schedule_uuid` varchar(255) DEFAULT NULL,
  UNIQUE KEY `index_sections_on_uuid` (`uuid`),
  KEY `index_sections_on_number` (`number`),
  KEY `index_sections_on_course_offering_uuid` (`course_offering_uuid`)
);
CREATE TABLE `subject_grades` (
  `subject_code` varchar(255) DEFAULT NULL,
  `gpa_total` int(11) DEFAULT NULL,
  `count_avg` decimal(6, 3) DEFAULT NULL,
  `gpa` decimal(10, 6) DEFAULT NULL
);
CREATE TABLE `subject_memberships` (
  `subject_code` varchar(255) DEFAULT NULL,
  `course_offering_uuid` varchar(255) DEFAULT NULL,
  KEY `index_subject_memberships_on_subject_code` (`subject_code`),
  KEY `index_subject_memberships_on_course_offering_uuid` (`course_offering_uuid`)
);
CREATE TABLE `subjects` (
  `code` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `abbreviation` varchar(255) DEFAULT NULL,
  UNIQUE KEY `index_subjects_on_code` (`code`),
  KEY `index_subjects_on_name` (`name`)
);
CREATE TABLE `teachings` (
  `section_uuid` varchar(255) DEFAULT NULL,
  `instructor_id` int(11) DEFAULT NULL,
  KEY `index_teachings_on_section_uuid` (`section_uuid`),
  KEY `index_teachings_on_instructor_id` (`instructor_id`)
);