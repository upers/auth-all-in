INSERT INTO privilege (id, name) VALUES (nextval('privilege_sequences'), 'write');
INSERT INTO privilege (id, name) VALUES (nextval('privilege_sequences'),'read');
INSERT INTO privilege (id, name) VALUES (nextval('privilege_sequences'),'view_users');
INSERT INTO privilege (id, name) VALUES (nextval('privilege_sequences'),'edit_users');
INSERT INTO privilege (id, name) VALUES (nextval('privilege_sequences'),'edit_roles');
INSERT INTO role (id, name) VALUES (nextval('role_sequences'), 'admin');
INSERT INTO role (id, name) VALUES (nextval('role_sequences'), 'user');
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 1);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 2);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 3);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 4);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (1, 5);
INSERT INTO roles_privileges (role_id, privilege_id) VALUES (2, 2);
