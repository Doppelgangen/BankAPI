CREATE TABLE IF NOT EXISTS owners
(
    id   IDENTITY     NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS accounts
(
    id         IDENTITY    NOT NULL PRIMARY KEY,
    acc_number VARCHAR(20) NOT NULL,
    balance    DECIMAL     NOT NULL,
    owner_id   BIGINT      NOT NULL,
    CONSTRAINT FK_OWNER FOREIGN KEY (owner_id) REFERENCES owners (id),
    UNIQUE KEY acc_number_UNIQUE (acc_number)
);

CREATE TABLE IF NOT EXISTS cards
(
    id          IDENTITY NOT NULL PRIMARY KEY,
    card_number BIGINT   NOT NULL,
    account_id  BIGINT,
    CONSTRAINT FK_ACCOUNT FOREIGN KEY (account_id) REFERENCES accounts (id),
    UNIQUE KEY card_number_UNIQUE (card_number)
);

CREATE TABLE IF NOT EXISTS contractors
(
    owner_id      BIGINT NOT NULL,
    contractor_id BIGINT NOT NULL,
    CONSTRAINT FK_OWNER_CONTRACTORS FOREIGN KEY (owner_id) REFERENCES owners (id),
    CONSTRAINT FK_CONTRACTORS FOREIGN KEY (contractor_id) REFERENCES owners (id)
);