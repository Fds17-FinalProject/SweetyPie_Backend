package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.QBookedDate;
import com.mip.sharebnb.model.QReservation;
import com.mip.sharebnb.model.Reservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicReservationRepository {

    private final JPAQueryFactory queryFactory;

    QReservation qr = new QReservation("qr");

    QBookedDate qb = new QBookedDate("qb");

    public List<BookedDate> findByReservationIdAndDate(Long reservationId, Long accommodationId, LocalDate checkIn, LocalDate checkout) {

        BooleanBuilder builder = new BooleanBuilder();

        if (reservationId != null) {
            builder.and(qb.reservation.id.eq(reservationId));
        }

        if (accommodationId != null){
            builder.and(qb.accommodation.id.eq(accommodationId));
        }

        if (checkIn != null && checkout != null) {
            builder.and(qb.date.between(checkIn, checkout));
        }

        return queryFactory.selectFrom(qb)
        .where(builder)
        .fetch();
    }

    private BooleanExpression idEq(Long reservationId){

        return reservationId != null ? qb.reservation.id.eq(reservationId) : null;

    }

    private List<BookedDate> findByBookedDate(Long reservationId){

        return queryFactory.selectFrom(qb)
                .where(qb.reservation.id.eq(reservationId))
                .fetch();
    }
    
    


}