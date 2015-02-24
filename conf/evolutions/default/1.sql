# Users schema

# --- !Ups

CREATE TABLE "User" (
    id                  SERIAL PRIMARY KEY,
    facebookId          INTEGER NOT NULL,
    firstName           VARCHAR (50) NOT NULL,
    lastName            VARCHAR (50) NOT NULL,
    email               VARCHAR (100) NOT NULL,
    avatar              VARCHAR (255) NOT NULL,
    registrationDate    TIMESTAMP NOT NULL,
    lastLoginDate       TIMESTAMP
);

# --- !Downs

DROP TABLE "User";


# Categories schema

# --- !Ups

CREATE TABLE "Category" (
    id                  SERIAL PRIMARY KEY,
    name                VARCHAR (100) NOT NULL,
    slug                VARCHAR (30) NOT NULL,
    UNIQUE(slug)
);

# --- !Downs

DROP TABLE "Category";
