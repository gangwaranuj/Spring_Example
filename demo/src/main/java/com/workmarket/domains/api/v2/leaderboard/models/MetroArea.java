package com.workmarket.domains.api.v2.leaderboard.models;

import com.google.common.collect.ImmutableMap;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Iterator;

/*
 * Created by bruno on 5/26/16.
 */
public class MetroArea {
    private String name;
    private String metroName;

    public static final String URL_SOURCE = "http://staging-wmrp.herokuapp.com/dma.json";
    public static ImmutableMap<String, String> METRO_AREAS;

    public MetroArea(String city, String state){
        this.name = city == null || state == null ? null : METRO_AREAS.get(city.toLowerCase() + "," + state.toLowerCase());
        if(this.name == null){
            //typo or invalid location, making virtual
            this.name = METRO_AREAS.get("virtual,virtual");
        }
        if(this.name != null) {
            int index = this.name.indexOf("&");
            this.metroName = this.name.substring(0, index);
        }
    }

    public static void setMetroAreas(JSONObject json) {
        if(json != null && json.length() > 0) {
            ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
            Iterator keys = json.keys();
            try {
                while (keys.hasNext()) {
                    String key = (String) keys.next();
                    builder.put(key, json.getString(key));
                }
            } catch(JSONException e){}

            METRO_AREAS = builder.build();
        }
    }

    public String getName() {
        return name;
    }

    public String getMetroName() {
        return metroName;
    }
}
