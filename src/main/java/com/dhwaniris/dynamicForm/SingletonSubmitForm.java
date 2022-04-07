package com.dhwaniris.dynamicForm;

import androidx.core.util.Pair;

import com.dhwaniris.dynamicForm.db.dbhelper.DynamicAnswerOption;
import com.dhwaniris.dynamicForm.db.dbhelper.form.Form;

import org.json.JSONObject;

import java.util.List;

import io.reactivex.Single;

/**
 * Created by ${Sahjad} on 7/10/2019.
 */
public class SingletonSubmitForm {

    private static SingletonSubmitForm instance;

    private SingletonSubmitForm() {
    }

    public static SingletonSubmitForm getInstance() {
        if (instance == null) {
            instance = new SingletonSubmitForm();
        }
        return instance;
    }
    public void clear(){
        form = null;
        dynamicAnswerOptions = null;
        jsonObject = null;
        instance = null;
    }

    public static SingletonSubmitForm createNew() {
        instance = new SingletonSubmitForm();
        return instance;
    }
    public static SingletonSubmitForm createNew(Form form) {
        return createNew(form,new JSONObject());
    }
    public static SingletonSubmitForm createNew(Form form,JSONObject jsonObject) {
        instance = new SingletonSubmitForm();
        instance.setForm(form);
        instance.setJsonObject(jsonObject);
        return instance;
    }


    private static Form form;
    private static List<DynamicAnswerOption> dynamicAnswerOptions;
    private static JSONObject jsonObject;
    private static Single<Pair<Boolean,String>> workOnSubmit;
    private static int uploadStatus;

    public Single<Pair<Boolean,String>> getWorkOnSubmit() {
        return workOnSubmit;
    }

    public void setWorkOnSubmit(Single<Pair<Boolean,String>> workOnSubmit) {
        SingletonSubmitForm.workOnSubmit = workOnSubmit;
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        SingletonSubmitForm.form = form;
    }

    public List<DynamicAnswerOption> getDynamicAnswerOptions() {
        return dynamicAnswerOptions;
    }

    public void setDynamicAnswerOptions(List<DynamicAnswerOption> dynamicAnswerOptions) {
        SingletonSubmitForm.dynamicAnswerOptions = dynamicAnswerOptions;
    }

    public JSONObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JSONObject jsonObject) {
        SingletonSubmitForm.jsonObject = jsonObject;
    }

    public int getUploadStatus () {
        return uploadStatus;
    }

    public void setUploadStatus (int uploadStatus) {
        SingletonSubmitForm.uploadStatus = uploadStatus;
    }
}
