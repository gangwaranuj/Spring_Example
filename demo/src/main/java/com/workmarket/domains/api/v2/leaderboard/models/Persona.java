package com.workmarket.domains.api.v2.leaderboard.models;

import com.google.common.collect.ImmutableList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
/**
 * Created by bruno on 5/31/16.
 */
public class Persona {
    private String name;
    private int target = 0;
    private double arpu = 0.0;
    private double arppu = 0.0;
    private double ltv = 0.0;
    private int totalPaid = 0;
    private double revenue = 0.0;
    private ImmutableList<String> specialties;
    private ImmutableList<String> caseSensitiveSpecialties;
    private ImmutableList<String> skills;
    private ImmutableList<String> industries;
    private ImmutableList<String> exclusions;
    private ImmutableList<Integer> warpRequisitionIds;

    //these cannot be immutable
    private HashSet<LeaderboardUser> users = new HashSet<LeaderboardUser>();
    private HashMap<String, Integer> metroBreakdown = new HashMap<String, Integer>();
    private HashMap<String, HashSet<String>> metroUsers = new HashMap<String, HashSet<String>>();
    private static final int LTV_MULTIPLIER = 12;


    public Persona(String name, JSONObject json) throws JSONException{
        this.name = name;
        this.target = json.getInt("target");
        this.specialties = jsonArrayToImmutableList(json.getJSONArray("keywords"));
        this.caseSensitiveSpecialties = jsonArrayToImmutableList(json.getJSONArray("caseSensitiveKeywords"));
        this.skills = jsonArrayToImmutableList(json.getJSONArray("skills"));
        this.industries = jsonArrayToImmutableList(json.getJSONArray("industries"));
        this.exclusions = jsonArrayToImmutableList(json.getJSONArray("exclusions"));
        this.warpRequisitionIds = jsonArrayToImmutableIntegerList(json.getJSONArray("warpRequisitionIds"));
    }

    private ImmutableList<Integer> jsonArrayToImmutableIntegerList(JSONArray json) throws JSONException{
        ImmutableList.Builder<Integer> builder = ImmutableList.builder();

        for(int i=0; i<json.length(); i++){
            builder.add(json.getInt(i));
        }

        return builder.build();
    }

    private ImmutableList<String> jsonArrayToImmutableList(JSONArray json) throws JSONException{
        ImmutableList.Builder<String> builder = ImmutableList.builder();

        for(int i=0; i<json.length(); i++){
            builder.add(json.getString(i));
        }

        return builder.build();
    }

    public void addUser(LeaderboardUser u){
        if(!this.users.contains(u)) {
            this.users.add(u);
            u.addPersona(this);
            Integer countMetro = metroBreakdown.get(u.getMetro().getMetroName());
            metroBreakdown.put(u.getMetro().getMetroName(), countMetro == null ? 1 : ++countMetro);
        }
    }

    public void calculateStats(){
        for(LeaderboardUser u : this.users){
            if(u.getRevenue() > 0){
                this.revenue += (u.getRevenue() / u.getPersonas().size());
                this.totalPaid++;
            }
        }

        this.arppu = this.totalPaid > 0 ? this.revenue / this.totalPaid : 0;
        this.arpu = this.users.size() > 0 ? this.revenue / this.users.size() : 0;
        this.ltv = this.arpu * LTV_MULTIPLIER;
    }

    public boolean isUserInPersona(LeaderboardUser u){
        boolean isInPersona = this.warpRequisitionIds.contains(u.getWarpRequisitionId());
        isInPersona = isInPersona ? isInPersona : search(this, u.getSpecialties(), this.specialties, u);
        isInPersona = isInPersona ? isInPersona : search(this, u.getSpecialtiesCaseSensitive(), this.caseSensitiveSpecialties, u);
        isInPersona = isInPersona ? isInPersona : search(this, u.getIndustries(), this.industries, u);
        isInPersona = isInPersona ? isInPersona : search(this, u.getSkills(), this.skills, u);
        return isInPersona;
    }

    public static boolean search(Persona p, ImmutableList<String> specialties, ImmutableList<String> data, LeaderboardUser u){
        for(String keyword : data){
            for(String s : specialties) {
                if(s.contains(keyword)){
                    for(String exclusion : p.getExclusions()){
                        if(s.contains(exclusion)){
                            //ignore
                            continue;
                        }
                    }

                    //in persona
                    return true;
                }
            }
        }

        return false;
    }

    public HashMap<String, HashSet<String>> getMetroUsers(){
        return metroUsers;
    }

    public int getTotalPaid() {
        return totalPaid;
    }

    public int getTotal() {
        return users.size();
    }

    public HashSet<LeaderboardUser> getUsers() {
        return users;
    }

    public String getName() {
        return name;
    }

    public int getTarget() {
        return target;
    }

    public double getRevenue() {
        return revenue;
    }

    public ImmutableList<String> getSpecialties() {
        return specialties;
    }

    public ImmutableList<String> getCaseSensitiveSpecialties() {
        return caseSensitiveSpecialties;
    }

    public ImmutableList<String> getSkills() {
        return skills;
    }

    public ImmutableList<String> getIndustries() {
        return industries;
    }

    public ImmutableList<String> getExclusions() {
        return exclusions;
    }

    public double getArpu() { return arpu; }

    public double getArppu() {
        return arppu;
    }

    public double getLtv() {
        return ltv;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("ARPPU", this.arppu);
        json.put("ARPU", this.arpu);
        json.put("LTV", this.ltv);
        json.put("target", this.target);
        json.put("total", this.users.size());
        json.put("totalPaid", this.totalPaid);
        json.put("revenue", this.revenue);
        JSONObject metros = new JSONObject();
        for(String metro : metroBreakdown.keySet()){
            JSONObject temp = new JSONObject();
            temp.put("count", metroBreakdown.get(metro));
            metros.put(metro, temp);
        }

        json.put("metros", metros);
        return json;

    }
}
