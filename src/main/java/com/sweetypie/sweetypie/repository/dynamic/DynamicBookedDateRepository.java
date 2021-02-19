package com.sweetypie.sweetypie.repository.dynamic;

import com.sweetypie.sweetypie.dto.BookedDateDto;
import com.sweetypie.sweetypie.dto.QBookedDateDto;
import com.sweetypie.sweetypie.model.QAccommodation;
import com.sweetypie.sweetypie.model.QReservation;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicBookedDateRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation acc = QAccommodation.accommodation;
    QReservation reservation = QReservation.reservation;

    public List<BookedDateDto> findByAccommodationId(long accommodationId) {

        return queryFactory
                .select(new QBookedDateDto(reservation.checkInDate, reservation.checkoutDate))
                .from(reservation)
                .where(reservation.accommodation.id.eq(accommodationId)
                        .and(reservation.checkoutDate.after(LocalDate.now())))
                .innerJoin(acc)
                .on(acc.id.eq(reservation.accommodation.id))
                .fetch();
    }
}
