package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import server.shareboard.domain.ListingService;
import server.shareboard.domain.RecentMatchEntry;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final ListingService service;

    public MatchController(ListingService service) {
        this.service = service;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<RecentMatchEntry>> getRecent(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 50);
        return new ResponseEntity<>(service.getRecentMatches(safeLimit), HttpStatus.OK);
    }
}