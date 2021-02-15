INSERT INTO accommodation (id, city, gu, title, accommodation_desc, accommodation_type, address, bathroom_num, bed_num,
                           bedroom_num, building_type, capacity, contact, host_desc, host_name, host_review_num,
                           latitude, location_desc, longitude, price, rating, review_num, transportation_desc, rand_id)
values (1, '서울특별시', '마포구', '깨끗한 아파트', '깔끔해요', '전체', 'Mapo-gu, Seoul', 2, 2, 2, '아파트', 4, '010-1234-1234', '좋아요',
        '이재복', 0, 37, '마포역 5분거리', 126, 40000, 0, 0, '마포역, 대흥역, 공덕역', '12345');

INSERT INTO member(id, email, password, name, contact, birth_date)
values (1, 'ddd@gmail.com', '1234', '이재복', '010-1234-1234', '1993-05-01');

INSERT INTO reservation (id, `check_in_date`, `checkout_date`, `guest_num`, `is_canceled`, `is_written_review`,
                         `payment_date`, `total_price`, `accommodation_id`, `member_id`)
VALUES (1, '2022-03-05', '2022-03-08', '3', 0, 0, '2022-03-01', 60000, 1, 1);

INSERT INTO reservation (id, `check_in_date`, `checkout_date`, `guest_num`, `is_canceled`, `is_written_review`,
                         `payment_date`, `total_price`, `accommodation_id`, `member_id`)
VALUES (2, '2022-03-10', '2022-03-15', '3', 0, 0, '2022-03-03', 120000, 1, 1);

INSERT INTO review(id, `rating`, `content`, `accommodation_id`, `member_id`, `reservation_id`)
values (1, 4, 'new content', 1, 1, 1);

INSERT INTO bookmark(id, accommodation_id, member_id)
values (1, 1, 1);


