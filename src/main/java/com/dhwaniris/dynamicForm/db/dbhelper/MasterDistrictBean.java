package com.dhwaniris.dynamicForm.db.dbhelper;

public class MasterDistrictBean  {


    /**
     * _id : 5b52ebd36e732c0e5e6b885b
     * code : S21382
     * name : Jajapur
     * state : 5b52ebc26e732c0e5e6b881f
     * isActive : true
     * regionalName : ଯାଜପୁର
     */


    private String uniqueId;
    private String projectId;



    private String id;

    private String name;

    private String state;

    private String regionalName;

    private boolean isBlocksSynced;


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

    public boolean isBlocksSynced() {
        return isBlocksSynced;
    }

    public void setBlocksSynced(boolean blocksSynced) {
        isBlocksSynced = blocksSynced;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegionalName() {
        return regionalName;
    }

    public void setRegionalName(String regionalName) {
        this.regionalName = regionalName;
    }
}
