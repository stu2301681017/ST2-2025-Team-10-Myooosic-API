
    create table suggestion_entity_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into suggestion_entity_seq values ( 1 );

    alter table suggestion_entity 
       add column id integer not null;
