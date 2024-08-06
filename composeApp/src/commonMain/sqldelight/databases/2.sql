ALTER TABLE moneyTransaction ADD COLUMN details TEXT DEFAULT NULL;

CREATE TABLE IF NOT EXISTS migration (
    migrationId INTEGER PRIMARY KEY,
    datetime INTEGER NOT NULL
);

INSERT INTO migration (migrationId, datetime) VALUES (2, strftime('%s', 'now'));

DELETE FROM migration WHERE migrationId < 2;