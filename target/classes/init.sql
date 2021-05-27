INSERT INTO owners (name)
VALUES ('Bob');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (500.33, '40884729571209875298', (SELECT id FROM owners WHERE name = 'Bob'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111111,
        SELECT id FROM accounts WHERE acc_number = '40884729571209875298');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (100000.77, '4088472957120987554', (SELECT id FROM owners WHERE name = 'Bob'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111101,
        SELECT id FROM accounts WHERE acc_number = '4088472957120987554');

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111001,
        SELECT id FROM accounts WHERE acc_number = '4088472957120987554');

INSERT INTO owners (name)
VALUES ('Josh');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (7000.89, '40803993847163759380', (SELECT id FROM owners WHERE name = 'Josh'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111110,
        SELECT id FROM accounts WHERE acc_number = '40803993847163759380');

INSERT INTO owners (name)
VALUES ('Mary');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (7980.13, '40803993847163759770', (SELECT id FROM owners WHERE name = 'Mary'));

INSERT INTO cards (card_number, account_id)
VALUES (1111110111111110,
        SELECT id FROM accounts WHERE acc_number = '40803993847163759770');

INSERT INTO contractors (owner_id, contractor_id)
VALUES (1, 2);
INSERT INTO contractors (owner_id, contractor_id)
VALUES (2, 1);
INSERT INTO contractors (owner_id, contractor_id)
VALUES (1, 3);