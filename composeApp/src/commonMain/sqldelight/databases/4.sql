PRAGMA foreign_keys = 0;

ALTER TABLE moneyTransaction RENAME TO moneyTransactionLegacy;

CREATE TABLE moneyTransaction (
    transactionId INTEGER PRIMARY KEY,
    categoryId INTEGER NOT NULL,
    number INTEGER NOT NULL UNIQUE,
    incurredAt INTEGER NOT NULL,
    description TEXT NOT NULL,
    details TEXT,
    currency TEXT NOT NULL,
    total REAL NOT NULL DEFAULT 0,

        FOREIGN KEY (categoryId)
            REFERENCES category (categoryId)
                ON DELETE CASCADE
                ON UPDATE NO ACTION
);

INSERT INTO moneyTransaction
SELECT m.transactionId, m.categoryId, m.number, m.incurredAt, m.description, m.details, account.currency, m.total FROM moneyTransactionLegacy AS m
LEFT JOIN entry ON entry.transactionId = m.transactionId
LEFT JOIN account ON account.accountId = entry.accountId
GROUP BY m.transactionId;

DROP TABLE moneyTransactionLegacy;

PRAGMA foreign_keys = 1;

CREATE TABLE IF NOT EXISTS migration (
    migrationId INTEGER PRIMARY KEY,
    datetime INTEGER NOT NULL
);

INSERT INTO migration (migrationId, datetime) VALUES (4, strftime('%s', 'now'));

DELETE FROM migration WHERE migrationId < 4;