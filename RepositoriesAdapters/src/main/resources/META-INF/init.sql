create table if not exists rooment
(
    room_number    integer          not null
        primary key,
    capacity       integer          not null,
    equipment_type varchar(255)     not null,
    price          double precision not null,
    version        bigint
);

alter table if exists rooment
    owner to nbd;

create table if not exists userent
(
    username     varchar(255) not null
        primary key,
    city         varchar(25),
    first_name   varchar(35)  not null,
    is_active    boolean      not null,
    last_name    varchar(255) not null,
    password     varchar(255) not null,
    postal_code  varchar(6)
        constraint userent_postal_code_check
            check ((postal_code)::text ~ '^\d{2}-\d{3}$'::text),
    role         varchar(255) not null,
    street       varchar(96),
    streetnumber varchar(4)
);

alter table if exists userent
    owner to nbd;

create table if not exists reservationent
(
    reservation_id     uuid             not null
        primary key,
    begin_time         date             not null,
    end_time           date             not null,
    is_active          boolean          not null,
    reservation_cost   double precision not null,
    room_room_number   integer          not null
        constraint fkm283f25sipq5wh7u8b7uaumix
            references rooment,
    client_personal_id varchar(255)     not null
        constraint fkmnnda0148vtl9x4fhtm7ykuwp
            references userent
);

alter table if exists reservationent
    owner to nbd;


INSERT INTO public.userent (username, city, first_name, is_active, last_name, password, postal_code, role, street,
                            streetnumber)
VALUES ('miszczu', 'Brzeziny', 'Bartosz', true, 'Miszczak', '123456', '95-060', 'USER', 'Piotrkowska', '2');
INSERT INTO public.userent (username, city, first_name, is_active, last_name, password, postal_code, role, street,
                            streetnumber)
VALUES ('miszczumod', null, '', true, '', '123456', '95-060', 'MODERATOR', '', '');
INSERT INTO public.userent (username, city, first_name, is_active, last_name, password, postal_code, role, street,
                            streetnumber)
VALUES ('miszczuadmin', null, '', true, '', '123456', '95-060', 'ADMIN', '', '');

INSERT INTO public.rooment (room_number, capacity, equipment_type, price, version)
VALUES (1, 51, 'EXTENDED', 100.5, 0);
INSERT INTO public.rooment (room_number, capacity, equipment_type, price, version)
VALUES (2, 51, 'DELUXE', 10000.5, 0);
INSERT INTO public.rooment (room_number, capacity, equipment_type, price, version)
VALUES (3, 1, 'BASIC', 55, 0);



