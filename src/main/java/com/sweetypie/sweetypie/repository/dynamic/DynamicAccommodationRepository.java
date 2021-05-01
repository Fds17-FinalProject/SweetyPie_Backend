package com.sweetypie.sweetypie.repository.dynamic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sweetypie.sweetypie.dto.AccommodationDto;
import com.sweetypie.sweetypie.dto.QAccommodationDto;
import com.sweetypie.sweetypie.dto.QSearchAccommodationDto;
import com.sweetypie.sweetypie.dto.SearchAccommodationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.sweetypie.sweetypie.model.QAccommodation.accommodation;
import static com.sweetypie.sweetypie.model.QBookedDate.bookedDate;
import static com.sweetypie.sweetypie.model.QBookmark.bookmark;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    public AccommodationDto findById(Long memberId, Long accommodationId) {

        return getQueryResult(memberId, accommodationId);
    }

    public Page<SearchAccommodationDto> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout,
                                                                   int guestNum, Long memberId, Integer minPrice, Integer maxPrice, String types, Pageable page) {
        BooleanBuilder bdBuilder = new BooleanBuilder();
        BooleanBuilder acBuilder = new BooleanBuilder();

        acBuilder.and(accommodation.capacity.goe(guestNum));

        setPriceQuery(minPrice, maxPrice, acBuilder);
        setAccommodationTypesQuery(types, acBuilder);
        setSearchKeywordQuery(searchKeyword, acBuilder);
        setCheckInCheckOutQuery(checkIn, checkout, bdBuilder);

        acBuilder.andNot(accommodation.id.in(JPAExpressions
                .select(bookedDate.accommodation.id)
                .from(bookedDate)
                .where(bdBuilder)));

        QueryResults<SearchAccommodationDto> results = getQueryResults(acBuilder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<SearchAccommodationDto> findAccommodationsByMapSearch(float minLatitude, float maxLatitude,
                                                                      float minLongitude, float maxLongitude,
                                                                      LocalDate checkIn, LocalDate checkout,
                                                                      Integer minPrice, Integer maxPrice,
                                                                      int guestNum, Long memberId, String types, Pageable page) {
        BooleanBuilder bdBuilder = new BooleanBuilder();
        BooleanBuilder acBuilder = new BooleanBuilder();

        acBuilder.and(accommodation.capacity.goe(guestNum));

        setPriceQuery(minPrice, maxPrice, acBuilder);
        setAccommodationTypesQuery(types, acBuilder);
        setCheckInCheckOutQuery(checkIn, checkout, bdBuilder);
        setCoordinate(minLatitude, maxLatitude, minLongitude, maxLongitude, acBuilder);

        acBuilder.andNot(accommodation.id.in(JPAExpressions
                .select(bookedDate.accommodation.id)
                .from(bookedDate)
                .where(bdBuilder)));

        QueryResults<SearchAccommodationDto> results = getQueryResults(acBuilder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<SearchAccommodationDto> findByBuildingType(String buildingType, Long memberId, Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(accommodation.buildingType.eq(buildingType));

        QueryResults<SearchAccommodationDto> results = getQueryResults(builder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<SearchAccommodationDto> findByCity(String city, Long memberId, Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        city = city.replace("특별시", "")
                .replace("광역시", "");

        if (city.charAt(city.length() - 1) == '시') {
            city = city.substring(0, city.length() - 1);
        }

        builder.and(accommodation.city.startsWith(city));

        QueryResults<SearchAccommodationDto> results = getQueryResults(builder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public List<Integer> findPricesBySearch(String searchKeyword, Float minLatitude, Float maxLatitude, Float minLongitude, Float maxLongitude,
                                            LocalDate checkIn, LocalDate checkout, int guestNum, String types) {

        BooleanBuilder acBuilder = new BooleanBuilder();
        BooleanBuilder bdBuilder = new BooleanBuilder();

        setSearchKeywordQuery(searchKeyword, acBuilder);
        setAccommodationTypesQuery(types, acBuilder);
        setCheckInCheckOutQuery(checkIn, checkout, bdBuilder);
        setCoordinate(minLatitude, maxLatitude, minLongitude, maxLongitude, acBuilder);

        acBuilder.and(accommodation.capacity.goe(guestNum));

        acBuilder.andNot(accommodation.id.in(JPAExpressions
                .select(bookedDate.accommodation.id)
                .from(bookedDate
                )
                .where(bdBuilder)));

        return queryFactory
                .select(accommodation.price)
                .from(accommodation)
                .where(acBuilder)
                .orderBy(accommodation.price.asc())
                .fetch();
    }

    private QueryResults<Long> getIds(BooleanBuilder builder, Pageable page) {
        return queryFactory
                .select(accommodation.id)
                .from(accommodation)
                .where(builder)
                .orderBy(accommodation.randId.asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetchResults();
    }

    private QueryResults<SearchAccommodationDto> getQueryResults(BooleanBuilder builder, Long memberId, Pageable page) {
        if (memberId != null) {
            return queryFactory
                    .select(new QSearchAccommodationDto(accommodation.id, accommodation.city, accommodation.gu, accommodation.address, accommodation.title, accommodation.bathroomNum, accommodation.bedroomNum,
                            accommodation.bedNum, accommodation.price, accommodation.capacity, accommodation.contact, accommodation.latitude, accommodation.longitude, accommodation.rating, accommodation.reviewNum,
                            accommodation.accommodationType, accommodation.buildingType, accommodation.hostName, bookmark))
                    .from(accommodation)
                    .where(builder)
                    .leftJoin(bookmark)
                    .on(bookmark.accommodation.id.eq(accommodation.id).and(bookmark.member.id.eq(memberId)))
                    .orderBy(accommodation.randId.asc())
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetchResults();
        } else {
            return queryFactory
                    .select(new QSearchAccommodationDto(accommodation.id, accommodation.city, accommodation.gu, accommodation.address, accommodation.title, accommodation.bathroomNum, accommodation.bedroomNum,
                            accommodation.bedNum, accommodation.price, accommodation.capacity, accommodation.contact, accommodation.latitude, accommodation.longitude, accommodation.rating, accommodation.reviewNum,
                            accommodation.accommodationType, accommodation.buildingType, accommodation.hostName))
                    .from(accommodation)
                    .where(builder)
                    .orderBy(accommodation.randId.asc())
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetchResults();
        }
    }

    private AccommodationDto getQueryResult(Long memberId, Long accommodationId) {
        AccommodationDto result;

        if (memberId != null) {
            result = queryFactory
                    .select(new QAccommodationDto(accommodation.id, accommodation.city, accommodation.gu, accommodation.address, accommodation.title, accommodation.bathroomNum, accommodation.bedroomNum,
                            accommodation.bedNum, accommodation.capacity, accommodation.price, accommodation.contact, accommodation.latitude, accommodation.longitude, accommodation.locationDesc, accommodation.transportationDesc,
                            accommodation.accommodationDesc, accommodation.rating, accommodation.reviewNum, accommodation.accommodationType, accommodation.buildingType, accommodation.hostName, accommodation.hostDesc,
                            accommodation.hostReviewNum, bookmark))
                    .from(accommodation)
                    .where(accommodation.id.eq(accommodationId))
                    .leftJoin(bookmark)
                    .on(bookmark.accommodation.id.eq(accommodation.id).and(bookmark.member.id.eq(memberId)))
                    .fetchOne();
        } else {
            result = queryFactory
                    .select(new QAccommodationDto(accommodation.id, accommodation.city, accommodation.gu, accommodation.address, accommodation.title, accommodation.bathroomNum, accommodation.bedroomNum,
                            accommodation.bedNum, accommodation.capacity, accommodation.price, accommodation.contact, accommodation.latitude, accommodation.longitude, accommodation.locationDesc, accommodation.transportationDesc,
                            accommodation.accommodationDesc, accommodation.rating, accommodation.reviewNum, accommodation.accommodationType, accommodation.buildingType, accommodation.hostName, accommodation.hostDesc,
                            accommodation.hostReviewNum))
                    .from(accommodation)
                    .where(accommodation.id.eq(accommodationId))
                    .fetchOne();
        }

        return result;
    }

    private void setSearchKeywordQuery(String searchKeyword, BooleanBuilder builder) {
        int count = 0;


        if (searchKeyword != null && !searchKeyword.equals("")) {
            String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

            searchKeyword = searchKeyword.replace("특별시", "시")
                    .replace("광역시", "시")
                    .replaceAll(match, "");

            String[] keywords = searchKeyword.split(" ");

            for (String keyword : keywords) {
                char lastChar = keyword.charAt(keyword.length() - 1);
                boolean flag = false;

                if (lastChar == '시') {
                    keyword = keyword.substring(0, keyword.length() - 1);
                    flag = true;
                } else if (lastChar == '면' || lastChar == '읍' || lastChar == '구') {
                    flag = true;
                }

                if (flag) {
                    builder.and(accommodation.city.startsWith(keyword).or(accommodation.gu.startsWith(keyword)));
                    count++;
                }
            }

            if (count == 0) {
                builder.and(accommodation.city.startsWith(keywords[keywords.length - 1]).or(accommodation.gu.startsWith(keywords[keywords.length - 1])));
            }
        }

    }

    private void setCheckInCheckOutQuery(LocalDate checkIn, LocalDate checkout, BooleanBuilder builder) {
        builder.and(bookedDate.date.goe(checkIn));

        if (checkout != null) {
            builder.and(bookedDate.date.before(checkout));
        }
    }

    private void setAccommodationTypesQuery(String types, BooleanBuilder builder) {
        if (types != null) {
            BooleanBuilder typeBuilder = new BooleanBuilder();

            for (String type : types.split(" ")) {
                typeBuilder.or(accommodation.accommodationType.eq(type));
            }
            builder.and(typeBuilder);
        }
    }


    private void setCoordinate(Float minLatitude, Float maxLatitude, Float minLongitude, Float maxLongitude, BooleanBuilder builder) {

        if (minLatitude != null && maxLatitude != null && minLongitude != null && maxLongitude != null) {
            builder.and(accommodation.latitude.between(minLatitude, maxLatitude));
            builder.and(accommodation.longitude.between(minLongitude, maxLongitude));
        }
    }

    private void setPriceQuery(Integer minPrice, Integer maxPrice, BooleanBuilder builder) {

        if (minPrice != null && maxPrice != null) {
            if (maxPrice == 250000) {
                builder.and(accommodation.price.goe(minPrice));
            } else {
                builder.and(accommodation.price.between(minPrice, maxPrice));
            }
        }
    }
}