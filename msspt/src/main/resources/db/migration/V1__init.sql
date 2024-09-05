create table brand
(
    brand_id   varchar(50)  not null primary key,
    name       varchar(255) not null,
    created_at datetime(6)  null,
    updated_at datetime(6)  null
);

create table category
(
    category_id varchar(10)  not null primary key,
    name        varchar(255) not null,
    created_at  datetime(6)  null
);

create table product
(
    id                    bigint auto_increment primary key,
    goods_no              bigint       not null unique,
    name                  varchar(255) not null,
    normal_price          int          not null,
    latest_price          int          not null,
    before_latest_price   int          not null,
    weekly_lowest_price   int          not null,
    weekly_lowest_date    date         not null,
    weekly_highest_price  int          not null,
    weekly_highest_date   date         not null,
    monthly_lowest_price  int          not null,
    monthly_lowest_date   date         not null,
    monthly_highest_price int          not null,
    monthly_highest_date  date         not null,
    image_url             varchar(255) not null,
    brand_id              varchar(50)  not null,
    category_id           varchar(10)  not null,
    created_at            datetime(6)  null,
    updated_at            datetime(6)  null
);

create table price_history
(
    id         bigint auto_increment primary key,
    price      int         not null,
    product_id bigint      not null,
    created_at datetime(6) null
);
