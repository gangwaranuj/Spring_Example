package com.workmarket.dao.leaderboard;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.api.v2.leaderboard.models.Leaderboard;

import java.util.List;

/**
 * Created by bruno on 5/23/16.
 */

public interface LeaderboardDAO {
    List getAllUsersForLeaderboard(final Leaderboard leaderboard);
}
