package com.workmarket.domains.api.v2.leaderboard.models;

import com.workmarket.ScreeningServiceClient;
import com.workmarket.dao.leaderboard.LeaderboardDAO;
import com.workmarket.helpers.WMCallable;
import com.workmarket.screening.dto.ScreeningSearchRequest;
import com.workmarket.screening.model.Screening;
import com.workmarket.service.web.WebRequestContextProvider;
import com.workmarket.vo.ScreeningSearchResponse;
import org.json.JSONException;
import org.json.JSONObject;
import rx.functions.Action1;
import rx.functions.Func1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by bruno on 5/31/16.
 */

public final class Leaderboard {
    private  ExecutorService executor;
    private List<Future<Boolean>> futures;
    private Date createdAt = new Date();
    private boolean isLoaded = false;
    private HashMap<String, ArrayList<Screening>> screenedUsers = null;
    private static final long UPDATE_INTERVAL = 1000 * 60 * 60 * 24; //24 hours
    private static Leaderboard leaderboard = null;
    private ScreeningServiceClient screeningServiceClient = new ScreeningServiceClient();

    private Leaderboard(){}

    private Leaderboard(final LeaderboardDAO dao, final WebRequestContextProvider webRequestContextProvider){
        this.initDependencies(dao, webRequestContextProvider);
    }

    public static Leaderboard getInstance(final LeaderboardDAO dao, final WebRequestContextProvider webRequestContextProvider){
        if(leaderboard == null || Leaderboard.shouldRefresh()) {
            return new Leaderboard(dao, webRequestContextProvider); //will be assigned later if successful
        }
        //cache hit
        return leaderboard;
    }

    private void initDependencies(final LeaderboardDAO dao, final WebRequestContextProvider webRequestContextProvider) {
        this.executor = Executors.newFixedThreadPool(4);
        this.futures = new ArrayList<Future<Boolean>>();

        this.futures.add(executor.submit(new WMCallable<Boolean>(webRequestContextProvider) {
            public Boolean apply() throws Exception {
                MetroArea.setMetroAreas(jsonFromURL(MetroArea.URL_SOURCE));
                return MetroArea.METRO_AREAS.size() > 0;
            }
        }));

        this.futures.add(executor.submit(new WMCallable<Boolean>(webRequestContextProvider) {
            public Boolean apply() throws Exception {
                LeaderboardIndustry.setAllIndustries(jsonFromURL(LeaderboardIndustry.URL_SOURCE));
                return LeaderboardIndustry.ALL_INDUSTRIES.size() > 0;
            }
        }));

        final Leaderboard self = this;

        this.futures.add(executor.submit(new WMCallable<Boolean>(webRequestContextProvider) {
            public Boolean apply() throws Exception {
                final HashMap<String, ArrayList<Screening>> userScreenings = new HashMap<>();
                screeningServiceClient.search(ScreeningSearchRequest.builder().setLimit(Integer.MAX_VALUE).build(), webRequestContextProvider.getRequestContext())
                        .onErrorReturn(new Func1<Throwable, ScreeningSearchResponse>() {
                            @Override
                            public ScreeningSearchResponse call(Throwable throwable) {
                                return null;
                            }
                        })
                        .forEach(new Action1<ScreeningSearchResponse>() {
                            @Override
                            public void call(ScreeningSearchResponse screeningSearchResponse) {
                                if (screeningSearchResponse != null) {
                                    for (Screening s : screeningSearchResponse.getResults()) {
                                        ArrayList<Screening> screenings = userScreenings.get(s.getUserId());
                                        if (screenings == null) {
                                            screenings = new ArrayList<>();
                                            userScreenings.put(s.getUserId(), screenings);
                                        }

                                        screenings.add(s);
                                    }
                                }
                            }
                        });

                self.screenedUsers = userScreenings;
                return self.screenedUsers.size() > 0;
            }}));


        this.futures.add(executor.submit(new WMCallable<Boolean>(webRequestContextProvider) {
            public Boolean apply() throws Exception {
                List<LeaderboardUser> users = dao.getAllUsersForLeaderboard(self);
                if (users != null) {
                    for (LeaderboardUser u : users) {
                        //add worker services revenue here
                        u.addWorkerServicesRevenue(self.screenedUsers.get(Long.toString(u.getId())));

                        for (LeaderboardIndustry i : LeaderboardIndustry.ALL_INDUSTRIES) {
                            for (Persona p : i.getPersonas()) {
                                if (p.isUserInPersona(u))
                                    p.addUser(u);
                            }
                        }
                    }

                    //have to loop through again to calculate LTV
                    for (LeaderboardIndustry i : LeaderboardIndustry.ALL_INDUSTRIES) {
                        for (Persona p : i.getPersonas()) {
                            p.calculateStats();
                        }
                    }

                }

                return users != null;
            }
         }));
    }

    public Leaderboard waitForAllData() {
        if (!this.isLoaded()) {
            Boolean success = this.waitForDependencies();
            if (success) {
                Future<Boolean> f = this.futures.get(3); //db call
                try {
                    if (!f.get()) {
                        success = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();;
                    success = false;
                }

                this.isLoaded = success;
                if(this.isLoaded())
                    leaderboard = this;
            }
        }

        return this;
    }

    public boolean waitForDependencies() {
        Boolean success = true;
        for (Future<Boolean> f : this.futures.subList(0, 3)) { //only care about first 3 futures here
             try {
                if(!f.get()){
                    success = false;
                }
            } catch (Exception e) {
                success = false;
                 e.printStackTrace();;
            }
        }
        return success;
    }

    public static boolean shouldRefresh() {
        return new Date().getTime() - leaderboard.createdAt.getTime() >= UPDATE_INTERVAL;
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject json = new JSONObject();

        for(LeaderboardIndustry i : LeaderboardIndustry.ALL_INDUSTRIES) {
            json.put(i.getName(), i.toJSON());
        }

        return json;
    }

    public static JSONObject jsonFromURL(String url) {
        BufferedReader br = null;
        JSONObject json = null;
        try {
            br = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = br.readLine()) != null) {
                sb.append(line);
            }

            json = new JSONObject(sb.toString());
        } catch (Exception ex){
            ex.printStackTrace();
        } finally {
            if (br != null)
                try {
                    br.close();
                } catch(IOException ioe){}
        }

        return json;
    }
}
