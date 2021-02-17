package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.dto.AccommodationDto;
import com.mip.sharebnb.dto.QAccommodationDto;
import com.mip.sharebnb.dto.QSearchAccommodationDto;
import com.mip.sharebnb.dto.SearchAccommodationDto;
import com.mip.sharebnb.model.QAccommodation;
import com.mip.sharebnb.model.QBookedDate;
import com.mip.sharebnb.model.QBookmark;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation ac = new QAccommodation("ac");
    QBookedDate bd = new QBookedDate("bd");
    QBookmark bookmark = new QBookmark("bookmark");

    public AccommodationDto findById(Long memberId, Long accommodationId) {
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

    public Page<SearchAccommodationDto> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout, int guestNum, Long memberId, Pageable page) {
        BooleanBuilder bdBuilder = new BooleanBuilder();
        BooleanBuilder acBuilder = new BooleanBuilder();

        if (searchKeyword != null && !searchKeyword.equals("")) {
            String match = "[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]";

            searchKeyword = searchKeyword.replace("특별시", "")
                    .replace("광역시", "")
                    .replaceAll(match, "");

            for (String keyword : searchKeyword.split(" ")) {
                if (keyword.charAt(keyword.length() - 1) == '시') {
                    keyword = keyword.substring(0, keyword.length() - 1);
                }
                acBuilder.and(ac.city.startsWith(keyword).or(ac.gu.startsWith(keyword)));
            }
        }

        acBuilder.and(ac.capacity.goe(guestNum));
        bdBuilder.and(bd.date.goe(checkIn));

        if (checkout != null) {
            bdBuilder.and(bd.date.before(checkout));
        }

        acBuilder.andNot(ac.id.in(JPAExpressions
                .select(bd.accommodation.id)
                .from(bd)
                .where(bdBuilder)));

        QueryResults<SearchAccommodationDto> results = getQueryResults(acBuilder, memberId, page);

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<SearchAccommodationDto> findAccommodationsByMapSearch(float minLatitude, float maxLatitude,
                                                                      float minLongitude, float maxLongitude, Long memberId, Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(ac.latitude.between(minLatitude, maxLatitude));
        builder.and(ac.longitude.between(minLongitude, maxLongitude));

        QueryResults<SearchAccommodationDto> results = getQueryResults(builder, memberId, page);

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
}