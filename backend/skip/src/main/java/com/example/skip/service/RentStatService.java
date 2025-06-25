package com.example.skip.service;

import com.example.skip.dto.RankedRentDto;
import com.example.skip.dto.request.RankingRequest;
import com.example.skip.entity.DailyRentStat;
import com.example.skip.entity.QDailyRentStat;
import com.example.skip.entity.QRent;
import com.example.skip.entity.Rent;
import com.example.skip.enumeration.UserStatus;
import com.example.skip.repository.DailyRentStatRepository;
import com.example.skip.repository.RentRepository;
import com.example.skip.repository.reservation.ReservationRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RentStatService {
    private final DailyRentStatRepository dailyRentStatRepository;

    private final ReservationRepository reservationRepository;

    private final RentRepository rentRepository;

    private final JPAQueryFactory jpaQueryFactory;

    private static final QDailyRentStat dailyRentStat = QDailyRentStat.dailyRentStat;

    private static final QRent rent = QRent.rent;


    @Scheduled(cron = "0 0 10 * * *")
    public void calculateDailyStats() {
        log.info("일일 통계 집계");
        LocalDate now = LocalDate.now();
        LocalDate statDate = now.minusDays(1);
        List<Rent> rents = rentRepository.findAll();

        for (Rent rent: rents) {
            Long reservationCount = reservationRepository.countByRentAndCreatedAtAfterAndCreatedAtLessThan(rent, statDate.atStartOfDay(), now.atStartOfDay());
            dailyRentStatRepository.save(DailyRentStat.builder()
                    .rent(rent)
                    .region(rent.getRegion())
                    .reservationCount(reservationCount.intValue())
                    .statDate(statDate)
                    .build());
        }
    }

    @Async
    public CompletableFuture<List<RankedRentDto>> getRankedList(RankingRequest rankingRequest) {
        List<Tuple> tuples = jpaQueryFactory
                .select(dailyRentStat.rent, dailyRentStat.reservationCount.sum())
                .from(dailyRentStat)
                .join(dailyRentStat.rent, rent)
                .where(rent.status.eq(UserStatus.APPROVED))
                .where(rankingRequest.toPredicate(dailyRentStat))
                .groupBy(dailyRentStat.rent)
                .orderBy(dailyRentStat.reservationCount.sum().desc())
                .limit(10)
                .fetch();

        AtomicInteger rank = new AtomicInteger(1);

        return CompletableFuture.completedFuture(
                tuples.stream().map(tuple -> new RankedRentDto(
                        tuple.get(dailyRentStat.rent),
                        rank.getAndIncrement(),
                        Optional.ofNullable(tuple.get(dailyRentStat.reservationCount.sum())).orElse(0)
                )).toList()
        );
    }
}
