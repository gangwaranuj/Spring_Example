package com.workmarket.api.helpers;

import com.workmarket.dao.leaderboard.LeaderboardDAO;
import com.workmarket.domains.api.v2.leaderboard.models.Leaderboard;
import com.workmarket.domains.api.v2.leaderboard.models.MetroArea;
import com.workmarket.service.web.WebRequestContextProvider;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * Created by bruno on 5/26/16.
 */
@Service
public class LeaderboardServiceImpl implements LeaderboardService {
    @Autowired LeaderboardDAO leaderboardDAO;
    @Autowired WebRequestContextProvider webRequestContextProvider;

    public JSONObject getLeaderboardJSON() {
        Leaderboard leaderboard = Leaderboard.getInstance(leaderboardDAO, webRequestContextProvider);

        if(!leaderboard.isLoaded()) {
            leaderboard = leaderboard.waitForAllData();
        }

        if(leaderboard.isLoaded()) {
            try {
                JSONObject response = new JSONObject();
                JSONObject mappingArray = new JSONObject();

                for (String m : MetroArea.METRO_AREAS.values()) {
                    String[] temp = m.split("&");
                    mappingArray.put(temp[1], temp[0]);
                }

                response.put("metroMapping", mappingArray);
                response.put("industries", leaderboard.toJSON());
                return response;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
