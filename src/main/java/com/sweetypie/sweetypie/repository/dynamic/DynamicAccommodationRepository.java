package com.sweetypie.sweetypie.repository.dynamic;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sweetypie.sweetypie.dto.AccommodationDto;
import com.sweetypie.sweetypie.dto.QAccommodationDto;
import com.sweetypie.sweetypie.dto.QSearchAccommodationDto;
import com.sweetypie.sweetypie.dto.SearchAccommodationDto;
import com.sweetypie.sweetypie.model.QAccommodation;
import com.sweetypie.sweetypie.model.QBookedDate;
import com.sweetypie.sweetypie.model.QBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation ac = QAccommodation.accommodation;
    QBookedDate bd = QBookedDate.bookedDate;
    QBookmark bookmark = QBookmark.bookmark;

    public AccommodationDto findById(Long memberId, Long accommodationId) {

        return getQueryResult(memberId, accommodationId);
    }

    public Page<SearchAccommodationDto> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout,
                                                                   int guestNum, Long memberId, Integer minPrice, Integer maxPrice, String types, Pageable page) {
        BooleanBuilder bdBuilder = new BooleanBuilder();
        BooleanBuilder acBuilder = new BooleanBuilder();

        acBuilder.and(ac.capacity.goe(guestNum));

        setPriceQuery(minPrice, maxPrice, acBuilder);
        setAccommodationTypesQuery(types, acBuilder);
        setSearchKeywordQuery(searchKeyword, acBuilder);
        setCheckInCheckOutQuery(checkIn, checkout, bdBuilder);

        acBuilder.andNot(ac.id.in(JPAExpressions
                .select(bd.accommodation.id)
                .from(bd)
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

        acBuilder.and(ac.capacity.goe(guestNum));

        setPriceQuery(minPrice, maxPrice, acBuilder);
        setAccommodationTypesQuery(types, acBuilder);
        setCheckInCheckOutQuery(checkIn, checkout, bdBuilder);
        setCoordinate(minLatitude, maxLatitude, minLongitude, maxLongitude, acBuilder);

        acBuilder.andNot(ac.id.in(JPAExpressions
                .select(bd.accommodation.id)
                .from(bd)
                .where(bdBuilder)));

        QueryResults<SearchAccommodationDto> results = getQueryResults(acBuilder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<SearchAccommodationDto> findByBuildingType(String buildingType, Long memberId, Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(ac.buildingType.eq(buildingType));

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

        builder.and(ac.city.startsWith(city));

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

        acBuilder.and(ac.capacity.goe(guestNum));

        acBuilder.andNot(ac.id.in(JPAExpressions
                .select(bd.accommodation.id)
                .from(bd)
                .where(bdBuilder)));

        return queryFactory
                .select(ac.price)
                .from(ac)
                .where(acBuilder)
                .orderBy(ac.price.asc())
                .fetch();
    }

    private QueryResults<SearchAccommodationDto> getQueryResults(BooleanBuilder builder, Long memberId, Pageable page) {
        QueryResults<SearchAccommodationDto> results;

        if (memberId != null) {
            results = queryFactory
                    .select(new QSearchAccommodationDto(ac.id, ac.city, ac.gu, ac.address, ac.title, ac.bathroomNum, ac.bedroomNum,
                            ac.bedNum, ac.price, ac.capacity, ac.contact, ac.latitude, ac.longitude, ac.rating, ac.reviewNum,
                            ac.accommodationType, ac.buildingType, ac.hostName, bookmark))
                    .from(ac)
                    .where(builder)
                    .leftJoin(bookmark)
                    .on(bookmark.accommodation.id.eq(ac.id).and(bookmark.member.id.eq(memberId)))
                    .orderBy(ac.randId.asc())
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetchResults();
        } else {
            results = queryFactory
                    .select(new QSearchAccommodationDto(ac.id, ac.city, ac.gu, ac.address, ac.title, ac.bathroomNum, ac.bedroomNum,
                            ac.bedNum, ac.price, ac.capacity, ac.contact, ac.latitude, ac.longitude, ac.rating, ac.reviewNum,
                            ac.accommodationType, ac.buildingType, ac.hostName))
                    .from(ac)
                    .where(builder)
                    .orderBy(ac.randId.asc())
                    .offset(page.getOffset())
                    .limit(page.getPageSize())
                    .fetchResults();
        }

        return results;
    }

    private AccommodationDto getQueryResult(Long memberId, Long accommodationId) {
        AccommodationDto result;

        if (memberId != null) {
            result = queryFactory
                    .select(new QAccommodationDto(ac.id, ac.city, ac.gu, ac.address, ac.title, ac.bathroomNum, ac.bedroomNum,
                            ac.bedNum, ac.capacity, ac.price, ac.contact, ac.latitude, ac.longitude, ac.locationDesc, ac.transportationDesc,
                            ac.accommodationDesc, ac.rating, ac.reviewNum, ac.accommodationType, ac.buildingType, ac.hostName, ac.hostDesc,
                            ac.hostReviewNum, bookmark))
                    .from(ac)
                    .where(ac.id.eq(accommodationId))
                    .leftJoin(bookmark)
                    .on(bookmark.accommodation.id.eq(ac.id).and(bookmark.member.id.eq(memberId)))
                    .fetchOne();
        } else {
            result = queryFactory
                    .select(new QAccommodationDto(ac.id, ac.city, ac.gu, ac.address, ac.title, ac.bathroomNum, ac.bedroomNum,
                            ac.bedNum, ac.capacity, ac.price, ac.contact, ac.latitude, ac.longitude, ac.locationDesc, ac.transportationDesc,
                            ac.accommodationDesc, ac.rating, ac.reviewNum, ac.accommodationType, ac.buildingType, ac.hostName, ac.hostDesc,
                            ac.hostReviewNum))
                    .from(ac)
                    .where(ac.id.eq(accommodationId))
                    .fetchOne();
        }

        return result;
    }

    private void setSearchKeywordQuery(String searchKeyword, BooleanBuilder builder) {
        if (searchKeyword != null && !searchKeyword.equals("")) {
            String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

            searchKeyword = searchKeyword.replace("특별시", "")
                    .replace("광역시", "")
                    .replaceAll(match, "");

            for (String keyword : searchKeyword.split(" ")) {
                if (keyword.charAt(keyword.length() - 1) == '시') {
                    keyword = keyword.substring(0, keyword.length() - 1);
                }
                builder.and(ac.city.startsWith(keyword).or(ac.gu.startsWith(keyword)));
            }
        }
    }

    private void setCheckInCheckOutQuery(LocalDate checkIn, LocalDate checkout, BooleanBuilder builder) {
        builder.and(bd.date.goe(checkIn));

        if (checkout != null) {
            builder.and(bd.date.before(checkout));
        }
    }

    private void setAccommodationTypesQuery(String types, BooleanBuilder builder) {
        if (types != null) {
            BooleanBuilder typeBuilder = new BooleanBuilder();

            for (String type : types.split(" ")) {
                typeBuilder.or(ac.accommodationType.eq(type));
            }
            builder.and(typeBuilder);
        }
    }


    private void setCoordinate(Float minLatitude, Float maxLatitude, Float minLongitude, Float maxLongitude, BooleanBuilder builder) {

        if (minLatitude != null && maxLatitude != null && minLongitude != null && maxLongitude != null) {
            builder.and(ac.latitude.between(minLatitude, maxLatitude));
            builder.and(ac.longitude.between(minLongitude, maxLongitude));
        }
    }

    private void setPriceQuery(Integer minPrice, Integer maxPrice, BooleanBuilder builder) {

        if (minPrice != null && maxPrice != null) {
            builder.and(ac.price.between(minPrice, maxPrice));
        }
    }
}