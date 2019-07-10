package com.dhwaniris.dynamicForm.db.dbhelper;
public class MasterStateBean  {


    /**
     * _id : 5b52ebc26e732c0e5e6b881f
     * code : S21
     * languageCode : or
     * name : Odisha
     * regionalName : ଓଡିଶା
     * isActive : true
     */


    private String uniqueId;
    private String projectId;



    private String id;

    private String name;

    private String regionalName;


    private boolean isDistrictSynced;

    public boolean isDistrictSynced() {
        return isDistrictSynced;
    }

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

    public void setDistrictSynced(boolean districtSynced) {
        isDistrictSynced = districtSynced;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
