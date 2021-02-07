package com.mip.sharebnb.service;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.ReservationDto;
import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.BookedDate;
import com.mip.sharebnb.model.Member;
import com.mip.sharebnb.model.Reservation;
import com.mip.sharebnb.repository.AccommodationRepository;
import com.mip.sharebnb.repository.MemberRepository;
import com.mip.sharebnb.repository.BookedDateRepository;
import com.mip.sharebnb.repository.ReservationRepository;

import com.mip.sharebnb.repository.dynamic.DynamicReservationRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.spec.PSource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final DynamicReservationRepository dynamicReservationRepository;
    private final ReservationRepository reservationRepository;
    private final MemberRepository memberRepository;
    private final AccommodationRepository accommodationRepository;
    private final BookedDateRepository bookedDateRepository;


    public List<ReservationDto> getReservations(Long memberId) {
        if (memberId == null) {
            return new ArrayList<>();
        }
        List<Reservation> reservations = reservationRepository.findReservationByMemberId(memberId);

        List<ReservationDto> reservationDtoList = new ArrayList<>();

        for (Reservation reservation : reservations) {

            ReservationDto reservationDto = new ReservationDto();

            reservationDto.setReservationId(reservation.getId());
            reservationDto.setCheckInDate(reservation.getCheckInDate());
            reservationDto.setCheckoutDate(reservation.getCheckoutDate());
            reservationDto.setAccommodationDto(mappingAccommodationDto(reservation));

            reservationDtoList.add(reservationDto);
        }
        return reservationDtoList;
    }

    @Transactional
    public Reservation insertReservation(ReservationDto reservationDto) {
        System.out.println("memeberId " + reservationDto.getMemberId() + " accommodationId" + reservationDto.getAccommodationId());
        Optional<Member> optionalMember = Optional.of(memberRepository.findById(reservationDto.getMemberId()).orElseThrow(RuntimeException::new));
        Member member = optionalMember.get();

        Optional<Accommodation> optionalAccommodation = Optional.of(accommodationRepository.findById(reservationDto.getAccommodationId()).orElseThrow(RuntimeException::new));
        Accommodation accommodation = optionalAccommodation.get();

        List<BookedDate> bookedDates = new ArrayList<>();

        List<BookedDate> checkDuplicateDate = dynamicReservationRepository.findByAccommodationIdAndDate(reservationDto.getAccommodationId(), reservationDto.getCheckInDate(), reservationDto.getCheckoutDate());
        System.out.println(" >>> checkduplicatddate " + checkDuplicateDate.isEmpty());

        if (checkDuplicateDate.isEmpty()) {
            for (LocalDate date = reservationDto.getCheckInDate(); date.isBefore(reservationDto.getCheckoutDate()); date = date.plusDays(1)) {
                bookedDates.add(saveBookedDate(date, accommodation));
            }

            Reservation buildReservation = Reservation.builder()
                    .checkInDate(reservationDto.getCheckInDate())
                    .checkoutDate(reservationDto.getCheckoutDate())
                    .guestNum(reservationDto.getGuestNum())
                    .totalPrice(reservationDto.getTotalPrice())
                    .isCanceled(false)
                    .paymentDate(LocalDate.now())
                    .member(member)
                    .accommodation(accommodation)
                    .bookedDates(bookedDates)
                    .reservationCode(setReservationCode(accommodation.getId(), member.getId()))
                    .build();

            System.out.println(setReservationCode(accommodation.getId(), member.getId()));
            return reservationRepository.save(buildReservation);
        }

        return new Reservation();
    }

    @Transactional
    public Reservation updateReservation(Long reservationId, ReservationDto reservationDto) {
        
        Optional<Reservation> findReservation = Optional.of(reservationRepository.findById(reservationId).orElseThrow(RuntimeException::new));
        Reservation reservation = findReservation.get();
        
        List<BookedDate> reservationBookedDates = reservation.getBookedDates();
        List<LocalDate> dates = new ArrayList<>();

        for (BookedDate reservationBookedDate : reservationBookedDates) {
            dates.add(reservationBookedDate.getDate());
        }
        
        // 이미 Reservation에 bookedDate를 가지고 있으니 삭제를 넣어주면 됨
        Long accommodationId = reservation.getAccommodation().getId();
        LocalDate checkInDate = reservation.getCheckInDate();
        LocalDate checkoutDate = reservation.getCheckoutDate();

        bookedDateRepository.deleteBookedDateByAccommodationIdAndDateIn(accommodationId, dates);

        List<BookedDate> bookedDates = dynamicReservationRepository.findByAccommodationIdAndDate(accommodationId, checkInDate, checkoutDate);
        
        // 예약하려고 날짜가 기존에 저장되어있던 날짜가 아닐때 예약을 할 수 있게  리스트가 비어있을 때 저장 
        if (bookedDates.isEmpty()) {
            reservation.setCheckInDate(reservationDto.getCheckInDate());
            reservation.setCheckoutDate(reservationDto.getCheckoutDate());
            reservation.setGuestNum(reservationDto.getGuestNum());
            reservation.setTotalPrice(reservationDto.getTotalPrice());

            return reservationRepository.save(reservation);

        } else {

            // 중복된 예약날짜면 다시 삭제했던 원래 날짜들을 넣어줘야 함.
            for (BookedDate findBookedDate : reservationBookedDates) {
                bookedDateRepository.save(findBookedDate);
            }

            return new Reservation();
        }
    }

    public void deleteReservation(Long reservationId){

    }

    private AccommodationDto mappingAccommodationDto(Reservation reservation) {

        AccommodationDto accommodationDto = new AccommodationDto();

        accommodationDto.setCity(reservation.getAccommodation().getCity());
        accommodationDto.setGu(reservation.getAccommodation().getGu());
        accommodationDto.setAccommodationPictures(reservation.getAccommodation().getAccommodationPictures());

        return accommodationDto;
    }

    private BookedDate saveBookedDate(LocalDate date, Accommodation accommodation) {

        BookedDate bookedDate = BookedDate.builder()
                .date(date)
                .accommodation(accommodation)
                .build();
        return bookedDateRepository.save(bookedDate);

    }

    private String setReservationCode(Long accommodationId, Long memberId){

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String strAccommodationId = String.format("%05d", accommodationId);
        String strMemberId = String.format("%05d", memberId);

        return today + strAccommodationId + strMemberId;

    }
}
