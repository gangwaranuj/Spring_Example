package com.workmarket.domains.api.v2.leaderboard.models;

import java.util.Iterator;
import com.google.common.collect.ImmutableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by bruno on 5/31/16.
 */
public final class LeaderboardIndustry {
    public static final String URL_SOURCE = "http://www.wmrecruiters.com/personas.json";
    public static ImmutableList<LeaderboardIndustry> ALL_INDUSTRIES;

    private String name;
    private ImmutableList<Persona> personas;

    public LeaderboardIndustry(String name, JSONObject json) throws JSONException{
        this.name = name;
        addPersonas(json);
    }

    public void addPersonas(JSONObject json) throws JSONException{
        ImmutableList.Builder<Persona> builder = ImmutableList.builder();
        JSONArray names = json.names();

        for(int i=0; i<names.length(); i++){
            String name = names.getString(i);
            builder.add(new Persona(name, json.getJSONObject(name)));
        }

        this.personas = builder.build();
    }

    public String getName() {
        return name;
    }

    public ImmutableList<Persona> getPersonas() {
        return personas;
    }

    public static void setAllIndustries(JSONObject json) {
        if(json != null && json.length() > 0) {
            ImmutableList.Builder<LeaderboardIndustry> builder = ImmutableList.builder();
            Iterator keys = json.keys();
            try {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    builder.add(new LeaderboardIndustry(key, json.getJSONObject(key)));
                }
            } catch(JSONException e){}

            ALL_INDUSTRIES = builder.build();
        }
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        for(Persona p : personas){
            json.put(p.getName(), p.toJSON());
        }

        return json;
    }

}
