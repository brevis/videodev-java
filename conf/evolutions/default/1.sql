# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table category (
  slug                      varchar(255) not null,
  name                      varchar(255),
  constraint pk_category primary key (slug))
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
  content                   varchar(255),
  constraint pk_page primary key (slug))
;

create sequence category_seq;

create sequence member_seq;

create sequence page_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists category;

drop table if exists member;

drop table if exists page;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists category_seq;

drop sequence if exists member_seq;

drop sequence if exists page_seq;

