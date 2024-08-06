PRAGMA foreign_keys = 0;

ALTER TABLE entry ADD COLUMN reference TEXT DEFAULT NULL;

PRAGMA foreign_keys = 1;

CREATE TABLE IF NOT EXISTS migration (
    migrationId INTEGER PRIMARY KEY,
    datetime INTEGER NOT NULL
);

INSERT INTO migration (migrationId, datetime) VALUES (5, strftime('%s', 'now'));

DELETE FROM migration WHERE migrationId < 5;