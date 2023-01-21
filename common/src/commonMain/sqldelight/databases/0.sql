CREATE TABLE account (
    id INTEGER PRIMARY KEY,
    name TEXT NOT NULL,
    balance_currency TEXT NOT NULL,
    balance_value REAL NOT NULL DEFAULT 0,
    initial_value REAL NOT NULL DEFAULT 0
);

CREATE TABLE record (
    id INTEGER PRIMARY KEY,
    description TEXT NOT NULL,
    reference TEXT NOT NULL UNIQUE
);

CREATE TABLE entry (
    id INTEGER PRIMARY KEY,
    account_id INTEGER NOT NULL,
    record_id INTEGER NOT NULL,
    description TEXT NOT NULL,
    amount_currency TEXT NOT NULL,
    amount_value REAL NOT NULL DEFAULT 0,
    incurred_at INTEGER NOT NULL,
    recorded_at INTEGER NOT NULL,

    FOREIGN KEY (account_id)
        REFERENCES account (id)
            ON DELETE CASCADE
            ON UPDATE NO ACTION,
    FOREIGN KEY (record_id)
        REFERENCES record (id)
            ON DELETE CASCADE
            ON UPDATE NO ACTION
);