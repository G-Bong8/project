package com.example.skip.repository.reservation;

import com.example.skip.dto.projection.ReviewWriteDTO;
import com.example.skip.entity.Reservation;
import com.example.skip.entity.ReservationItem;
import com.example.skip.enumeration.ItemCategory;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationItemRepository extends JpaRepository<ReservationItem, Long> {
    @Query("""
    SELECT COUNT(DISTINCT p)
    FROM ReservationItem ri
    JOIN ri.reservation r
    JOIN r.payment p
    JOIN ri.itemDetail id
    JOIN id.item i
    WHERE i.category = :category
    AND p.createdAt BETWEEN :start AND :end
    """)
    Long countPaymentsByItemCategory(
            @Param("category") ItemCategory category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // 비관적 락 : 다른 트랜잭션의 읽기/쓰기 모두 차단(재고 수량 변경 위하기 위해 먼저 예약상세 접근 막기)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM ReservationItem r WHERE r.rentItemId = :rentItemId")
    Optional<ReservationItem> findByIdWithLock(@Param("rentItemId") Long rentItemId);
    Optional<ReservationItem> findByReservation(Reservation reservation);


    @Query("""
    SELECT COUNT(DISTINCT p)
    FROM ReservationItem ri
    JOIN ri.reservation r
    JOIN r.payment p
    JOIN ri.itemDetail id
    JOIN id.item i
    WHERE i.category = :category
      AND r.rent.user.userId = :userId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Long countPaymentsByUserAndItemCategory(
            @Param("userId") Long userId,
            @Param("category") ItemCategory category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("""
    SELECT COUNT(DISTINCT p)
    FROM ReservationItem ri
    JOIN ri.reservation r
    JOIN r.payment p
    JOIN ri.itemDetail id
    JOIN id.item i
    WHERE i.category = :category
      AND r.rent.user.userId = :userId
      AND r.rent.rentId = :rentId
      AND p.createdAt BETWEEN :start AND :end
      AND p.status = 'PAID'
    """)
    Long countPaymentsByUserAndItemCategoryAndRent(
            @Param("userId") Long userId,
            @Param("rentId") Long rentId,
            @Param("category") ItemCategory category,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    // ** rentStart~rentEnd 기간 동안 이미 예약된 수량을 합산하는 쿼리(대여기간이 겹치는지 확인) **
    // 사용자가 예약하려는 수량이 가능한지 판단(결제 시)
    @Query("SELECT COALESCE(SUM(ri.quantity), 0) FROM ReservationItem ri " +
            "WHERE ri.itemDetail.itemDetailId = :itemDetailId " +
            "AND ri.rentStart < :rentEnd " +
            "AND ri.rentEnd > :rentStart")
    int getReservedQuantity(@Param("itemDetailId") Long itemDetailId,
                            @Param("rentStart") LocalDateTime rentStart,
                            @Param("rentEnd") LocalDateTime rentEnd);

    // ** 당일 예약 목록을 가져와 재고 차감(스케줄러) **
    List<ReservationItem> findAllByRentStartBetween(LocalDateTime start, LocalDateTime end);


    // 리뷰 작성 페이지
    @Query("""
        SELECT
            ri.rentItemId AS rentItemId,
            i.name AS itemName,
            i.image AS itemImage,
            idt.size AS size,
            ri.rentStart AS rentStart,
            ri.rentEnd AS rentEnd
        FROM ReservationItem ri
        JOIN ri.itemDetail idt
        JOIN idt.item i
        WHERE ri.rentItemId =:rentItemId
        AND ri.reservation.user.userId =:userId 
    """)
    Optional<ReviewWriteDTO> findReviewWriteInfo(Long rentItemId, Long userId);
}