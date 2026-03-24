create table items_aud
(
    deleted    bit,
    rev        integer not null,
    revtype    tinyint,
    created_at datetime(6),
    updated_at datetime(6),
    id         binary(16) not null,
    owner_id   binary(16),
    content    TEXT,
    title      varchar(255),
    primary key (rev, id)
);

create table user_aware_revision_entity_seq
(
    next_val bigint
);

insert into user_aware_revision_entity_seq values (1);

create table user_aware_revision_entity
(
    id        integer not null,
    timestamp bigint  not null,
    username  varchar(255),
    primary key (id)
);
