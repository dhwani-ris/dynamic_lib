package com.dhwaniris.dynamicForm.db;




import java.util.List;

public class VillageWiseFormCount  {




    private int formId;

    private int count;

    private List<VillageWiseBean> villageWise;

    public List<VillageWiseBean> getVillageWise() {
        return villageWise;
    }

    public void setVillageWise(List<VillageWiseBean> villageWise) {
        this.villageWise = villageWise;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }
}
