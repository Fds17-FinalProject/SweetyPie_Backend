package com.sweetypie.sweetypie.repository.dynamic;

import com.sweetypie.sweetypie.model.BookedDate;
import com.sweetypie.sweetypie.model.QBookedDate;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicReservationRepository {

    private final JPAQueryFactory queryFactory;

    QBookedDate qb = new QBookedDate("qb");

    public List<BookedDate> findByAccommodationIdAndDate(Long accommodationId, LocalDate checkIn, LocalDate checkout) {

        BooleanBuilder builder = new BooleanBuilder();

        if (accommodationId != null){
            builder.and(qb.accommodation.id.eq(accommodationId));
        }

        if (checkIn != null && checkout != null) {
            builder.and(qb.date.between(checkIn, checkout.minusDays(1)));
        }

        return queryFactory.selectFrom(qb)
        .where(builder)
        .fetch();
    }

}
