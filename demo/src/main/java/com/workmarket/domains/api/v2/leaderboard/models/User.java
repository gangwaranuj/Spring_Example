package com.workmarket.domains.api.v2.leaderboard.models;

import com.google.common.collect.ImmutableList;
import com.workmarket.screening.model.Screening;

import java.util.ArrayList;

/**
 * Created by bruno on 5/31/16.
 */
public class User {
    private int id;
    private MetroArea metro;
    private double revenue = 0.0;
    private int numAssignments;
    private int warpRequisitionId;
    private ImmutableList<String> skills;
    private ImmutableList<String> specialties;
    private ImmutableList<String> industries;
    private ImmutableList<String> skillsCaseSensitive;
    private ImmutableList<String> specialtiesCaseSensitive;
    private ImmutableList<String> industriesCaseSensitive;
    //this cannot be immutable
    private ArrayList<Persona> personas = new ArrayList<Persona>();
    public static final Double BACKGROUND_CHECK_REVENUE_PER_CHECK = 25.0;
    public static final Double DRUG_TEST_REVENUE_PER_CHECK = 35.0;

    public ArrayList<Persona> getPersonas() {
        return personas;
    }

    public User(int id, String city, String state, double revenue, int numAssignments,
                String skills, String specialties, String industries, int warpRequisitionId){
        this.id = id;
        this.metro = new MetroArea(city, state);
        this.revenue = revenue;
        this.numAssignments = numAssignments;
        this.skills = csvToImmutableList(skills, false);
        this.specialties = csvToImmutableList(specialties, false);
        this.industries = csvToImmutableList(industries, false);
        this.skillsCaseSensitive = csvToImmutableList(skills, true);
        this.specialtiesCaseSensitive = csvToImmutableList(specialties, true);
        this.industriesCaseSensitive = csvToImmutableList(industries, true);
        this.warpRequisitionId = warpRequisitionId;
    }


    private ImmutableList<String> csvToImmutableList(String s, boolean caseSensitive){
        return s == null ? ImmutableList.<String>of() :
                ImmutableList.<String>copyOf((caseSensitive ? s : s.toLowerCase() ).split(","));
    }

    public void addPersona(Persona p){
        personas.add(p);
    }

    public void addWorkerServicesRevenue(ArrayList<Screening> screenings) {
        if(screenings == null)
            return;

        for(Screening screening : screenings) {
            switch (screening.getVendorRequestCode()) {
                case BACKGROUND :
                    this.revenue += BACKGROUND_CHECK_REVENUE_PER_CHECK;
                    break;
                case DRUG:
                    this.revenue += DRUG_TEST_REVENUE_PER_CHECK;
                    break;
            }
        }
    }

    public int getId() {
        return id;
    }

    public int getWarpRequisitionId() { return warpRequisitionId; }

    public MetroArea getMetro() { return metro; }

    public double getRevenue() {
        return revenue;
    }

    public int getNumAssignments() {
        return numAssignments;
    }

    public ImmutableList<String> getSkills() {
        return skills;
    }

    public ImmutableList<String> getSpecialties() {
        return specialties;
    }

    public ImmutableList<String> getIndustries() {
        return industries;
    }

    public ImmutableList<String> getSkillsCaseSensitive() {
        return skillsCaseSensitive;
    }

    public ImmutableList<String> getSpecialtiesCaseSensitive() {
        return specialtiesCaseSensitive;
    }

    public ImmutableList<String> getIndustriesCaseSensitive() {
        return industriesCaseSensitive;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
