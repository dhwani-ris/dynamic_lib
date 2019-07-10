package com.dhwaniris.dynamicForm.db.dbhelper.form;

import com.dhwaniris.dynamicForm.db.dbhelper.ResourceUrlsBean;

import java.util.List;

public class QuestionBean  implements Cloneable, Comparable<QuestionBean> {


    private String _id;

    private String order;

    private String viewSequence;

    private String title;

    private String input_type;

    private String hint;

    private String pattern;

    private String min;

    private String max;

    private String constant;

    private String label;

    private String information;

    private boolean editable;

    private List<ChildBean> child;

    private List<ParentBean> parent;

    private List<ValidationBean> validation;

    private List<AnswerOptionsBean> answer_option;

    private List<Answers> answers;

    private List<RestrictionsBean> restrictions;

    private List<ResourceUrlsBean> resource_urls;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getViewSequence() {
        return viewSequence;
    }

    public void setViewSequence(String viewSequence) {
        this.viewSequence = viewSequence;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getMin()
    {
        if (min == null) {
            min = "";
        }
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        if (max == null) {
            max = "";
        }
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

    public String getConstant() {
        return constant;
    }

    public void setConstant(String constant) {
        this.constant = constant;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public List<ChildBean> getChild() {
        return child;
    }

    public void setChild(List<ChildBean> child) {
        this.child = child;
    }

    public List<ParentBean> getParent() {
        return parent;
    }

    public void setParent(List<ParentBean> parent) {
        this.parent = parent;
    }

    public List<AnswerOptionsBean> getAnswer_option() {
        return answer_option;
    }

    public void setAnswer_option(List<AnswerOptionsBean> answer_option) {
        this.answer_option = answer_option;
    }

    public List<ValidationBean> getValidation() {
        return validation;
    }

    public void setValidation(List<ValidationBean> validation) {
        this.validation = validation;
    }

    public List<AnswerOptionsBean> getAnswer_options() {
        return answer_option;
    }

    public void setAnswer_options(List<AnswerOptionsBean> answer_options) {
        this.answer_option = answer_options;
    }

    public List<Answers> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answers> answers) {
        this.answers = answers;
    }

    public List<RestrictionsBean> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<RestrictionsBean> restrictions) {
        this.restrictions = restrictions;
    }

    public List<ResourceUrlsBean> getResource_urls() {
        return resource_urls;
    }

    public void setResource_urls(List<ResourceUrlsBean> resource_urls) {
        this.resource_urls = resource_urls;
    }

    @Override
    public int compareTo(QuestionBean o) {
        int currentOrder = Integer.parseInt(viewSequence);
        int secondOrder = Integer.parseInt(o.getViewSequence());
        if (secondOrder < currentOrder) {
            return -1;
        } else if (secondOrder > currentOrder) {
            return 1;
        }
        return 0;
    }
}