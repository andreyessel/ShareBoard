package server.shareboard.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;
import server.shareboard.models.User;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class UserJdbcClientRepositoryTest {

    @Autowired
    private UserJdbcClientRepository repository;

    @Autowired
    private JdbcClient jdbcClient;

    @BeforeEach
    void setup() {
        jdbcClient.sql("call set_known_good_state();").update();
    }

    //happy path
    @Test
    void shouldFindByUserId() {
        //Act
        User user = repository.findById(1);

        //Assert
        assertNotNull(user);
        assertEquals(1,user.getUserId());
        assertEquals("test_org", user.getUsername());
        assertTrue(user.isOrganization());
    }

    //unhappy path
    @Test
    void shouldReturnNullWhenUserIdDoesNotExist() {
        //Act
        User notUser = repository.findById(8888);

        //Assert
        assertNull(notUser);
    }

    //happy path
    @Test
    void shouldFindExistingUserByUsername() {
        //Act
        User user = repository.findByUsername("test_org");

        //Assert
        assertNotNull(user);
        assertEquals(1, user.getUserId());
    }

    //unhappy path
    @Test
    void shouldReturnNullWhenUsernameDoes() {
        //Act
        User notUser = repository.findByUsername("notAUser");
        User nullUser = repository.findByUsername(null);

        //Assert
        assertNull(notUser);
        assertNull(nullUser);
    }


    @Test
    void shouldCreateANewUser() {
        //Act
        User newUser = repository.create(TestDataHelper.userToCreate());
          newUser.setUserId(3);

        //Assert
        assertEquals(3, newUser.getUserId());
        assertEquals("newUser", newUser.getUsername());
    }


}