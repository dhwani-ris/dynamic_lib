package com.dhwaniris.dynamicForm.utils;

import android.app.Activity;
import android.os.Environment;
import android.text.TextUtils;

import com.dhwaniris.dynamicForm.BuildConfig;

import java.io.File;
import java.io.IOException;

import android.util.Log;


public class Utility {


    public static boolean isUpdateNeeded(String minVersionForForm) {
        /*if (!TextUtils.isEmpty(minVersionForForm)) {
            int minVersion = Integer.parseInt(minVersionForForm.replace(".", "")
                    .replace("(Alfa)", "").replace("(Beta)", "")
                    .replace(" ", ""));
            int myCurrentVersion = Integer.parseInt(BuildConfig.VERSION_NAME.replace(".", "")
                    .replace("(Beta)", "").replace("(Alfa)", "")
                    .replace(" ", ""));
            if (myCurrentVersion >= minVersion) {
                return false;
            }
        }*/
        return false;
//        return true;
    }


    /*public static boolean ValidateForm(List<QuestionBean> questionBeanList) {
        boolean isValid = true;
        int previousOder = 0;
        for (QuestionBean questionBean : questionBeanList) {
            int order = Integer.parseInt(questionBean.getOrder());
            if (order - 1 != previousOder) {
                Log.e("FormValidation", String.format(" value of missing question = %d", (previousOder + 1)));
                isValid = false;
                break;

            } else {
                for (ChildBean childBean : questionBean.getChild()) {
                    if (Integer.parseInt(childBean.getOrder()) <= order) {
                        Log.e("FormValidation", String.format(" child has wrong value question = %d", order));
                        isValid = false;
                        break;
                    }
                }
                if (!isValid)
                    break;

                for (ParentBean parentBean : questionBean.getParent()) {
                    if (Integer.parseInt(parentBean.getOrder()) >= order) {
                        Log.e("FormValidation", String.format(" parent has wrong value question = %d", order));
                        isValid = false;
                        break;
                    }
                }
                if (!isValid)
                    break;
            }
            previousOder = order;
        }
        return isValid;
    }*/

