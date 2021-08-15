-- password is 'admin'
INSERT INTO user_account (id, email, enabled, first_name, is_using2fa, last_name, password) VALUES (nextval('user_sequences'), 'uper4206@gmail.com', true, 'Misha', false, 'Savchuk', '$2a$11$A8Kgba3ABd6HIDMHFeO1R.3K6SoUhsFBK..S0jJ8FKdYHqm52HUQW');
INSERT INTO users_roles (user_id, role_id) VALUES (1, 1);
-- password is 'user'
INSERT INTO user_account (id, email, enabled, first_name, is_using2fa, last_name, password) VALUES (nextval('user_sequences'), 'like_fozzyyyy@bigmir.net', true, 'Misha', false, 'Savchuk', '$2a$11$zFR3TuAc3C7u0C6/rg3LzOnu28wHYNe3iYCIsv0KVSinYF3cvYxwy');
INSERT INTO users_roles (user_id, role_id) VALUES (2, 2);

INSERT INTO user_account (id, email, enabled, first_name, is_using2fa, last_name, password) VALUES (nextval('user_sequences'), 'admin@admin.net', true, 'Misha', false, 'Savchuk', '$2a$11$zFR3TuAc3C7u0C6/rg3LzOnu28wHYNe3iYCIsv0KVSinYF3cvYxwy');
INSERT INTO users_roles (user_id, role_id) VALUES (3, 2);