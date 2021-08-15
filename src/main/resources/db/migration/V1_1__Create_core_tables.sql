create sequence user_sequences;
CREATE TABLE user_account
(
    id                bigint  NOT NULL primary key,
    email             character varying(255),
    enabled           boolean NOT NULL,
    first_name        character varying(255),
    is_using2fa       boolean NOT NULL,
    last_name         character varying(255),
    password          character varying(60),
    registration_time timestamp without time zone,
    unique (email)
);
create sequence password_reset_token_sequences;
CREATE TABLE password_reset_token
(
    id          bigint NOT NULL primary key,
    expiry_date timestamp without time zone,
    token       character varying(255),
    user_id     bigint NOT NULL REFERENCES user_account (id),
    unique (user_id),
    unique (token)
);
create sequence role_sequences;
CREATE TABLE role
(
    id   bigint NOT NULL primary key,
    name character varying(255)
);
create sequence privilege_sequences;
CREATE TABLE privilege
(
    id   bigint NOT NULL primary key,
    name character varying(255)
);
CREATE TABLE roles_privileges
(
    role_id      bigint NOT NULL REFERENCES role (id),
    privilege_id bigint NOT NULL REFERENCES privilege (id),
    CONSTRAINT roles_privileges_pkey PRIMARY KEY (role_id, privilege_id)
);
CREATE TABLE users_roles
(
    user_id bigint NOT NULL REFERENCES user_account (id),
    role_id bigint NOT NULL REFERENCES role (id),
    CONSTRAINT users_roles_pkey PRIMARY KEY (user_id, role_id)
);

create sequence verification_token_sequences;
CREATE TABLE verification_token
(
    id          bigint NOT NULL primary key,
    expiry_date timestamp without time zone,
    token       character varying(255),
    user_id     bigint NOT NULL REFERENCES user_account (id),
    unique (user_id),
    unique (token)
);