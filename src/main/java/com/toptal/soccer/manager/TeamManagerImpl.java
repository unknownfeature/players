package com.toptal.soccer.manager;

import com.toptal.soccer.manager.iface.TeamManager;
import com.toptal.soccer.model.Player;
import com.toptal.soccer.model.Team;
import com.toptal.soccer.repo.TeamRepo;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.Validate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

/**
 * This class implements player manager functionality
 */
public class TeamManagerImpl implements TeamManager {

    private final TeamRepo teamRepo;

    public TeamManagerImpl(final TeamRepo teamRepo) {
        this.teamRepo = teamRepo;
    }

    @Override
    @Transactional
    public Optional<Team> findById(Long id) {

        Validate.notNull(id, Constants.ID_CAN_T_BE_NULL);

        return teamRepo.findById(id);
    }

    @Override
    @Transactional
    public List<Player> findPlayersByTeamId(Long teamId, int start, int pageSize) {
        Validate.isTrue(start > 0 && pageSize > 0, Constants.START_AND_PAGE_SIZE_HAVE_TO_BE_GREATER_THAN_ZERO);

        final Page<Player> result = teamRepo.findPlayers(PageRequest.of(start/pageSize, pageSize));

        return result.stream().toList();
    }

    @Override
    @Transactional
    public Team save(Team team) {
        Validate.notNull(team, Constants.
                PLAYER_ID_CAN_T_BE_NULL);

        return teamRepo.save(team);
    }


}