drop table if exists comments;
drop table if exists books;


create table if not exists books (
                                     id identity primary key,
                                     title varchar(255) not null,
    author varchar(255) not null,
    pub_year int not null
    );

create table if not exists comments (
                                        id identity primary key,
                                        book_id bigint not null,
                                        author varchar(64) not null,
    text varchar(1000) not null,
    created_at timestamp not null default '2024-01-01 12:00:00',
    constraint fk_book foreign key (book_id) references books(id) on delete cascade
    );