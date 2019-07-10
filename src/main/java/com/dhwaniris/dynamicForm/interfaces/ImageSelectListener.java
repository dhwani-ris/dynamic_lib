package com.dhwaniris.dynamicForm.interfaces;

import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;

public interface ImageSelectListener {
    void imagePath(String path, QuestionBean questionBean);

    void imageSelectionFailure();
}
