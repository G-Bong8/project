package com.example.skip.repository;

import com.example.skip.dto.admin.AdminDetailDTO;
import com.example.skip.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminReportRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT new com.example.skip.dto.admin.AdminDetailDTO(
            p.paymentId,
            p.merchantUid,
            p.impUid,
            r.name,
            u.username,
            p.totalPrice,
            p.commissionRate,
            p.method,
            p.pgProvider,
            p.status,
            p.createdAt
        )
        FROM Payment p
        JOIN p.reservations res
        JOIN res.user u
        JOIN res.rent r
        WHERE p.createdAt BETWEEN :start AND :end
        ORDER BY p.createdAt
    """)
    List<AdminDetailDTO> findAllDetailsBetween(
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    @Query("""
        SELECT new com.example.skip.dto.admin.AdminDetailDTO(
            p.paymentId,
            p.merchantUid,
            p.impUid,
            r.name,
            u.username,
            p.totalPrice,
            p.commissionRate,
            p.method,
            p.pgProvider,
            p.status,
            p.createdAt
        )
        FROM Payment p
        JOIN p.reservations res
        JOIN res.user u
        JOIN res.rent r
        WHERE p.createdAt BETWEEN :start AND :end
          AND r.user.userId = :userId
        ORDER BY p.createdAt
    """)
    List<AdminDetailDTO> findAllDetailsByUserBetween(
            @Param("userId") Long userId,
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

    @Query("""
        SELECT new com.example.skip.dto.admin.AdminDetailDTO(
            p.paymentId,
            p.merchantUid,
            p.impUid,
            r.name,
            u.username,
            p.totalPrice,
            p.commissionRate,
            p.method,
            p.pgProvider,
            p.status,
            p.createdAt
        )
        FROM Payment p
        JOIN p.reservations res
        JOIN res.user u
        JOIN res.rent r
        WHERE p.createdAt BETWEEN :start AND :end
          AND r.user.userId = :userId
          AND r.rentId = :rentId
        ORDER BY p.createdAt
    """)
    List<AdminDetailDTO> findAllDetailsByUserAndRentBetween(
            @Param("userId") Long userId,
            @Param("rentId") Long rentId,
            @Param("start") LocalDateTime start,
            @Param("end")   LocalDateTime end
    );

}
