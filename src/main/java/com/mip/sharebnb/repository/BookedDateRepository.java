package com.mip.sharebnb.repository;

import com.mip.sharebnb.model.BookedDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

public interface BookedDateRepository extends JpaRepository<BookedDate, Long> {

    List<BookedDate> findBookedDatesByAccommodationId(Long accommodationId);

    @Transactional // 일단 검색해보다가 방법이 생겨서 써봤는데 됬다 동작원리에 대해서 더 알아봐야 해 그리고 이 방법이 아닌 다른 쉬운 방법은 뭐가 있을까
    @Modifying //
    @Query("delete from BookedDate as b where b.accommodation.id = :accommodationId and b.date in :bookedDates")
    void deleteBookedDateByAccommodationIdAndDateIn(@Param("accommodationId") Long accommodationId, @Param("bookedDates") List<LocalDate> bookedDates);
}
