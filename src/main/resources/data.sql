INSERT INTO authority_table
VALUES(1, 'ROLE_ADMIN');

INSERT INTO authority_table
VALUES(2, 'ROLE_USER');

INSERT INTO user_table (user_id, first_name, last_name, username, password)
VALUES(101L, 'nabid', 'akash', 'koios', 'iamadmin');

INSERT INTO user_table (user_id, first_name, last_name, username, password)
VALUES(102L, 'nabid', 'akash1', 'nabid', 'iamuser');

INSERT INTO user_authority (user_id, authority_id)
VALUES(101L, 1);

INSERT INTO user_authority (user_id, authority_id)
VALUES(101L, 2);

INSERT INTO user_authority (user_id, authority_id)
VALUES(102L, 2);