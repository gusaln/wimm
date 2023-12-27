# WIMM - Where is my money?

An expense tracker for the highly ~neurotic~ organized and responsible adult.

This project is a **work in progress**, and my personal expense tracker.

Made with: Kotlin, Jetpack Compose, Kodein - DI, and Decompose - Navigation.

## Description

W.I.M.M. (Where is my money?) is a desktop application that helps you manage you personal finances.
Its design is inspired by accounting basic principle and the concepts outlined in the _Inventory and Accounting_ chapter
of the _Analysis Patterns_ book by Martin Fowler.

## Works on

- Linux: Yes
- Mac: Unsure, but maybe (?)
- Windows: To Do

### Linux

The program will use the following order to determine its data directory:

1. `$XDG_DATA_HOME/wimm/`
2. `$HOME/.wimm/`
3. current directory

Inside this directory it will store the database file and backups.
If no database file is found, a new one will be created.

- Launch WIMM.
- Create, modified, and delete the accounts / categories you make a setup to your liking.
- Start to keep track of your finances.

## Basic concepts

Refer to [this document](./docs/concepts.md).

## Features

### Implemented

- [x] Multi-part transactions
- [x] Account management
- [x] Categories management
- [x] Rolling backups
- [x] Summaries of transactions in a month
    - [x] by category
    - [x] by account
- [x] Overview screen

### To do

- [ ] Proper support for multiple currencies
- [ ] Profiles - being able to store data for you and your small business in the same app
- [ ] Calculator widget for operations
- [ ] Budgets
- [ ] Support for Payable and Receivables - lending money and borrowing money.
- [ ] Scheduled / repeatable transactions
- [ ] Colors for accounts and categories
    - [ ] Color picker widget
- [ ] Search function for transactions
- [ ] Activity log
- [ ] Plug-in system

## Motivation

After some time of making and effort to organize my own finances, I realize that **it is not a trivial task**.

You start simple: There is a spreadsheet to record when you get money and when you spend of money as _incomes_ and
_expenses_ respectively, as well as the reason for those changes in your accounts.
It grows in complexity as you start to add multiple accounts, transfers, payables, multiple currencies, record exchanges
between currencies and their fees, paying with multiple currencies, etc.

There is a lot of software built for this purpose, but I never found one solution that had a pleasant workflow.
I like to sit down with my account statement opened, a few invoices, and a cup of coffee to do the necessary bookkeeping
every weekend.
This is the reason a mobile application does not cut it.
Having to unlock my phone every 30 seconds to feed the information is annoying.
A phone is a good tool for visualizing information - like summaries of you expenses and you current balances - and maybe
saving quick about spending 5 $ in a coffee shop.
It is no a comfortable way to input a lot of information and validate it across multiple screens.

The other reason is that I wanted to _get good_ with a technology for making desktop applications.
There might not be a market for them anymore, but I really like to have stuff in my screen and in my own computer.
Since no one else is making them, I might as well learn to make my own.