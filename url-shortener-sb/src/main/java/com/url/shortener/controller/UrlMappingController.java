package com.url.shortener.controller;

import com.url.shortener.dtos.ClickEventDTO;
import com.url.shortener.dtos.UrlMappingDTO;
import com.url.shortener.models.User;
import com.url.shortener.service.UrlMappingService;
import com.url.shortener.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/urls")
@AllArgsConstructor
public class UrlMappingController {

    private UrlMappingService urlMappingService;
    private UserService userService;

    // ================= SHORTEN =================

    @PostMapping("/shorten")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UrlMappingDTO> createShortUrl(
            @RequestBody UrlMappingDTO request,
            Principal principal) {

        if (request.getOriginalUrl() == null || request.getOriginalUrl().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        User user = userService.findByUsername(principal.getName());

        UrlMappingDTO urlMappingDTO =
                urlMappingService.createShortUrl(request.getOriginalUrl(), user);

        return ResponseEntity.ok(urlMappingDTO);
    }

    // ================= MY URLS =================

    @GetMapping("/myurls")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<UrlMappingDTO>> getUserUrls(Principal principal) {

        User user = userService.findByUsername(principal.getName());
        List<UrlMappingDTO> urls = urlMappingService.getUrlsByUser(user);

        return ResponseEntity.ok(urls);
    }

    // ================= ANALYTICS =================

    @GetMapping("/analytics/{shortUrl}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUrlAnalytics(
            @PathVariable String shortUrl,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

            LocalDateTime start = LocalDateTime.parse(startDate.trim(), formatter);
            LocalDateTime end = LocalDateTime.parse(endDate.trim(), formatter);

            List<ClickEventDTO> result =
                    urlMappingService.getClickEventsByDate(shortUrl, start, end);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }

    // ================= TOTAL CLICKS =================

//    @GetMapping("/totalClicks")
//    @PreAuthorize("hasRole('USER')")   // ✅ FIXED HERE
//    public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(
//            Principal principal,
//            @RequestParam String startDate,
//            @RequestParam String endDate) {
//
//        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//        LocalDate start = LocalDate.parse(startDate.trim(), formatter);
//        LocalDate end = LocalDate.parse(endDate.trim(), formatter);
//
//        User user = userService.findByUsername(principal.getName());
//
//        Map<LocalDate, Long> totalClicks =
//                urlMappingService.getTotalClicksByUserAndDate(user, start, end);
//
//        return ResponseEntity.ok(totalClicks);
//    }
//@GetMapping("/totalClicks")
//@PreAuthorize("hasRole('USER')")
//public ResponseEntity<Map<LocalDate, Long>> getTotalClicksByDate(
//        Principal principal,
//        @RequestParam String startDate,
//        @RequestParam String endDate) {
//
//    DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
//
//    LocalDate start = LocalDate.parse(startDate.trim(), formatter);
//    LocalDate end = LocalDate.parse(endDate.trim(), formatter);
//
//    User user = userService.findByUsername(principal.getName());
//
//    // Get real data from DB
//    Map<LocalDate, Long> totalClicks =
//            urlMappingService.getTotalClicksByUserAndDate(user, start, end);
//
//    // 🔥 Fill missing dates with 0
//    Map<LocalDate, Long> fullResponse = new LinkedHashMap<>();
//
//    LocalDate current = start;
//    while (!current.isAfter(end)) {
//
//        fullResponse.put(
//                current,
//                totalClicks.getOrDefault(current, 0L)
//        );
//
//        current = current.plusDays(1);
//    }
//
//    return ResponseEntity.ok(fullResponse);
//}

    @GetMapping("/totalClicks")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getTotalClicksByDate(
            Principal principal,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {

            // Parse dates
            LocalDate start = LocalDate.parse(startDate.trim());
            LocalDate end = LocalDate.parse(endDate.trim());

            if (end.isBefore(start)) {
                return ResponseEntity.badRequest().build();
            }

            // Get logged-in user
            User user = userService.findByUsername(principal.getName());

            // Get data from service
            Map<LocalDate, Long> totalClicks =
                    urlMappingService.getTotalClicksByUserAndDate(user, start, end);

            // Convert LocalDate → String (for clean JSON)
            Map<String, Long> response = new LinkedHashMap<>();

            totalClicks.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry ->
                            response.put(entry.getKey().toString(), entry.getValue())
                    );

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
