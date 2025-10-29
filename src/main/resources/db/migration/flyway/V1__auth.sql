
    create table registration (
        name varchar(32) not null,
        password_salt_base64 varchar(24) not null,
        password_sha256base64 varchar(44) not null,
        primary key (name)
    ) engine=InnoDB;

    create table user (
        name varchar(32) not null,
        primary key (name)
    ) engine=InnoDB;

    alter table user 
       add constraint FKi68puenljwbavk3kb63o7a6ct 
       foreign key (name) 
       references registration (name);
