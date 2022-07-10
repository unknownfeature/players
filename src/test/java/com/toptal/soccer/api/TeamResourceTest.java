package com.toptal.soccer.api;

import com.toptal.soccer.dto.Login;
import com.toptal.soccer.dto.Player;
import com.toptal.soccer.dto.Team;
import com.toptal.soccer.dto.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@TestPropertySource("classpath:application-test.properties")
@WebMvcTest({UserResource.class, TeamResource.class})
public class TeamResourceTest extends BaseResourceTest {

    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String ANOTHER_NAME = "another_name_";
    public static final String ANOTHER_COUNTRY = "another_country_";

    @Value("${team.default.budget:5000000}")
    private String defaultBudget;

    @Value("${team.default.player.count:20}")
    private int defaultPlayerCount;

    @Test
    public void testAuthorizedUserCanUpdateTeam() throws Exception {

        User user = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(user.getId());


        // log in with the second user
        Login loginResult = loginAndReturnResult(EMAIL, PASSWORD);

        // find the user's team

        Team team = getTeamAndReturnResult(user.getId(), loginResult.getToken());
        // create update request
        Team updated = getTeamForUpdate(team);

        Team updatedAfterRequest = updateTeamAndReturnResult(updated, user.getId(), loginResult.getToken());

        Assertions.assertEquals(team.getId(), updatedAfterRequest.getId());
        // check that the rest of the fields is still the same
        Assertions.assertEquals(team.getNumberOfPlayers(), updatedAfterRequest.getNumberOfPlayers());
        Assertions.assertEquals(team.getBudget(), updatedAfterRequest.getBudget());
        Assertions.assertEquals(team.getValue(), updatedAfterRequest.getValue());

        // check that name and country have changed
        Assertions.assertNotEquals(team.getName(), updatedAfterRequest.getName());
        Assertions.assertNotEquals(team.getCountry(), updatedAfterRequest.getCountry());

    }



    @Test
    public void testNonAuthorizedUserCantUpdatePlayer() throws Exception {

        User firstUser = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(firstUser.getId());

        User secondUser = createAndReturnUser(OTHER_EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(secondUser.getId());

        Login loginResultFirst = loginAndReturnResult(EMAIL, PASSWORD);
        Assertions.assertNotNull(loginResultFirst);

        Login loginResultSecond = loginAndReturnResult(OTHER_EMAIL, PASSWORD);
        Assertions.assertNotNull(loginResultSecond);

        Team firstUsersTeam = getTeamAndReturnResult(firstUser.getId(), loginResultFirst.getToken());

        // create update request
        Team updated = getTeamForUpdate(firstUsersTeam);
        updateTeam(updated, firstUsersTeam.getId(), loginResultSecond.getToken()).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    }

    @Test
    public void testNonAuthenticatedUserCantUpdatePlayer() throws Exception {
        User firstUser = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(firstUser.getId());


        Login loginResultFirst = loginAndReturnResult(EMAIL, PASSWORD);
        Assertions.assertNotNull(loginResultFirst);

        Team firstUsersTeam = getTeamAndReturnResult(firstUser.getId(), loginResultFirst.getToken());
        Team updated = getTeamForUpdate(firstUsersTeam);
        updateTeam(updated, firstUsersTeam.getId(), null).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
    }


    @Test
    public void testAuthorizedUserCanReadAllPlayersIfSizeIsGreater() throws Exception {
        User user = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(user.getId());

        // log in with second user
        Login loginResult = loginAndReturnResult(EMAIL, PASSWORD);

        // find the user's team

        Team team = getTeamAndReturnResult(user.getId(), loginResult.getToken());

        List<Player> players = getPlayersAndReturnList(team.getId(), defaultPlayerCount + TEN, loginResult.getToken());

        Assertions.assertEquals(defaultPlayerCount, players.size());
    }

    @Test
    public void testAuthorizedUserCanReadAllPlayersIfSizeIsLess() throws Exception {
        User user = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(user.getId());

        // log in with second user
        Login loginResult = loginAndReturnResult(EMAIL, PASSWORD);

        // find the user's team

        Team team = getTeamAndReturnResult(user.getId(), loginResult.getToken());

        List<Player> players = getPlayersAndReturnList(team.getId(), defaultPlayerCount / 2, loginResult.getToken());

        Assertions.assertEquals(defaultPlayerCount / 2, players.size());
    }
    @Test
    public void testNonAuthorizedUserCantReadAllPlayers() throws Exception {
        User firstUser = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(firstUser.getId());

        // log in with second firstUser
        Login loginResultFirst = loginAndReturnResult(EMAIL, PASSWORD);

        // find the firstUser's team
        Team team = getTeamAndReturnResult(firstUser.getId(), loginResultFirst.getToken());

        User secondUser = createAndReturnUser(OTHER_EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(secondUser.getId());

        // log in with second firstUser
        Login loginResultSecond = loginAndReturnResult(EMAIL, PASSWORD);

        getPlayers(team.getId(), defaultPlayerCount + TEN, loginResultSecond.getToken()).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    }

    @Test
    public void testNonAuthenticatedUserCantReadAllPlayers() throws Exception {
        User firstUser = createAndReturnUser(EMAIL, PASSWORD);
        // make sure it has been created
        Assertions.assertNotNull(firstUser.getId());

        // log in with second firstUser
        Login loginResultFirst = loginAndReturnResult(EMAIL, PASSWORD);

        // find the firstUser's team
        Team team = getTeamAndReturnResult(firstUser.getId(), loginResultFirst.getToken());

        getPlayers(team.getId(), defaultPlayerCount + TEN, null).andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));

    }

    private Team getTeamForUpdate(Team team) {
        Team updated = new Team();
        // set the same id
        updated.setId(team.getId());
        // different name and country (and players)
        String newName = ANOTHER_NAME + team.getName();
        String newCountry = ANOTHER_COUNTRY + team.getCountry();

        updated.setName(newName);
        updated.setCountry(newCountry);
        // should not be updated
        updated.setBudget(TEN + team.getBudget());
        updated.setNumberOfPlayers(team.getNumberOfPlayers() + TEN);
        updated.setValue(TEN + team.getValue());
        return updated;
    }
}
