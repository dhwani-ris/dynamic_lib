package com.dhwaniris.dynamicForm;

import com.dhwaniris.dynamicForm.db.dbhelper.DynamicAnswerOption;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ${Sahjad} on 7/10/2019.
 */
public class SingletonForm {

    private static SingletonForm instance;

    private SingletonForm() {
    }

    public static SingletonForm getInstance() {
        if (instance == null) {
            instance = new SingletonForm();
        }
        return instance;
    }
    public void clear(){
        form = null;
        dynamicAnswerOptions = null;
        jsonObject = null;
        instance = null;
    }

    public static SingletonForm createNew() {
        instance = new SingletonForm();
        return instance;
    }


    private static Form form;
    private static List<DynamicAnswerOption> dynamicAnswerOptions;
    private static JSONObject jsonObject;

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        SingletonForm.form = form;
    }

    public List<DynamicAnswerOption> getDynamicAnswerOptions() {
        return dynamicAnswerOptions;
    }

    public void setDynamicAnswerOptions(List<DynamicAnswerOption> dynamicAnswerOptions) {
        SingletonForm.dynamicAnswerOptions = dynamicAnswerOptions;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        SingletonForm.jsonObject = jsonObject;
    }
}
