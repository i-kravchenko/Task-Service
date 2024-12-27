INSERT INTO users(email, password) VALUES ('admin@mail.com', '{noop}admin');
INSERT INTO users(email, password) VALUES ('user@mail.com', '{noop}user');

INSERT INTO user_roles(user_id, role) VALUES (1, 'ROLE_ADMIN');
INSERT INTO user_roles(user_id, role) VALUES (2, 'ROLE_USER');