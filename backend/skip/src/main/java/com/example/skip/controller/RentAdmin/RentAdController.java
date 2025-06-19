package com.example.skip.controller.RentAdmin;

import com.example.skip.dto.ad.AdCashChargeDTO;
import com.example.skip.service.RentAdService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/rentAdmin")
@RequiredArgsConstructor
public class RentAdController {

    private final RentAdService rentAdService;

    @GetMapping("/cash")
    public ResponseEntity<Map<String, Integer>> getCash(
            @RequestParam Long userId,
            @RequestParam(required = false) Long rentId) {
        int remaining = rentAdService.getCash(userId, rentId);
        return ResponseEntity.ok(Map.of("remainingCash", remaining));
    }

    @PostMapping("/cash")
    public ResponseEntity<Map<String, Integer>> chargeCash(@RequestBody AdCashChargeDTO dto) throws Exception {
        int remaining = rentAdService.chargeCash(dto);
        return ResponseEntity.ok(Map.of("remainingCash", remaining));
    }

    @PostMapping("/boost")
    public ResponseEntity<Map<String, Integer>> purchaseBoost(@RequestBody Map<String, Object> body) {
        Long userId = ((Number) body.get("userId")).longValue();
        Long rentId = body.get("rentId") != null ? ((Number) body.get("rentId")).longValue() : null;
        int boost = ((Number) body.get("boost")).intValue();
        int cpb = ((Number) body.get("cpb")).intValue();
        int remaining = rentAdService.purchaseBoost(userId, rentId, boost, cpb);
        return ResponseEntity.ok(Map.of("remainingCash", remaining));
    }

    @PostMapping(value = "/banner", consumes = "multipart/form-data")
    public ResponseEntity<Map<String, Integer>> submitBanner(
            @RequestParam Long userId,
            @RequestParam(required = false) Long rentId,
            @RequestParam Integer cpcBid,
            @RequestPart(value = "bannerImage", required = false) MultipartFile bannerImage
    ) {
        int remaining = rentAdService.submitBanner(userId, rentId, cpcBid, bannerImage);
        return ResponseEntity.ok(Map.of("remainingCash", remaining));
    }
}