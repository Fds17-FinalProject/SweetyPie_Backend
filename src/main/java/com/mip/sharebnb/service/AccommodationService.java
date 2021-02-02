package com.mip.sharebnb.service;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.repository.AccommodationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

@Service
@RequiredArgsConstructor
public class AccommodationService {

    private final AccommodationRepository accommodationRepository;

    public Page<Accommodation> findByCityContaining(String searchKeyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return accommodationRepository.findByCityContaining(searchKeyword, pageable);
    }

    public Page<Accommodation> findByCityContainingOrGuContaining(String searchKeyword, int page) {
        Pageable pageable = PageRequest.of(page, 10);

        return accommodationRepository.findByCityContainingOrGuContaining(searchKeyword, searchKeyword, pageable);
    }

    // 데이터 많아 질수록 성능 안 좋아짐. 임시용
    public List<Accommodation> findAccommodationsByBookedDatesNotContaining(String searchKeyword,
                                                                            LocalDate checkIn, LocalDate checkout,
                                                                            int page) {
        StringTokenizer st = new StringTokenizer(searchKeyword);

        if (st.countTokens() > 1) {
            while (st.hasMoreTokens()) {
                searchKeyword = st.nextToken();
            }
        }

        List<LocalDate> localDates = new ArrayList<>();
        List<Accommodation> results = new ArrayList<>();

        for (LocalDate date = checkIn; date.isBefore(checkout); date = date.plusDays(1)) {
            localDates.add(date);
        }

        int count = 0;
        int currentPage = 0;

        while (count < (page + 1) * 10) {
            Page<Accommodation> accommodations = findByCityContainingOrGuContaining(searchKeyword, currentPage++);


            System.out.println("accommodations.getSize() : " + accommodations.getSize());

            if (accommodations.getSize() == 0) {
                return results;
            }

            for (Accommodation accommodation : accommodations) {
                boolean flag = false;
                List<BookedDate> bookedDates = accommodation.getBookedDates();

                for (BookedDate bookedDate : bookedDates) {
                    if (localDates.contains(bookedDate.getDate())) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    count++;

                    if (count >= page * 10) {
                        results.add(accommodation);
                        if (count == (page + 1) * 10) {
                            return results;
                        }
                    }
                }
            }
        }

        return results;
    }
}
