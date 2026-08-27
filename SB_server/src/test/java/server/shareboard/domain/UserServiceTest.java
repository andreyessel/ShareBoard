package server.shareboard.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.shareboard.data.TestDataHelper;
import server.shareboard.data.UserRepository;
import server.shareboard.models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserServiceTest {

    @Autowired
    UserService service;

    @MockBean
    UserRepository repository;

    //happy path
    @Test
    void findByIdHappyPath() {
        //Arrange
        User expected = TestDataHelper.existingUser();
        when(repository.findById(1)).thenReturn(expected);

        //Act
        Result<User> result = service.findById(1);

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(expected, result.getPayload());
    }

    //unhappy path
    @Test
    void findByIdNotFound(){
        //Arrange
        when(repository.findById(1)).thenReturn(null);

        //Act
        Result<User> result = service.findById(1);

        //Assert
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }
}