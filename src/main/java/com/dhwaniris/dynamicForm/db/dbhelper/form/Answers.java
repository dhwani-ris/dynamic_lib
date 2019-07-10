package com.dhwaniris.dynamicForm.db.dbhelper.form;
public class Answers  {

    private String value;
    private String label;
    private String textValue;
    private String reference;

    public String getTextValue() {
        if (textValue == null) {
            return "";
        }
        return textValue;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    public String getValue() {
        if (value == null) {
            return "";
        }
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        if (label == null) {
            return "";
        }
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
