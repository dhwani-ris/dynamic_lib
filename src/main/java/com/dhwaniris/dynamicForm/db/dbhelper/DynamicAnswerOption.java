package com.dhwaniris.dynamicForm.db.dbhelper;

import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;

import java.util.List;


/**
 * Created by ${Sahjad} on 6/13/2019.
 */
public class DynamicAnswerOption  {



    private String uniqueId;

    private int formId;

    private String order;

    private List<AnswerOptionsBean> answer_option;


    public void makeUnique() {
        uniqueId = formId + "_" + order;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getFormId() {
        return formId;
    }

    public void setFormId(int formId) {
        this.formId = formId;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public List<AnswerOptionsBean> getAnswer_option() {
        return answer_option;
    }

    public void setAnswer_option(List<AnswerOptionsBean> answer_option) {
        this.answer_option = answer_option;
    }


}