# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  slug                      varchar(255) not null,
  name                      varchar(255),
  constraint pk_category primary key (slug))
;

create table course (
  id                        integer not null,
  type                      integer,
  category_slug             varchar(255),
  title                     varchar(255),
  description               TEXT,
  member_facebook_id        varchar(255),
  cover_id                  integer,
  update_date               timestamp,
  views_count               integer,
  constraint ck_course_type check (type in (0,1)),
  constraint pk_course primary key (id))
;

create table cover (
  id                        integer not null,
  data                      bytea,
  content_type              varchar(255),
  url                       varchar(255),
  constraint uq_cover_1 unique (url),
  constraint pk_cover primary key (id))
;

create table lesson (
  id                        integer not null,
  title                     varchar(255),
  player_code               TEXT,
  postdate                  timestamp,
  course_id                 integer,
  constraint pk_lesson primary key (id))
;

create table member (
  facebook_id               varchar(255) not null,
  first_name                varchar(255),
  last_name                 varchar(255),
  email                     varchar(255),
  registration_date         timestamp,
  last_login_date           timestamp,
  constraint pk_member primary key (facebook_id))
;

create table page (
  slug                      varchar(255) not null,
  title                     varchar(255),
  content                   TEXT,
  constraint pk_page primary key (slug))
;


create table member_lesson (
  member_facebook_id             varchar(255) not null,
  lesson_id                      integer not null,
  constraint pk_member_lesson primary key (member_facebook_id, lesson_id))
;

create table member_course (
  member_facebook_id             varchar(255) not null,
  course_id                      integer not null,
  constraint pk_member_course primary key (member_facebook_id, course_id))
;
create sequence category_seq;

create sequence course_seq;

create sequence cover_seq;

create sequence lesson_seq;

create sequence member_seq;

create sequence page_seq;

alter table course add constraint fk_course_category_1 foreign key (category_slug) references category (slug);
create index ix_course_category_1 on course (category_slug);
alter table course add constraint fk_course_member_2 foreign key (member_facebook_id) references member (facebook_id);
create index ix_course_member_2 on course (member_facebook_id);
alter table course add constraint fk_course_cover_3 foreign key (cover_id) references cover (id);
create index ix_course_cover_3 on course (cover_id);
alter table lesson add constraint fk_lesson_course_4 foreign key (course_id) references course (id);
create index ix_lesson_course_4 on lesson (course_id);



alter table member_lesson add constraint fk_member_lesson_member_01 foreign key (member_facebook_id) references member (facebook_id);

alter table member_lesson add constraint fk_member_lesson_lesson_02 foreign key (lesson_id) references lesson (id);

alter table member_course add constraint fk_member_course_member_01 foreign key (member_facebook_id) references member (facebook_id);

alter table member_course add constraint fk_member_course_course_02 foreign key (course_id) references course (id);

# --- !Downs

drop table if exists category cascade;

drop table if exists course cascade;

drop table if exists cover cascade;

drop table if exists lesson cascade;

drop table if exists member cascade;

drop table if exists member_lesson cascade;

drop table if exists member_course cascade;

drop table if exists page cascade;

drop sequence if exists category_seq;

drop sequence if exists course_seq;

drop sequence if exists cover_seq;

drop sequence if exists lesson_seq;

drop sequence if exists member_seq;

drop sequence if exists page_seq;

