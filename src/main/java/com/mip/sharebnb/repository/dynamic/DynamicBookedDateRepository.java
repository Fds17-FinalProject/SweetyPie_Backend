package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.dto.BookedDateDto;
import com.mip.sharebnb.dto.QBookedDateDto;
import com.mip.sharebnb.model.QAccommodation;
import com.mip.sharebnb.model.QReservation;
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