    //Testing Purpose Only
  /*  public static boolean ValidateFormFindAll(List<QuestionBean> questionBeanList, String fid) {
        boolean isValid = true;
        int previousOder = 0;
        List<String> errorsList = new ArrayList<>();
        for (QuestionBean questionBean : questionBeanList) {
            int order = Integer.parseInt(questionBean.getOrder());
            if (order - 1 != previousOder) {
                Log.e("FormValidation Logical", String.format("order = %d ,value of missing question", (previousOder + 1)));
                errorsList.add(String.format("order = %d ,value of missing question", (previousOder + 1)));
                isValid = false;

            }

            for (ChildBean childBean : questionBean.getChild()) {
                if (Integer.parseInt(childBean.getOrder()) <= order) {
                    Log.e("FormValidation Logical", String.format("order = %d, child has wrong value," +
                            " Child order value = %s", order, childBean.getOrder()));
                    errorsList.add(String.format("order = %d, child has wrong value," +
                            " Child order value = %s", order, childBean.getOrder()));
                    isValid = false;
                }

                if (Integer.parseInt(childBean.getOrder()) > questionBeanList.size()) {
                    Log.e("FormValidation Logical", String.format("order = %d, child has lager value than total question value," +
                            " Child order value = %s", order, childBean.getOrder()));
                    errorsList.add(String.format("order = %d, child has lager value than total question value," +
                            " Child order value = %s", order, childBean.getOrder()));
                    isValid = false;

                }

                QuestionBean childQuestion = questionBeanList.get(Integer.parseInt(childBean.getOrder()) - 1);
                if (childQuestion != null) {
                    boolean ischildhasParent = false;
                    for (ParentBean childParent : childQuestion.getParent()) {
                        if (Integer.parseInt(childParent.getOrder()) == order) {
                            ischildhasParent = true;
                        }
                    }
                    if (!ischildhasParent) {
                        Log.e("FormValidation Logical", String.format("order = %d, question has child but child has no parent," +
                                " Child order value = %s", order, childBean.getOrder()));
                        errorsList.add(String.format("order = %d, question has child but child has no parent," +
                                " Child order value = %s", order, childBean.getOrder()));
                        isValid = false;
                    }


                }

            }

            for (ParentBean parentBean : questionBean.getParent()) {
                if (Integer.parseInt(parentBean.getOrder()) >= order) {
                    Log.e("FormValidation Logical", String.format("order = %d, " +
                            "parent has wrong value, parent order value = %s", order, parentBean.getOrder()));
                    errorsList.add(String.format("order = %d, " +
                            "parent has wrong value, parent order value = %s", order, parentBean.getOrder()));
                    isValid = false;
                }

                if (Integer.parseInt(parentBean.getOrder()) > questionBeanList.size()) {
                    Log.e("FormValidation Logical", String.format("order = %d, parant has lager value than total question value," +
                            " Child order value = %s", order, parentBean.getOrder()));
                    errorsList.add(String.format("order = %d, parant has lager value than total question value," +
                            " Child order value = %s", order, parentBean.getOrder()));
                    isValid = false;

                }


                QuestionBean parentQuestion = questionBeanList.get(Integer.parseInt(parentBean.getOrder()) - 1);
                if (parentQuestion != null) {
                    boolean isParentHasChild = false;
                    for (ChildBean childBean : parentQuestion.getChild()) {
                        if (Integer.parseInt(childBean.getOrder()) == order) {
                            isParentHasChild = true;
                        }
                    }
                    if (!isParentHasChild) {
                        Log.e("FormValidation Logical", String.format("order = %d, question has parent but parent has no child," +
                                " parent order value = %s", order, parentBean.getOrder()));
                        errorsList.add(String.format("order = %d, question has parent but parent has no child," +
                                " parent order value = %s", order, parentBean.getOrder()));
                        isValid = false;
                    }


                }

            }
///valid but testin issue
            if (questionBean.getPattern() != null && !questionBean.getPattern().equals("")) {
                boolean isRegexValidation = false;
                for (ValidationBean validationBean : questionBean.getValidation()) {
                    if (validationBean.get_id().equals(LibDynamicAppConfig.VAL_REGEX))
                        isRegexValidation = true;
                }
                if (!isRegexValidation)
                    Log.e("FormValidation Testing", String.format("order = %d ,has regex " +
                            "but validation is empty", order));
                errorsList.add(String.format("order = %d ,has regex " +
                        "but validation is empty", order));
            }
            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_NUMBER)) {
                if (questionBean.getMax() == null) {
                    Log.e("FormValidation Testing", String.format(" order = %d, Input Type Number" +
                            " but has no Max value", order));
                    errorsList.add(String.format(" order = %d, Input Type Number" +
                            " but has no Max value", order));
                } else if (questionBean.getMax().equals("")) {
                    Log.e("FormValidation Testing", String.format("order = %d, Input Type Number" +
                            " but has no Max value", order));
                    errorsList.add(String.format("order = %d, Input Type Number" +
                            " but has no Max value", order));
                }

                if (questionBean.getMin() == null) {
                    Log.e("FormValidation Testing", String.format("order = %d, Input Type Number" +
                            " but has no Min value", order));
                    errorsList.add(String.format("order = %d, Input Type Number" +
                            " but has no Min value", order));
                } else if (questionBean.getMin().equals("")) {
                    Log.e("FormValidation Testing", String.format("order = %d, Input Type  Number" +
                            " but has no Min value", order));
                    errorsList.add(String.format("order = %d, Input Type  Number" +
                            " but has no Min value", order));
                }


            }

            if (questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_DROPDOWN) ||
                    questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT) ||
                    questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED) ||
                    questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_DROPDOWN_HIDE) ||
                    questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE)
                    ) {

                if (questionBean.getAnswer_options().size() == 0) {
                    Log.e("FormValidation Testing", String.format("order = %d, Input Type  DropDown or Multi" +
                            " but has no Answer option", order));
                    errorsList.add(String.format("order = %d, Input Type  DropDown or Multi" +
                            " but has no Answer option", order));
                }
            }


            if (questionBean.getAnswer_options().size() > 0) {

                if (!questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_DROPDOWN) &&
                        !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT) &&
                        !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT_LIMITED) &&
                        !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_DROPDOWN_HIDE) &&
                        !questionBean.getInput_type().equals(LibDynamicAppConfig.QUS_MULTI_SELECT_HIDE)
                        ) {

                    Log.e("FormValidation Testing", String.format("order = %d, has Answer Option " +
                            " but diffrent Input Type", order));
                    errorsList.add(String.format("order = %d, has Answer Option " +
                            " but diffrent Input Type", order));


                }

            }

            if (questionBean.getValidation().size() == 0) {

                Log.e("FormValidation Testing", String.format("order = %d, has no validation " +
                        " ", order));
                errorsList.add(String.format("order = %d, has no validation " +
                        " ", order));

            }


            /////


            previousOder = order;
        }

        try {
            writeTxtStream(errorsList, fid);
        } catch (IOException e) {
             Log.e("error",e.getMessage());;
        }
        return isValid;
    }

  */
    public static void runAnimatedLoadingDots(Activity activity, String messagePrefix, final androidx.appcompat.app.AlertDialog alertDialog) {

        int dotsCount = 0;
        while (alertDialog != null && alertDialog.isShowing()) {

            dotsCount++;
            dotsCount = dotsCount % 5; // looks good w/4 dots

            try {
                Thread.sleep(500);
            } catch (InterruptedException iEx) {
                Thread.currentThread().interrupt();
            }

            final StringBuffer updateValue = new StringBuffer(messagePrefix);
            for (int i = 0; i < dotsCount; i++) {
                updateValue.append('.');
            }

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    alertDialog.setMessage(updateValue.toString());
                }
            });
        }

    }

    public static File getFile(String fid) {

        File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DeltaError");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "logs_" + fid + ".txt");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
        } catch (IOException e) {
             Log.e("error",e.getMessage());;
        }
        return file;
    }

}
