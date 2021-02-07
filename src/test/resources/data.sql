insert into member(member_id, email, password, name, contact, birth_date)
values (1, 'ddd@gmail.com', '1234', '이재복', '010-1234-1234', '1993-05-01');

insert into accommodation(accommodation_id, city, gu)
values (1, '서울특별시', '마포구');

insert into bookmark(id, accommodation_id, member_id)
values (1, 1, 1)