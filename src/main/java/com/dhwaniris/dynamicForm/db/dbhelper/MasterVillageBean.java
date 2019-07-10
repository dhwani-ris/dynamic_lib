package com.dhwaniris.dynamicForm.db.dbhelper;

public class MasterVillageBean  {


    /**
     * _id : 5b52ec056e732c0e5e6b8a06
     * code : 400559
     * block : 5b52ebe06e732c0e5e6b8893
     * district : 5b52ebd36e732c0e5e6b885b
     * household : 0
     * name : NADIA BHANGA
     * state : 5b52ebc26e732c0e5e6b881f
     * gramPanchayat : 5b52ebf56e732c0e5e6b8900
     * regionalName : ନଦିଆ ଭଙ୍ଗ
     */


    private String uniqueId;
    private String projectId;



    private String id;

    private String block;

    private String name;

    private String regionalName;



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


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
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
