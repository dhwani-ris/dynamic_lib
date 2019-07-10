package com.dhwaniris.dynamicForm.db.dbhelper;

public class MasterBlockBean  {


    /**
     * _id : 5b52ebe06e732c0e5e6b8893
     * code : S213820137
     * district : 5b52ebd36e732c0e5e6b885b
     * name : DANAGADI
     * state : 5b52ebc26e732c0e5e6b881f
     * isActive : true
     * regionalName : ଦାନଗଦି
     */


    private String uniqueId;
    private String projectId;


    private String id;

    private String district;

    private String name;

    private String regionalName;

    private boolean isVillageSynced;


    public void makeUniuqe() {
        this.uniqueId = id + "-" + projectId;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public boolean getIsVillageSynced() {
        return isVillageSynced;
    }

    public void setIsVillageSynced(boolean isVillageSynced) {
        this.isVillageSynced = isVillageSynced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegionalName() {
        return regionalName;
    }

    public void setRegionalName(String regionalName) {
        this.regionalName = regionalName;
    }
}
