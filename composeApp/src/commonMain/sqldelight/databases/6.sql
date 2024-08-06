CREATE TABLE exchangeRate (
    exchangeRateId INTEGER PRIMARY KEY,
    baseCurrency TEXT NOT NULL,
    -- Also known as quote currency
    counterCurrency TEXT NOT NULL,
    effectiveSince INTEGER NOT NULL,
    rate REAL NOT NULL DEFAULT 0,

    UNIQUE (baseCurrency, counterCurrency, effectiveSince)
);

CREATE TABLE IF NOT EXISTS migration (
    migrationId INTEGER PRIMARY KEY,
    datetime INTEGER NOT NULL
);

INSERT INTO migration (migrationId, datetime) VALUES (6, strftime('%s', 'now'));

DELETE FROM migration WHERE migrationId < 6;