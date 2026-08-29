package server.shareboard.domain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import server.shareboard.data.TestDataHelper;
import server.shareboard.data.UserRepository;
import server.shareboard.models.User;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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

    //happy path
    @Test
    void findByUsernameHappyPath() {
        //Arrange
        User expected = TestDataHelper.existingUser();
        when(repository.findByUsername("existingUser")).thenReturn(expected);

        //Act
        Result<User> result = service.findByUsername("existingUser");

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(expected, result.getPayload());
    }

    //unhappy path
    @Test
    void findByUsernameNotFound(){
        //Arrange
        when(repository.findByUsername("null")).thenReturn(null);

        //Act
        Result<User> result = service.findByUsername("null");

        //Assert
        assertFalse(result.isSuccess());
        assertEquals(ResultType.NOT_FOUND, result.getResultType());
    }

    @Test
    void shouldCreateANewUser() {
        //Arrange
        User userToCreate = TestDataHelper.userToCreate();
        User createdUser = TestDataHelper.existingUser();

        when(repository.create(any(User.class))).thenReturn(createdUser);

        //Act
        Result<User> result = service.create(userToCreate);

        //Assert
        assertTrue(result.isSuccess());
        assertEquals(createdUser, result.getPayload());
    }
}