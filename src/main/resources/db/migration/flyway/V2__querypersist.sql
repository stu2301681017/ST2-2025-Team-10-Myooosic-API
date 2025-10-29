
    create table query_prompt_entity_seq (
        next_val bigint
    ) engine=InnoDB;

    insert into query_prompt_entity_seq values ( 1 );

    create table query_prompt_entity (
        id integer not null,
        prompt varchar(256),
        owner_name varchar(32),
        primary key (id)
    ) engine=InnoDB;

    create table suggestion_entity (
        name varchar(255) not null,
        author varchar(255) not null,
        reason varchar(128),
        prompt_id integer,
    ) engine=InnoDB;

    alter table query_prompt_entity 
       add constraint FKjd6pujjl5rbgmdqtmhxlhj29u 
       foreign key (owner_name) 
       references registration (name);

    alter table suggestion_entity 
       add constraint FKoss4rqkliqmi3sugsl59jcr15 
       foreign key (prompt_id) 
       references query_prompt_entity (id);
