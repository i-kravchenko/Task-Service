INSERT INTO task(
	author_id, responsible_id, title, description, priority, status)
	VALUES (1, 1, 'Admins task', 'Admins task description', 'LOW', 'PENDING');

INSERT INTO task(
	author_id, responsible_id, title, description, priority, status)
	VALUES (1, 2, 'Users task', 'Users task description', 'LOW', 'PENDING');

INSERT INTO comment(
	author_id, task_id, text)
	VALUES (1, 1, 'Admins comment');

INSERT INTO comment(
	author_id, task_id, text)
	VALUES (1, 2, 'Admins request comment');

INSERT INTO comment(
	author_id, task_id, text)
	VALUES (2, 2, 'Users response comment');

