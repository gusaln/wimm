CREATE TABLE exchangeRate (
    exchangeRateId INTEGER PRIMARY KEY,
    baseCurrency TEXT NOT NULL,
--     Also known as quote currency
    counterCurrency TEXT NOT NULL,
    effectiveSince INTEGER NOT NULL,
    rate REAL NOT NULL DEFAULT 0,

    UNIQUE (baseCurrency, counterCurrency, effectiveSince)
);