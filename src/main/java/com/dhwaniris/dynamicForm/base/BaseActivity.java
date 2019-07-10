package com.dhwaniris.dynamicForm.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dhwaniris.dynamicForm.NetworkModule.AppConfing;
import com.dhwaniris.dynamicForm.R;
import com.dhwaniris.dynamicForm.adapters.LocalSingleAdapter;
import com.dhwaniris.dynamicForm.adapters.MultiSelectorAdapter;
import com.dhwaniris.dynamicForm.adapters.SingleSelectAdapter;
import com.dhwaniris.dynamicForm.customViews.CustomDatePicker;
import com.dhwaniris.dynamicForm.db.dbhelper.form.AnswerOptionsBean;
import com.dhwaniris.dynamicForm.db.dbhelper.form.QuestionBean;
import com.dhwaniris.dynamicForm.interfaces.ImageSelectListener;
import com.dhwaniris.dynamicForm.interfaces.PermissionListener;
import com.dhwaniris.dynamicForm.interfaces.ProfileFormListener;
import com.dhwaniris.dynamicForm.interfaces.SelectListener;
import com.dhwaniris.dynamicForm.locationservice.LocationUpdatesService;
import com.dhwaniris.dynamicForm.pojo.DropDownHelper;
import com.dhwaniris.dynamicForm.ui.activities.formActivities.FormActivity;
import com.dhwaniris.dynamicForm.utils.FileUtil;
import com.dhwaniris.dynamicForm.utils.PreferenceHelper;
import com.dhwaniris.dynamicForm.utils.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static androidx.core.content.FileProvider.getUriForFile;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.DIVISSION_BY_ZERO;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.FILE_NOT_FOUND_EXCEPTION_ERROR;
import static com.dhwaniris.dynamicForm.NetworkModule.AppConfing.OUT_OF_MEMORY_EXCEPTION_ERROR;


