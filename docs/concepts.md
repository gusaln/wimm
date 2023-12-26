# Design

### Accounts

An _accounts_ represents a container for money.
A bank account, a wallet, a group of accounts, etc.
Every account has a name, a currency and a balance.

### Transaction and entries

These are the lifeblood of the system.

A transaction is collection of movements of money that happen for a reason (see _category_).
Entries are all movements involve in that process.

Buying a phone is a _transaction_.
100 $ cash + 100 $ through your card are _how you paid for it_.

The transactions have a date that indicates when you want it to be recorded as happening, and every entry has a date
that indicates when it _happened_ according to your bank statement.
A transaction may happen during the weekend, the invoice could show that.
However, your bank statement may show it as happening on the next business day.

### Category

A category represents a group of reasons for a transaction to happen.
_WHY_ did you spend that money?

Categories can be assigned a _parent category_ as long as the last one does not have a parent.