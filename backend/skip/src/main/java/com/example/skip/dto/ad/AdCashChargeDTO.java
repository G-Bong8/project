package com.example.skip.dto.ad;

import lombok.Data;

@Data
public class AdCashChargeDTO {
    private String impUid;
    private String merchantUid;
    private Long amount; // 결제 금액
    private Long userId;
    private Long rentId;
    private String pgProvider;
}