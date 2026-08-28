package server.shareboard.domain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.shareboard.data.ListingRepository;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class ListingServiceTest {

    @Autowired
    ListingService service;

    @MockBean
    ListingRepository repository;

    

}