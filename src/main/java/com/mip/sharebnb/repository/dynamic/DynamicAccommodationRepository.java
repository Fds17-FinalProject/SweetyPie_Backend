package com.mip.sharebnb.repository.dynamic;

import com.mip.sharebnb.model.Accommodation;
import com.mip.sharebnb.model.QAccommodation;
import com.mip.sharebnb.model.QBookedDate;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class DynamicAccommodationRepository {

    private final JPAQueryFactory queryFactory;

    QAccommodation ac = new QAccommodation("ac");
    QBookedDate bd = new QBookedDate("bd");

    public Page<Accommodation> findAccommodationsBySearch(String searchKeyword, LocalDate checkIn, LocalDate checkout, int guestNum, Pageable page) {
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
                acBuilder.and(ac.city.contains(keyword).or(ac.gu.contains(keyword)));
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

        QueryResults<Accommodation> results = queryFactory
                .select(ac)
                .from(ac)
                .where(acBuilder)
//                .orderBy(NumberExpression.random().asc())
                .offset(page.getOffset())
                .limit(page.getPageSize())
                .fetchResults();

        return new PageImpl<>(results.getResults(), page, results.getTotal());
    }

    public Page<Accommodation> findAccommodationsByMapSearch(float minLatitude, float maxLatitude,
                                                             float minLongitude, float maxLongitude, @PageableDefault(page = 1) Pageable page) {
        BooleanBuilder builder = new BooleanBuilder();

        builder.and(ac.latitude.gt(minLatitude));
        builder.and(ac.latitude.lt(maxLatitude));

        builder.and(ac.longitude.gt(minLongitude));
        builder.and(ac.longitude.lt(maxLongitude));

        return new PageImpl<>(queryFactory
                .select(ac)
                .from(ac)
                .where(builder)
                .offset(page.getOffset() - page.getPageSize())
                .limit(page.getPageSize())
                .fetch(), page, page.getPageSize());
    }
}