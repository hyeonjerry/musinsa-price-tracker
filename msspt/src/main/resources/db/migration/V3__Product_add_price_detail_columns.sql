alter table product
    add column latest_price          int  not null,
    add column before_latest_price   int  null,
    add column weekly_lowest_price   int  null,
    add column weekly_lowest_date    date null,
    add column weekly_highest_price  int  null,
    add column weekly_highest_date   date null,
    add column monthly_lowest_price  int  null,
    add column monthly_lowest_date   date null,
    add column monthly_highest_price int  null,
    add column monthly_highest_date  date null;
