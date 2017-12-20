package com.workmarket.api.helpers;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.test.IntegrationTest;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertTrue;

/**
 * Created by bruno on 5/26/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)

public class LeaderboardServiceIT extends BaseServiceIT{
    @Autowired LeaderboardService leaderboardService;

    @Test
    @Transactional
    public void getLeaderboardJSON() {
        JSONObject json = leaderboardService.getLeaderboardJSON();
        assertTrue(json != null);
    }
}