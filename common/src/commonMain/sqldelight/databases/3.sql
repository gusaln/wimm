PRAGMA foreign_keys = 0;

ALTER TABLE moneyTransaction RENAME TO moneyTransactionLegacy;

ALTER TABLE entry RENAME TO entryLegacy;

CREATE TABLE moneyTransaction (
    transactionId INTEGER PRIMARY KEY,
    categoryId INTEGER NOT NULL,
    number INTEGER NOT NULL UNIQUE,
    incurredAt INTEGER NOT NULL,
    description TEXT NOT NULL,
    details TEXT,
    total REAL NOT NULL DEFAULT 0,

        FOREIGN KEY (categoryId)
            REFERENCES category (categoryId)
                ON DELETE CASCADE
                ON UPDATE NO ACTION
);

CREATE TABLE entry (
    entryId INTEGER PRIMARY KEY,
    transactionId INTEGER NOT NULL,
    accountId INTEGER NOT NULL,
    amount REAL NOT NULL DEFAULT 0,
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

INSERT INTO moneyTransaction
SELECT MAX(m.transactionId), MAX(m.categoryId), MAX(m.number), MIN(entryLegacy.incurredAt), MAX(m.description), MAX(m.details), MAX(m.total) FROM moneyTransactionLegacy AS m
JOIN entryLegacy ON entryLegacy.transactionId = m.transactionId
GROUP BY m.transactionId;

INSERT INTO entry
SELECT e.entryId, e.transactionId, e.accountId, e.amount, e.recordedAt FROM entryLegacy AS e;

DROP TABLE entryLegacy;

DROP TABLE moneyTransactionLegacy;

PRAGMA foreign_keys = 1;