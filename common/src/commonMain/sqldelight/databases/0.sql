CREATE TABLE account (
    accountId INTEGER PRIMARY KEY,
    type TEXT NOT NULL,
    name TEXT NOT NULL,
    currency TEXT NOT NULL,
    balance REAL NOT NULL DEFAULT 0
);

CREATE TABLE moneyTransaction (
    transactionId INTEGER PRIMARY KEY,
    number INTEGER NOT NULL UNIQUE,
    description TEXT NOT NULL
);

CREATE TABLE entry (
    entryId INTEGER PRIMARY KEY,
    transactionId INTEGER NOT NULL,
    accountId INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0,
    incurredAt INTEGER NOT NULL,
    recordedAt INTEGER NOT NULL,

    FOREIGN KEY (transactionId)
        REFERENCES moneyTransaction (transactionId)
            ON DELETE CASCADE
            ON UPDATE NO ACTION,
    FOREIGN KEY (accountId)
        REFERENCES account (accountId)
            ON DELETE CASCADE
            ON UPDATE NO ACTION
);