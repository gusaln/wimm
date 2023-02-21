ALTER TABLE moneyTransaction ADD COLUMN total REAL NOT NULL DEFAULT 0;

UPDATE moneyTransaction
SET total = (
    SELECT SUM(amount) total
    FROM entry
    WHERE entry.transactionId == moneyTransaction.transactionId
);