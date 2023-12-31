package com.toptal.soccer.orchestrator;

import com.toptal.soccer.crypto.iface.PasswordHasher;
import com.toptal.soccer.manager.iface.UserManager;
import com.toptal.soccer.model.User;
import com.toptal.soccer.orchestrator.iface.CreateNewTeamOrchestrator;
import com.toptal.soccer.orchestrator.iface.RegisterUserOrchestrator;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.Validate;

/**
 * This class provides functionality for a new user registration
 */
public class RegisterUserOrchestratorImpl implements RegisterUserOrchestrator {

    private final UserManager userManager;
    private final PasswordHasher passwordHasher;
    private final CreateNewTeamOrchestrator createNewTeamOrchestrator;

    public RegisterUserOrchestratorImpl(UserManager userManager,
                                        PasswordHasher passwordHasher,
                                        CreateNewTeamOrchestrator createNewTeamOrchestrator) {
        this.userManager = userManager;
        this.passwordHasher = passwordHasher;
        this.createNewTeamOrchestrator = createNewTeamOrchestrator;
    }

    @Override
    @Transactional
    public User register(String email, String password) {

        Validate.notEmpty(email, Constants.EMAIL_CAN_T_BE_EMPTY);
        Validate.notEmpty(password, Constants.PASSWORD_CAN_T_BE_EMPTY);

        if (userManager.findUserByEmail(email).isEmpty()) {

            try {
                final User user = new User();
                user.setEmail(email);
                user.setPasswordHash(passwordHasher.hash(password));
                user.setTeam(createNewTeamOrchestrator.create());

                return userManager.save(user);

            } catch (Exception e) {
                throw new IllegalArgumentException(Constants.CAN_T_CREATE_A_USER, e);
            }

        }

        throw new IllegalArgumentException(Constants.USER_WITH_SUCH_EMAIL_ALREADY_EXISTS);
    }


}
