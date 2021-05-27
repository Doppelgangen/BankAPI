INSERT INTO owners (name)
VALUES ('Timmy');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (100, '40884729571209875298', (SELECT id FROM owners WHERE name = 'Timmy'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111111,
        SELECT id FROM accounts WHERE acc_number = '40884729571209875298');

INSERT INTO owners (name)
VALUES ('Johnny');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (200, '4088472957120987554', (SELECT id FROM owners WHERE name = 'Johnny'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111001,
        SELECT id FROM accounts WHERE acc_number = '4088472957120987554');

INSERT INTO owners (name)
VALUES ('Claire');

INSERT INTO accounts(balance, acc_number, owner_id)
VALUES (700, '40803993847163759380', (SELECT id FROM owners WHERE name = 'Claire'));

INSERT INTO cards (card_number, account_id)
VALUES (1111111111111110,
        SELECT id FROM accounts WHERE acc_number = '40803993847163759380');

INSERT INTO contractors (owner_id, contractor_id)
VALUES (1, 2);
INSERT INTO contractors (owner_id, contractor_id)
VALUES (2, 1);