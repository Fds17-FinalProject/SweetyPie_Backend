package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.QAccommodation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation ac = new QAccommodation("ac");

    public List<Accommodation> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout, int page) {
        BooleanBuilder builder = new BooleanBuilder();

        if (checkout != null && checkout.isBefore(LocalDate.now())) {
            checkout = null;
        }

        if (checkIn != null && checkIn.isBefore(LocalDate.now())) {
            checkIn = null;
        }


        if (checkIn != null) {
            if (checkout != null) {
                for (LocalDate date = checkIn; !date.isEqual(checkout); date = date.plusDays(1)) {
                    builder.andNot(ac.bookedDates.contains(new BookedDate(date)));
                }
                builder.andNot(ac.bookedDates.contains(new BookedDate(checkout)));
            } else {
                builder.andNot(ac.bookedDates.contains(new BookedDate(checkIn)));
            }

        } else {
            if (checkout != null) {
                checkIn = LocalDate.now();
                for (LocalDate date = checkIn; !date.isEqual(checkout); date = date.plusDays(1)) {
                    builder.andNot(ac.bookedDates.contains(new BookedDate(date)));
                }
                builder.andNot(ac.bookedDates.contains(new BookedDate(checkout)));
            }
        }

        if (searchKeyword != null && !searchKeyword.equals("")) {
            for (String keyword : searchKeyword.split(" ")) {
                builder.and(ac.city.contains(keyword).or(ac.gu.contains(keyword)));
            }
        }
        System.out.println("builder: " + builder.toString());
        return queryFactory
                .select(ac)
                .from(ac)
                .where(builder)
                .offset(page * 10L)
                .limit(10)
                .fetch();
    }
}