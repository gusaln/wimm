ALTER TABLE moneyTransaction ADD COLUMN total REAL NOT NULL DEFAULT 0;

UPDATE moneyTransaction
SET total = (
    SELECT SUM(amount) total
    FROM entry
    WHERE entry.transactionId == moneyTransaction.transactionId
);

CREATE TABLE IF NOT EXISTS migration (
    migrationId INTEGER PRIMARY KEY,
    datetime INTEGER NOT NULL
);

INSERT INTO migration (migrationId, datetime) VALUES (1, strftime('%s', 'now'));

DELETE FROM migration WHERE migrationId < 1;