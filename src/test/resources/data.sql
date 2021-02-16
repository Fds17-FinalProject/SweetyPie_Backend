
insert into member(id, email, password, name, contact, birth_date, is_deleted, role ) values(1, 'test123@gmail.com', '$2a$10$l4Vp0DfrB7CkSqasVyVSOufyP9FCqWReCtYd7xJ1ZYXrYAjXuUXji', 'tester', '01022223333', '19990911', false, 'MEMBER');

INSERT INTO accommodation (id, city, gu, title, accommodation_desc, accommodation_type, address, bathroom_num, bed_num,
                           bedroom_num, building_type, capacity, contact, host_desc, host_name, host_review_num,
                           latitude, location_desc, longitude, price, rating, review_num, transportation_desc, rand_id)
values (1, '서울특별시', '마포구', '깨끗한 아파트', '깔끔해요', '전체', 'Mapo-gu, Seoul', 2, 2, 2, '아파트', 4, '010-1234-1234', '좋아요',
        '이재복', 0, 37, '마포역 5분거리', 126, 40000, 0, 0, '마포역, 대흥역, 공덕역', '12345');

INSERT INTO reservation (id, `check_in_date`, `checkout_date`, `guest_num`, `is_canceled`, `is_written_review`,
                         `payment_date`, `total_price`, `accommodation_id`, `member_id`)
VALUES (1, '2022-02-10', '2022-02-12', '3', 0, 1, '2022-03-01', 60000, 1, 1);

INSERT INTO reservation (id, `check_in_date`, `checkout_date`, `guest_num`, `is_canceled`, `is_written_review`,
                         `payment_date`, `total_price`, `accommodation_id`, `member_id`)
VALUES (2, '2022-02-20', '2022-02-22', '3', 0, 0, '2022-03-03', 120000, 1, 1);

INSERT INTO review(id, `rating`, `content`, `accommodation_id`, `member_id`, `reservation_id`)
values (1, 4, 'new content', 1, 1, 1);


insert into booked_date(id, date, accommodation_id, reservation_id) values(1, '20220210', 1, 1);
insert into booked_date(id, date, accommodation_id, reservation_id) values(2, '20220211', 1, 1);
insert into booked_date(id, date, accommodation_id, reservation_id) values(3, '20220220', 1, 2);
insert into booked_date(id, date, accommodation_id, reservation_id) values(4, '20220221', 1, 2);

insert into accommodation_picture(id, url, accommodation_id) values(1, 'picture', 1);

INSERT INTO bookmark(id, accommodation_id, member_id)
values (1, 1, 1);
