package com.example.skip.repository.reservation;

import com.example.skip.entity.Rent;
import com.example.skip.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long>,
        QuerydslPredicateExecutor<Reservation>,
        ReservationRepositoryCustom {
    // 관리자ID 기준 예약 + 아이템 정보까지 모두 fetch
    @Query("SELECT DISTINCT r FROM Reservation r " +
            "JOIN FETCH r.rent rent " +
            "JOIN FETCH r.reservationItems ri " +
            "JOIN FETCH ri.itemDetail id " +
            "JOIN FETCH id.item i " +
            "WHERE rent.user.userId = :userId")
    List<Reservation> findReservationsByUserId(@Param("userId") Long userId);


    long countByRentAndCreatedAt(Rent rent, LocalDateTime createdAt);

    long countByRentAndCreatedAtBetween(Rent rent, LocalDateTime createdAtAfter, LocalDateTime createdAtBefore);

    long countByRentAndCreatedAtAfterAndCreatedAtLessThan(Rent rent, LocalDateTime createdAtAfter, LocalDateTime createdAtIsLessThan);

    Long countByRent_User_UserId(Long userId);

    Long countByRent_RentId(Long rentId);

    //해당 렌탈샵 가져오기
    List<Reservation> findByRentIn(List<Rent> rents);

    Reservation findDetailByReserveId(Long reserveId);
}