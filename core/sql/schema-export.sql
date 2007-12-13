drop table MC_ACCOUNT;
create table MC_ACCOUNT (
   ACCOUNT_ID char(32) not null,
   NAME varchar(64) not null,
   primary key (ACCOUNT_ID)
);
