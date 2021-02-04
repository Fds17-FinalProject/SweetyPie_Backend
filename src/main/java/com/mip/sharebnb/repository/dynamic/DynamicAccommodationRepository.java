package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.QAccommodation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation ac = new QAccommodation("ac");

    public Page<Accommodation> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout, int guestNum, Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(ac.capacity.goe(guestNum));

        if (checkout != null && checkout.isBefore(LocalDate.now())) {
            checkout = null;
        }

        if (checkIn != null && checkIn.isBefore(LocalDate.now())) {
            checkIn = LocalDate.now();
        }

        if (checkIn == null) {
            checkIn = LocalDate.now();
        }

        if (checkout != null) {
            while (checkIn.isBefore(checkout)) {
                builder.andNot(ac.bookedDates.contains(new BookedDate(checkIn)));
                checkIn = checkIn.plusDays(1);

                if (ChronoUnit.DAYS.between(checkIn, checkout) > 730) {
                    break;
                }
            }
        } else {
            builder.andNot(ac.bookedDates.contains(new BookedDate(checkIn)));
        }

        if (searchKeyword != null && !searchKeyword.equals("")) {
            for (String keyword : searchKeyword.split(" ")) {
                builder.and(ac.city.contains(keyword).or(ac.gu.contains(keyword)));
            }
        }

        return new PageImpl<>(queryFactory
                .select(ac)
                .from(ac)
                .where(builder)
                .offset(page.getOffset() - page.getPageSize())
                .limit(page.getPageSize())
                .fetch(), page, page.getPageSize());
    }

    public Page<Accommodation> findAccommodationsByMapSearch(Float minLatitude, Float maxLatitude,
                                                             Float minLongitude, Float maxLongitude, @PageableDefault(page = 1) Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        if (minLatitude == null || maxLatitude == null || minLongitude == null || maxLongitude == null) {
            return null;
        }

        builder.and(ac.latitude.gt(minLatitude));
        builder.and(ac.latitude.lt(maxLatitude));

        builder.and(ac.longitude.gt(minLongitude));
        builder.and(ac.longitude.lt(maxLongitude));

        return new PageImpl<>(queryFactory
                .select(ac)
                .from(ac)
                .where(builder)
                .offset(page.getOffset() - page.getPageSize())
                .limit(page.getPageSize())
                .fetch(), page, page.getPageSize());
    }
}