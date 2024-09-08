ALTER TABLE product
    ADD daily_price_drop_rate DOUBLE AS
        (IF(before_latest_price = 0, 100,
            ((latest_price - before_latest_price) / before_latest_price) * 100)) VIRTUAL,
    ADD INDEX idx_product_daily_drop_rate (daily_price_drop_rate);

ALTER TABLE product
    ADD weekly_price_drop_rate DOUBLE AS
        (IF(before_latest_price = 0, 100,
            ((latest_price - weekly_highest_price) / weekly_highest_price) * 100)) VIRTUAL,
    ADD INDEX idx_product_weekly_drop_rate (weekly_price_drop_rate);

ALTER TABLE product
    ADD monthly_price_drop_rate DOUBLE AS
        (IF(before_latest_price = 0, 100,
            ((latest_price - monthly_highest_price) / monthly_highest_price) * 100)) VIRTUAL,
    ADD INDEX idx_product_monthly_drop_rate (monthly_price_drop_rate);
