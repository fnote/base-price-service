-- CREATE SCHEMA `REF_PRICE_020`;

CREATE TABLE `PA`
(
    `SUPC`            varchar(9) NOT NULL,
    `PRICE_ZONE`      tinyint(1) NOT NULL,
    `PRICE`           double(13) NOT NULL,
    `EFFECTIVE_DATE`  datetime   NOT NULL,
    `EXPORTED_DATE`   bigint(10) NOT NULL,
    `SPLIT_INDICATOR` char(1)    NOT NULL,
    PRIMARY KEY (`SUPC`, `PRICE_ZONE`, `EFFECTIVE_DATE`, `EXPORTED_DATE`)
--                       KEY `pa_ix_supc_pz_eff_date` (`SUPC`,`PRICE_ZONE`,`EFFECTIVE_DATE`)
);

CREATE TABLE `PRICE_ZONE_01`
(
    `SUPC`           varchar(9)  NOT NULL,
    `PRICE_ZONE`     tinyint(1)  NOT NULL,
    `CUSTOMER_ID`    varchar(14) NOT NULL,
    `EFFECTIVE_DATE` datetime    NOT NULL,
    PRIMARY KEY (`CUSTOMER_ID`, `SUPC`)
);
