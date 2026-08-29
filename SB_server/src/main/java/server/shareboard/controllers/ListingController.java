package server.shareboard.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.shareboard.domain.ListingService;
import server.shareboard.domain.Result;
import server.shareboard.models.Listing;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService service;

    public ListingController(ListingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Listing listing) {
        Result<Listing> result = service.create(listing);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.CREATED);
    }

    @GetMapping("/{listingId}")
    public ResponseEntity<?> findById(@PathVariable("listingId") int listingId) {
        Result<Listing> result = service.findById(listingId);

        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }

        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> findAll(){
        Result<List<Listing>> result = service.findAll();
        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> findByUserId(@RequestParam int userId) {
        Result<List<Listing>> result = service.findByUserId(userId);
        if (!result.isSuccess()) {
            return ErrorResponse.build(result);
        }
        return new ResponseEntity<>(result.getPayload(), HttpStatus.OK);
    }
}