public class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    public boolean saved;
    private static File filename;
    protected PermissionListener permissionListener;
    private static String user_name;
    private static String appVersion;
    public boolean isGPSRequested = true;

    public static final String TAG = FormActivity.class.getSimpleName();

    public final int REQUEST_PERMISSIONS_FOR_GPS = 305;
    public final int REQUEST_PERMISSIONS_FOR_IMAGE = 306;
    public final int REQUEST_PERMISSIONS_FOR_RECEIVE_SMS = 307;
    public final int REQUEST_PERMISSIONS_FOR_GPS_FILE = 308;
    public final int REQUEST_PERMISSIONS_FOR_RECORD_AUDIO = 309;

    public final int NESTEDCHILD_CODE = 22;
    public final int ADDITIONAL_FORM_CODE = 23;

    public final int PICK_IMAGE_REQUEST = 1;
    public final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public final long UPDATE_INTERVAL_IN_MILLISECONDS = 600000;
    public int REQUEST_LOCATION_ISLOGIN = 999;
    public int REQUEST_LOCATION = 998;

    public String timeStamp;
    public static int rotation;
    public static String errorMessgae;
    private ImageSelectListener selectedImage;
    private QuestionBean questionBean;
    public static final String APP_DIRECTORY_NAME = ".Delta";
    public static final String DATA_DIRECTORY_NAME = "formData";


    public String realPath;
    public ProgressDialog mProgressDialog;
    public AlertDialog mProgressDialog2;
    public static PreferenceHelper preferenceHelper;
    public GoogleApiClient mGoogleApiClient;
    // private String filePath;
    private Uri picUri;


    ///-------------------loc-----
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    public LocationUpdatesService mService = null;
    public boolean mBound = false;
    public final int NONE = 0;
    public final int ANSWERED = 1;
    public final int NOT_ANSWERED = 2;
    // Monitors the state of the connection to the service.




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferenceHelper = new PreferenceHelper(this, AppConfing.DELTA_PREF_NAME);
        preferenceHelper.SaveStringPref(AppConfing.APP_VERSION, getAppVersion());
    }


    public String getAppVersion() {

        PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            return "1.0.0";
        }
        return pInfo.versionName;
    }


    public static void showMessage(String msg) {

        System.out.println("BaseActivity" + msg);
    }

    //show toast from String
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //show toast from String Resource
    public void showToast(int string_id) {
        showToast(getString(string_id));
    }

    //creating dropdown selector
    public void createSelector(SelectListener formListener, List<AnswerOptionsBean> answerOptionsBeen,
                               QuestionBean questionBean, String header) {

        List<AnswerOptionsBean> tempList = new ArrayList<>();
        tempList.addAll(answerOptionsBeen);

        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);
        AppCompatTextView noOptionFound = view.findViewById(R.id.no_option_found);
        final EditText edtSearch = view.findViewById(R.id.edt_search);
        final Button btnClear = view.findViewById(R.id.btn_clear);
        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
        dialog.setView(view);
        if (answerOptionsBeen.size() > 8) {
            edtSearch.setVisibility(View.VISIBLE);
            String searchText = getString(R.string.search_txt) + "  (" + answerOptionsBeen.size() + ")";
            edtSearch.setHint(searchText);
        } else if (answerOptionsBeen.size() == 0) {
            noOptionFound.setVisibility(View.VISIBLE);
        }
        title.setText(header);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final android.app.AlertDialog alertDialog = dialog.create();
        final SingleSelectAdapter adapterS = new SingleSelectAdapter(formListener, tempList,
                questionBean, alertDialog);
        recyclerView.setAdapter(adapterS);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapterS != null) {
                    adapterS.search(charSequence.toString());
                }
                if (!charSequence.toString().equals("")) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });
        alertDialog.show();
    }

    //creating dropdown selector from local array
    public void createSelectorProFile(ProfileFormListener formListener, List<DropDownHelper> answerOptionsBeen,
                                      int header, String label) {
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);
        EditText edtSearch = view.findViewById(R.id.edt_search);
        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
        dialog.setView(view);

      /*  if (answerOptionsBeen.size() > 8) {
            edtSearch.setVisibility(View.VISIBLE);
        }
*/
        title.setText(getResources().getString(header));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final android.app.AlertDialog alertDialog = dialog.create();
        LocalSingleAdapter adapter = new LocalSingleAdapter(formListener, answerOptionsBeen,
                alertDialog, label);
        recyclerView.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //custom view multi selector
    public void showMultiSelector(List<AnswerOptionsBean> ansOpt, final List<String> selectedList,
                                  final SelectListener formListener, final QuestionBean questionBean,
                                  String header) {

        final String[] sArray = new String[ansOpt.size()];
        final String[] ids = new String[ansOpt.size()];

        int i = -1;
        for (AnswerOptionsBean bean : ansOpt) {
            i++;
            sArray[i] = bean.getName();
            ids[i] = bean.get_id();
        }

        final boolean[] booleenList = new boolean[sArray.length];
        List<String> tempList = Arrays.asList(ids);

        boolean isValidSelectedList = true;

        for (String sls : selectedList) {
            if (!tempList.contains(sls))
                isValidSelectedList = false;
        }


        if (selectedList.size() > 0 && isValidSelectedList) {
            for (int j = 0; j < selectedList.size(); j++) {
                int pos = tempList.indexOf(selectedList.get(j));
                booleenList[pos] = true;
            }
        } else if (isValidSelectedList) {
            BaseActivity.logDatabase(AppConfing.END_POINT, "No value in multi-select, Question ID: "
                    + questionBean.get_id(), AppConfing.UNEXPECTED_ERROR, "BaseActivity");
        }

        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);

        dialog.setCustomTitle(view);
        dialog.setMultiChoiceItems(sArray, booleenList, null);
        title.setText(header);
        dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView listView = ((android.app.AlertDialog) dialog).getListView();
                StringBuilder stringBuilder = new StringBuilder();
                selectedList.clear();
                for (int i = 0; i < listView.getCount(); i++) {
                    boolean isChecked = listView.isItemChecked(i);
                    if (isChecked) {
                        String value = ids[i];
                        selectedList.add(value);
                        if (stringBuilder.length() > 0) {
                            stringBuilder.append(",");
                        }
                        stringBuilder.append(sArray[i]);
                    }
                }
                if (stringBuilder.toString().trim().equals("")) {
                    stringBuilder.setLength(0);
                    formListener.MultiSelector(questionBean, selectedList, "");
                } else {
                    formListener.MultiSelector(questionBean, selectedList, stringBuilder.toString());
                }
            }
        });
        dialog.setNegativeButton(R.string.clear, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dia, int which) {
                selectedList.clear();
                formListener.MultiSelector(questionBean, selectedList, "");
                dia.dismiss();
            }
        });
        final android.app.AlertDialog alertDialog = dialog.create();
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    //custom view multi selector with Limeted check
    public void showMultiSelectorWithLimit(final List<AnswerOptionsBean> ansOpt, final List<String> selectedList,
                                           final SelectListener formListener, final QuestionBean questionBean,
                                           String header, int checkLimit) {

        List<String> tempSelectedList = new ArrayList<>();
        tempSelectedList.addAll(selectedList);
        for (String select : selectedList) {
            boolean isInCurrentList = false;
            for (AnswerOptionsBean answerOptionsBean : ansOpt) {
                if (answerOptionsBean.get_id().equals(select)) {
                    isInCurrentList = true;
                    break;
                }
            }
            if (!isInCurrentList)
                tempSelectedList.remove(select);
        }
        final List<String> mycheckList = tempSelectedList;

        List<AnswerOptionsBean> tempList = new ArrayList<>();
        tempList.addAll(ansOpt);
        final android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dynamic_custom_selector_layout, null);
        TextView title = view.findViewById(R.id.dTitle);
        ImageView close = view.findViewById(R.id.dClose);
        TextView noOptionFound = view.findViewById(R.id.no_option_found);
        final EditText edtSearch = view.findViewById(R.id.edt_search);
        final Button btnClear = view.findViewById(R.id.btn_clear);
        RecyclerView recyclerView = view.findViewById(R.id.dRecycler);
        dialog.setView(view);
        if (ansOpt.size() > 8) {
            edtSearch.setVisibility(View.VISIBLE);
        }


        title.setText(header);
        final SelectListener innerListener = new SelectListener() {
            @Override
            public void MultiSelector(QuestionBean question, List<String> value, String text) {

            }

            @Override
            public void SingleSelector(QuestionBean question, String value, String id, boolean isSingle) {
                /// here m using id for add new item in checkList
                if (id != null) {
                    if (!mycheckList.contains(id))
                        mycheckList.add(id);
                }
                /// value for remove item from list if uncheckitem
                else if (value != null) {
                    if (mycheckList.contains(value)) {
                        mycheckList.remove(value);
                    } else if (value.equals("ClearAll")) {
                        mycheckList.clear();
                    }
                }
            }

            @Override
            public void DateSelector(String dd, String mm, String yy, QuestionBean question) {

            }
        };
        if (ansOpt.size() == 0) {
            noOptionFound.setVisibility(View.VISIBLE);
        } else {
            dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean isAnswerSame = false;
                    String prifix = "";
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(prifix);
                    /*for (AnswerOptionsBean optionsBean : ansOpt) {
                        if (mycheckList.contains(optionsBean.get_id())) {
                            stringBuilder.append(prifix);
                            stringBuilder.append(optionsBean.getName());
                            prifix = ", ";
                        }
                    }*/

                 /*   List<Integer> selectedListInt = sortCheckList(selectedList);
                    List<Integer> selectedMyCheckList = sortCheckList(mycheckList);
                 */   Collections.sort(selectedList);
                    Collections.sort(mycheckList);
                    if (selectedList.equals(mycheckList)) {
                        isAnswerSame = true;
                    }


                    final List<String> myFinalSelectedList = new ArrayList<>();
                    for (AnswerOptionsBean optionsBean : ansOpt) {
                        if (mycheckList.contains(optionsBean.get_id())) {
                            myFinalSelectedList.add(optionsBean.get_id());
                            stringBuilder.append(prifix);
                            stringBuilder.append(optionsBean.getName());
                            prifix = ", ";
                        }
                    }
                    if (!isAnswerSame) {
                        if (stringBuilder.toString().trim().equals("")) {
                            stringBuilder.setLength(0);
                            formListener.MultiSelector(questionBean, myFinalSelectedList, "");
                        } else {
                            formListener.MultiSelector(questionBean, myFinalSelectedList, stringBuilder.toString());
                        }
                    }

                }
            });
        }


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final android.app.AlertDialog alertDialog = dialog.create();


        final MultiSelectorAdapter adapter = new MultiSelectorAdapter(innerListener, tempList, checkLimit, tempSelectedList, questionBean);
        recyclerView.setAdapter(adapter);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null) {
                    adapter.search(charSequence.toString());
                }
                if (!charSequence.toString().equals("")) {
                    btnClear.setVisibility(View.VISIBLE);
                } else {
                    btnClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtSearch.setText("");
            }
        });

        alertDialog.show();

    }

    private List<Integer> sortCheckList(List<String> selectedList) {
        List<Integer> newIntList = new ArrayList<>();

        for (String value : selectedList) {
            try {
                newIntList.add(Integer.parseInt(value));
            } catch (Exception e) {
                Toast.makeText(this, "something went wrong in multiSelect", Toast.LENGTH_SHORT).show();
            }


        }

        return newIntList;
    }


    //Date picker
    public void dateChooser(final SelectListener dateListener, final QuestionBean questionBean, long max, long min) {

        CustomDatePicker datePicker = new CustomDatePicker();
        datePicker.setListener(dateListener, questionBean, 2100);
        datePicker.show(getSupportFragmentManager(), "CustomDatePicker");
    }

    //get date and time from UNIX TIMESTAMP
    public String getDateTime(long value) {
        Date date = new Date(value * 7500);
        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mma", Locale.ENGLISH);
        return df.format(date);
    }

    //getting current time of device
    public static String LocalTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    //show Loading
    public void showLoading() {
        hideLoading();
        mProgressDialog = showLoadingDialog(this);
    }

    //hide loading
    public void hideLoading() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.cancel();
        }
    }



    //loading helper
    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.dy_progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    //show Loading
    public void showProgress(final Context context, final String loadingtext) {
        mProgressDialog2 = getAleartDialogLoading(this, loadingtext + "  ");
        TextView messageText = mProgressDialog2.findViewById(android.R.id.message);
        LinearLayout.LayoutParams buttonLayoutParams
                = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.setMargins(45, 10, 0, 10);
        messageText.setLayoutParams(buttonLayoutParams);
        Thread thread = new Thread() {
            @Override
            public void run() {
                Utility.runAnimatedLoadingDots(
                        (Activity) context,
                        loadingtext
                        , mProgressDialog2);

            }
        };
        thread.start();
    }

    //hide loading
    public void hideProgess() {
        if (mProgressDialog2 != null && mProgressDialog2.isShowing()) {
            mProgressDialog2.dismiss();
        }
    }


    public static AlertDialog getAleartDialogLoading(Context context, String messagePrefix) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(messagePrefix);
        alertDialog.setCancelable(false);
        alertDialog.show();
        return alertDialog;
    }


    /******** User preference Started ********/

    //get user token
    public String userToken() {
        preferenceHelper = new PreferenceHelper(this, AppConfing.DELTA_PREF_NAME);
        return preferenceHelper.LoadStringPref(AppConfing.USER_TOKEN, "");
    }

    //get user name
    public String userName() {
        preferenceHelper = new PreferenceHelper(this, AppConfing.DELTA_PREF_NAME);
        return preferenceHelper.LoadStringPref(AppConfing.USER_NAME, "");
    }

    //get user's current local language
    public String userLanguage() {
        preferenceHelper = new PreferenceHelper(this, AppConfing.DELTA_PREF_NAME);
        return preferenceHelper.LoadStringPref(AppConfing.LANGUAGE, "en");
    }

    //get login status
    public boolean loginStatus() {
        preferenceHelper = new PreferenceHelper(this, AppConfing.DELTA_PREF_NAME);
        return preferenceHelper.LoadBooleanPref(AppConfing.LOGEDIN_STATUS, false);
    }

    /******** User preference Ended ********/

    //check network connection
    public static boolean checkNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        return false;
    }

    /******* Permissions Module Started ********/

    //check GPS permission
    public boolean checkGpsPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //check MIC permission
    public boolean checkMicPermission() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                RECORD_AUDIO);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    //ask MIC permission
    public void requestMicPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{RECORD_AUDIO}, REQUEST_PERMISSIONS_FOR_RECORD_AUDIO);
    }


    public boolean checkGpsFilePermission() {

        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION);
        int result3 = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }

    //ask location permission
    public void requestGpsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{ACCESS_FINE_LOCATION}, REQUEST_PERMISSIONS_FOR_GPS);
    }

    public void requestGpsFilePhoneStatePermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{WRITE_EXTERNAL_STORAGE, ACCESS_FINE_LOCATION, READ_PHONE_STATE},
                REQUEST_PERMISSIONS_FOR_GPS_FILE);
    }

    //check local storage and camera permission
    public boolean checkImagePermission() {
        int result = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
        int result2 = ContextCompat.checkSelfPermission(this, CAMERA);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    //check local storage and camera permission
    public boolean checkSMSPermission() {
        int result = ContextCompat.checkSelfPermission(this, READ_SMS);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    //Requesting write and camera permission
    protected void requestImagePermissions() {
        ActivityCompat.requestPermissions(BaseActivity.this, new String[]{
                WRITE_EXTERNAL_STORAGE,
                CAMERA
        }, REQUEST_PERMISSIONS_FOR_IMAGE);
    }

    //Requesting sms permission
    protected void requestSMSPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                READ_SMS
        }, REQUEST_PERMISSIONS_FOR_RECEIVE_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //permission handling for GPS
        if (permissionListener != null) {
            if (requestCode == REQUEST_PERMISSIONS_FOR_GPS) {
                if (grantResults.length > 0) {
                    //Permission accepted
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionListener.acceptedPermission();
                    } else {
                        permissionListener.deniedPermission();
                    }
                } else {
                    // Permission denied.
                    permissionListener.deniedPermission();
                }

                //permission handling for Image
            } else if (requestCode == REQUEST_PERMISSIONS_FOR_IMAGE) {
                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                            grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                        permissionListener.acceptedPermission();
                    } else {
                        // Permission denied.
                        permissionListener.deniedPermission();

                    }
                } else {
                    // Permission denied.
                    permissionListener.deniedPermission();
                }
            }

            //permission handling for Image
            else if (requestCode == REQUEST_PERMISSIONS_FOR_RECEIVE_SMS) {
                if (grantResults.length > 0) {

                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionListener.acceptedPermission();
                    } else {
                        permissionListener.deniedPermission();
                    }

                } else {
                    // Permission denied.
                    permissionListener.deniedPermission();
                }
            } else if (requestCode == REQUEST_PERMISSIONS_FOR_GPS_FILE) {
                if (grantResults.length > 0) {
                    if (grantResults[0] >= PackageManager.PERMISSION_GRANTED && grantResults[1] >= PackageManager.PERMISSION_GRANTED
                            && grantResults[2] >= PackageManager.PERMISSION_GRANTED) {
                        permissionListener.acceptedPermission();
                    } else {
                        permissionListener.deniedPermission();
                    }

                } else {
                    // Permission denied.
                    permissionListener.deniedPermission();
                }
            }
            permissionListener = null;
        }
    }
    /******* Permissions Module Ended ********/


    /******* Image Module Started ********/


    // Image capture module
    protected void CaptureImage(ImageSelectListener selectedImage, QuestionBean questionBean) {

        this.selectedImage = selectedImage;
        this.questionBean = questionBean;


        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        File file = getOutputImageFile();
        if (file != null) {
            picUri = getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
            i.putExtra(MediaStore.EXTRA_OUTPUT, picUri); // set the take_photo file
            try {
                startActivityForResult(i, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(BaseActivity.this, R.string.sorry_failed_to_capture_photo, Toast.LENGTH_LONG).show();
                BaseActivity.logDatabase(AppConfing.END_POINT, "There are no email applications installed."
                        , AppConfing.UNEXPECTED_ERROR, "BaseActivity");

            }


        } else {
            // failed to capture take_photo
            Toast.makeText(BaseActivity.this, R.string.sorry_failed_to_capture_photo, Toast.LENGTH_LONG).show();
            this.selectedImage = null;
            this.questionBean = null;

        }
        //filePath = file.getAbsolutePath();
//        <!--nougat-->

    }

    protected void PickImage(ImageSelectListener selectedImage, QuestionBean questionBean) {

        this.selectedImage = selectedImage;
        this.questionBean = questionBean;


        Intent intent;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        } else {

            intent = new Intent(Intent.ACTION_GET_CONTENT);

        }
        // <!--nougat--add two permisssion>
        intent.setFlags(FLAG_GRANT_READ_URI_PERMISSION);
        intent.setFlags(FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        //                <!--nougat--show image>
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }


    @Override
    @SuppressLint("NewApi")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        File actualImage;
        File compress_image_path;
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == -1 && picUri != null && selectedImage != null) {
                try {
                    actualImage = FileUtil.from(BaseActivity.this, picUri);

                    compress_image_path = compressImage(actualImage.getAbsolutePath(), BaseActivity.this);

                    if (compress_image_path != null && compress_image_path.length() > 0) {
                        selectedImage.imagePath(compress_image_path.getAbsolutePath(), questionBean);
                    } else {
                        selectedImage.imageSelectionFailure();
                    }

                    if (picUri.getPath() != null && !picUri.getPath().equals("")) {
                        if (new File(picUri.getPath()).exists()) {
                            new File(picUri.getPath()).delete();
                        }

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            getContentResolver().delete(picUri, null, null);
                        }
                    }

                } catch (IOException e) {
                     Log.e("error",e.getMessage());
                    Log.e("error",e.getMessage());

                    BaseActivity.logDatabase(AppConfing.END_POINT,
                            e.getMessage(), FILE_NOT_FOUND_EXCEPTION_ERROR, "BaseActivity");
                    logText("Mobile Image " + e.getMessage());
                    selectedImage.imageSelectionFailure();
                }
            } else if (resultCode == 0) {
                // user cancelled Image capture
                //       Toast.makeText(BaseActivity.this, R.string.sorry_failed_to_capture_photo, Toast.LENGTH_SHORT).show();
            } else {
                // failed to capture take_photo
                Toast.makeText(BaseActivity.this, R.string.sorry_failed_to_capture_photo, Toast.LENGTH_LONG).show();
            }
            selectedImage = null;
            questionBean = null;

        } else if (requestCode == PICK_IMAGE_REQUEST) {

            if (resultCode == -1 && selectedImage != null && data != null && data.getData() != null) {


                try {
                    actualImage = FileUtil.from(BaseActivity.this, data.getData());
                    compress_image_path = compressImage(actualImage.getAbsolutePath(), BaseActivity.this);

                    if (compress_image_path != null && compress_image_path.length() > 0) {
                        selectedImage.imagePath(compress_image_path.getAbsolutePath(), questionBean);
                    } else {
                        selectedImage.imageSelectionFailure();
                    }

                } catch (IOException e) {
                     Log.e("error",e.getMessage());;

                    BaseActivity.logDatabase(AppConfing.END_POINT, e.getMessage(), FILE_NOT_FOUND_EXCEPTION_ERROR, "BaseActivity");

                }


            } else if (resultCode == 0) {
                // user cancelled Image capture
                //   Toast.makeText(BaseActivity.this, R.string.your_cancel, Toast.LENGTH_SHORT).show();

            } else {
                // failed to capture take_photo
                Toast.makeText(BaseActivity.this, R.string.sorry_failed_to_pick_photo, Toast.LENGTH_LONG).show();
            }
            selectedImage = null;
            questionBean = null;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public File compressImage(String filePath, Context context) {

        // String filePath = getRealPathFromURI(imageUri,context);

        try {


            Bitmap scaledBitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

            int actualHeight = options.outHeight;
            int actualWidth = options.outWidth;

//      max Height and width values of the compressed take_photo is taken as 816x612
            float maxHeight = 816.0f;
            float maxWidth = 612.0f;
            float imgRatio = (float) actualWidth / actualHeight;
            float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the take_photo
            if (actualHeight > maxHeight || actualWidth > maxWidth) {
                if (imgRatio < maxRatio) {
                    imgRatio = maxHeight / actualHeight;
                    actualWidth = (int) (imgRatio * actualWidth);
                    actualHeight = (int) maxHeight;
                } else if (imgRatio > maxRatio) {
                    imgRatio = maxWidth / actualWidth;
                    actualHeight = (int) (imgRatio * actualHeight);
                    actualWidth = (int) maxWidth;
                } else {
                    actualHeight = (int) maxHeight;
                    actualWidth = (int) maxWidth;
                }
            }

//      setting inSampleSize value allows to load a scaled down version of the original take_photo
            options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
            options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inTempStorage = new byte[16 * 1024];


            try {
//          load the bitmap from its preview_image_path
                bmp = BitmapFactory.decodeFile(filePath, options);
            } catch (OutOfMemoryError exception) {
                BaseActivity.logDatabase(AppConfing.END_POINT, exception.getMessage(), OUT_OF_MEMORY_EXCEPTION_ERROR, "BaseActivity");

            }


            try {
                scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
            } catch (OutOfMemoryError exception) {
                BaseActivity.logDatabase(AppConfing.END_POINT, exception.getMessage(), OUT_OF_MEMORY_EXCEPTION_ERROR, "BaseActivity");

            }

            float ratioX = actualWidth / (float) options.outWidth;
            float ratioY = actualHeight / (float) options.outHeight;
            float middleX = actualWidth / 2.0f;
            float middleY = actualHeight / 2.0f;

            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

            Canvas canvas = new Canvas(scaledBitmap);
            canvas.setMatrix(scaleMatrix);
            canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the take_photo and display it properly

            ExifInterface exif;
            try {
                exif = new ExifInterface(filePath);

                int orientation = exif.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, 1);
                Log.d("EXIF", "Exif: " + orientation);

                Matrix matrix = new Matrix();
                matrix.postRotate(rotation);
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                } else if (orientation == 0) {
                    matrix.postRotate(0);
                } else if (orientation == 1) {
                    matrix.postRotate(0);
                }

                if (scaledBitmap != null) {
                    scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                            scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                            true);
                }
            } catch (IOException e) {
                 Log.e("error",e.getMessage());;
            }

            FileOutputStream out = null;

            filename = getOutputImageFile();

            try {
                out = new FileOutputStream(filename);

//          write the compressed bitmap at the destination specified by filename.
                if (scaledBitmap != null) {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                }


            } catch (FileNotFoundException e) {
                 Log.e("error",e.getMessage());;
                BaseActivity.logDatabase(AppConfing.END_POINT, e.getMessage(), FILE_NOT_FOUND_EXCEPTION_ERROR, "BaseActivity");
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.getMessage();
            BaseActivity.logDatabase(AppConfing.END_POINT, e.getMessage(), DIVISSION_BY_ZERO, "BaseActivity");

            Toast.makeText(context, R.string.image_not_captured, Toast.LENGTH_LONG).show();
        }

        return filename;

    }


    public File getOutputAudioFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                getFilesDir(),
                DATA_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.ENGLISH).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "Recoding" + "_" + timeStamp + ".mp3");

        return mediaFile;
    }


    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = (float) (width * height);
        final float totalReqPixelsCap = (float) (reqWidth * reqHeight * 2);
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /******* Image Module Ended ********/


    /******* Logs Module Started ********/

    // Error Response handle
    public static String ParseErrorMessage(String msg) {

        // {"success":false,"status":401,"message":"Authentication Failed"}
        try {
            JSONObject jsonObject = new JSONObject(msg);
            errorMessgae = jsonObject.get("message").toString();
        } catch (JSONException e) {
             Log.e("error",e.getMessage());;
        }
        return errorMessgae;
    }

    public File getOutputImageFile() {

        File mediaStorageDir = new File(
                getFilesDir(),
                DATA_DIRECTORY_NAME);


        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",
                Locale.ENGLISH).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG" + "_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static void logDatabase(String endpoint, String errorMessgae, String errorCode, String transactionId) {

//        preferenceHelper = new PreferenceHelper(MyApplication.getInstance(), AppConfing.DELTA_PREF_NAME);
//        user_name = preferenceHelper.LoadStringPref(AppConfing.USER_MOBILE, "");
//        appVersion = preferenceHelper.LoadStringPref(AppConfing.APP_VERSION, "");

        String aa = Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String oSversion = String.valueOf(android.os.Build.VERSION.SDK_INT);

        String mobileDetails = "" + aa + " - " + manufacturer + " - " + oSversion;


    }

    public static void logText(String text) {

//        preferenceHelper = new PreferenceHelper(MyApplication.getInstance(), AppConfing.DELTA_PREF_NAME);
//        user_name = preferenceHelper.LoadStringPref(AppConfing.USER_MOBILE, "");
//        appVersion = preferenceHelper.LoadStringPref(AppConfing.APP_VERSION, "");


        String aa = Build.MODEL;
        String manufacturer = android.os.Build.MANUFACTURER;
        String oSversion = String.valueOf(android.os.Build.VERSION.SDK_INT);


        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/TataTrust_Delta");
        if (!dir.exists()) {
            dir.mkdir();
        }


        File file = new File(dir, user_name + "_Logs.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                 Log.e("error",e.getMessage());;
            }
        }
        BufferedWriter buf = null;
        try {
            //BufferedWriter for performance, true to set append to file flag
            buf = new BufferedWriter(new FileWriter(file, true));
            buf.append(aa + "-" + oSversion + "-" + appVersion + "-" + LocalTime() + "-" + text);
            buf.newLine();
        } catch (IOException e) {
             Log.e("error",e.getMessage());;
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                     Log.e("error",e.getMessage());;
                }
            }
        }
    }


    /******* Logs Module Ended ********/


    /******** GPS Module Started ********/


    /******** GPS Module Ended ********/

    public static String getLocalTime(String dateTime) {
        SimpleDateFormat inputSDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'", Locale.ENGLISH);
        inputSDF.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date myDate = null;
        try {
            myDate = inputSDF.parse(dateTime);
        } catch (ParseException e) {
             Log.e("error",e.getMessage());;
        }
//
        SimpleDateFormat outputSDF = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
        System.out.println(outputSDF.format(myDate));
        System.out.println(TimeZone.getDefault().getID());
        return outputSDF.format(myDate);
    }

    //hiding keyboard
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        isGPSRequested = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }


    public static void versionDialog(final Context mContext) {
        AlertDialog.Builder altDialog = new AlertDialog.Builder(mContext);
        altDialog.setTitle("Please download latest version of app");
        altDialog.setMessage("Latest Version is available on PlayStore.");
        altDialog.setCancelable(false);
        altDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                String url = "https://play.google.com/store/apps/details?id=" + mContext.getPackageName();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                mContext.startActivity(i);
                dialog.dismiss();
                ((Activity) mContext).finish();
            }
        });
        altDialog.show();
    }

    public static boolean isAppUpdateRequired(Context mContext) {
        PreferenceHelper preferenceHelper = new PreferenceHelper(mContext, AppConfing.DELTA_PREF_NAME);
        String webV = preferenceHelper.LoadStringPref(AppConfing.LATEST_VERSION, "");
        if (!webV.equals("")) {
            PackageInfo pInfo = null;
            try {
                pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                String version = pInfo.versionName;
                version = version.replace(".", "");
                webV = webV.replace(".", "");

                if (Integer.parseInt(version) < Integer.parseInt(webV)) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                 Log.e("error",e.getMessage());;
                return false;
            }
        } else {
            return false;
        }
        return false;

    }


    public void showCustomToast(String s, int status) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.dy_custom_toast,
                (ViewGroup) findViewById(R.id.toast_layout_root));

        if (status == 0) {
            layout.setBackgroundResource(R.drawable.green_circle);
        } else if (status == 2) {
            layout.setBackgroundResource(R.drawable.orange_circle);
        }


        TextView text = layout.findViewById(R.id.text);
        text.setText(s);

        Toast toast = new Toast(getApplicationContext());
//        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // permissionListener = null;
        selectedImage = null;

        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(this);
            mGoogleApiClient.disconnect();
            mGoogleApiClient = null;
        }
    }


}
