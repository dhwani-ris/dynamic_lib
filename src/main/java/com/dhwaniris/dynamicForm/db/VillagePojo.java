package com.dhwaniris.dynamicForm.db;




public class VillagePojo  {


    /**
     * _id : 59df74ad8d92252f00a6cfab
     * name : MH PN B1 Village1
     * code : MHPNB1V1
     * block : 59df73d08d92252f00a6cfaa
     * district : 59df73538d92252f00a6cfa4
     * state : 59df73338d92252f00a6cfa3
     */


    private String _id;

    private String name;

    private String code;

    private String block;

    private String district;

    private String state;

    private String regionalName;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getRegionalName() {
        if (regionalName == null) {
            regionalName = "";
        }
        return regionalName;
    }

    public void setRegionalName(String regionalName) {
        this.regionalName = regionalName;
    }
}
