create table brand
(
    brand_id     varchar(50)  not null primary key,
    name         varchar(255) not null,
    english_name varchar(255) not null,
    created_at   datetime(6)  null,
    updated_at   datetime(6)  null
);

create table category
(
    category_id varchar(10)  not null primary key,
    name        varchar(255) not null,
    created_at  datetime(6)  null
);

create table product
(
    id           bigint auto_increment primary key,
    goods_no     bigint       not null unique,
    name         varchar(255) not null,
    normal_price int          not null,
    image_url    varchar(255) not null,
    brand_id     varchar(50)  not null,
    category_id  varchar(10)  not null,
    created_at   datetime(6)  null,
    updated_at   datetime(6)  null
);

create table price_history
(
    id         bigint auto_increment primary key,
    price      int         not null,
    product_id bigint      not null,
    created_at datetime(6) null
);
