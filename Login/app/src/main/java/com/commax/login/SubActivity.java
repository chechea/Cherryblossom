package com.commax.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.commax.login.Common.AboutFile;
import com.commax.login.Common.FileEx;
import com.commax.login.Common.GetMacaddress;
import com.commax.login.Common.TypeDef;
import com.commax.login.UC.DongHo;
import com.commax.login.UC.Utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/*
 * Created by OWNER on 2016-07-14.
 */
public class SubActivity extends Activity {
    String TAG = SubActivity.class.getSimpleName();
    private static SubActivity instance;

    public static SubActivity getInstance() {
        return instance;
    }

    private Context context;

    AboutFile aboutFile = new AboutFile();
    JSONHelper_sub jsonHelper_sub = new JSONHelper_sub();
    Customdialog_password_change customdialog_password_change;
    Customdialog_init customdialog_init;
    Utility utility = new Utility();

    GetMacaddress getMacaddress;

    // UI
    EditText id;
    EditText pwd;
    Button pwd_change;
    Button user_initial;

    private View mLoadingContainer;
    LinearLayout all_view;
    /*custom toast pxd*/
    public LayoutInflater toastInflater;
    public View toastLayout;
    public TextView toastTextView;

    //timer
    int mainTime = 3;


    //for toast
    Toast mToast;
    public ToastMessageHandler toastHandler = null;

    String cloud_ip = readCloudDNSfile("AuthServer_DNS");
    public String model_name = readCloudDNSfile("ProductModel");
    public String client_id = readCloudDNSfile("Client_ID");
    public String client_secret = readCloudDNSfile("Client_Secret");
    String user_id;
    String user_pwd;
    public String Mac_address;

    /*
    //TODO Full ip
    String mDong= "1234";
    String mHo = "5678";
    */

    //TODO not full IP
    String mDong;
    String mHo ;

    String site_code;
    String resourceNo;
    String domainName = "commax.com";
    //UC ip, port
    public String uc_group_ip_port;
    public String uc_user_ip_port;
    public String ews;

    public int repeat_count = 0;
    public int repeat_count_user = 0;

    //Local server
    String LocalserverIP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        context = this;
        setContentView(R.layout.activity_sub_main);
        hideNavigationBar();
        //custom toast pxd
        try {
            toastInflater = getLayoutInflater();
            toastLayout = toastInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast_layout));
            toastTextView = (TextView) toastLayout.findViewById(R.id.textView1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //toast handler
        toastHandler = new ToastMessageHandler();
        //Activity 속성 다른곳에서 사용하도록
        instance = this;
        mLoadingContainer = findViewById(R.id.loading_container);
        all_view = (LinearLayout) findViewById(R.id.all_view);
        getMacaddress = new GetMacaddress(context);

        try
        {
            //get information
            Mac_address = getMacaddress.getMacAddress();
            getDongho();
            site_code = aboutFile.readFile("SiteCode");
            Log.d(TAG, "Mac_address : " + Mac_address);
            Log.d(TAG, "siteCode : " + site_code);
            aboutFile.writeFile("Dong", mDong);
            aboutFile.writeFile("Ho", mHo);
            resourceNo = aboutFile.readFile(TypeDef.resourceNo);
            LocalserverIP = aboutFile.readFile(TypeDef.LocalServerIP);

        }catch (Exception e)
        {
            e.printStackTrace();
        }

        try {
            Log.d(TAG, "client_id : " + client_id);
            client_id = URLEncoder.encode(client_id, "UTF-8");
            client_secret = URLEncoder.encode(client_secret, "UTF-8");
//            cloud_ip = URLEncoder.encode(cloud_ip, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void pxd_custom_toast(String txt) {
        Toast toast = new Toast(getApplicationContext());
        toastTextView.setText(txt);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    void initData() {
        Log.d(TAG,"initData()");
        //DATA 선언 및 초기화

        id = (EditText) findViewById(R.id.user_id);
        pwd = (EditText) findViewById(R.id.user_pwd);
        pwd_change = (Button) findViewById(R.id.user_pwd_change);
        user_initial = (Button) findViewById(R.id.user_initial);

        id.setFocusable(false); //포커스 불가능
        id.setClickable(false); //클릭 불가능
        id.clearFocus(); //포커스 클리어

        pwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        pwd.setTransformationMethod(PasswordTransformationMethod.getInstance()); //비밀번호 타입으로 입력
        pwd.clearFocus();
        pwd.setFocusable(false);
        pwd.setClickable(false);
        id.setText(user_id);
        // 암호회된 pwd
        String pwd_length_string = "*";

        //1.0.6 이전 버전에서 Properties 파일의 password 랑 호환 가능하도록 설정 하기 위함
        try{
            if(aboutFile.readFile("password").length() >= 3 )
            {
                int i = aboutFile.readFile("password").length();
                Log.d(TAG ,"i = " +i);

                pwd.setText("******");
                aboutFile.writeFile("password",String.valueOf(i));
            }
            else
            {
                for (int i = 0; i < (Integer.valueOf(aboutFile.readFile("password")) - 1); i++) {
                    pwd_length_string = pwd_length_string + "*";
                }
                Log.d(TAG, "pwd_length_string : " + pwd_length_string);
                pwd.setText(pwd_length_string);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //clound_svr.i 파일에 필요 값들이 있는지를 파악
        if (TextUtils.isEmpty(client_id) || TextUtils.isEmpty(client_secret) || TextUtils.isEmpty(cloud_ip) ||
                TextUtils.isEmpty(readCloudDNSfile("UC_Group_port"))|| TextUtils.isEmpty(readCloudDNSfile("UC_User_port")))
        {
            pxd_custom_toast(getString(R.string.file_error));
        }
    }

    //토큰 발급이 완료되면 ews서버 , 토큰 을 사용하는 Access랑 UC 등록
    public void Accss_Uc_register()
    {
        //uracle access 등록

        if (TextUtils.isEmpty(aboutFile.readFile("access"))) {
            try {
                Log.e(TAG, "dong/ho : " + mDong + "/" + mHo);

                //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code
                //토큰에서 가져오는 정보로 CreateAccount.properties 파일에서 가져오기
                String access_url = aboutFile.readFile("access_ip") +":"+ aboutFile.readFile("access_port");

                Log.d(TAG, "workType : " + aboutFile.readFile("workType"));
                String[] access_register = {"access_register", mDong, mHo, site_code , aboutFile.readFile("workType") , access_url};
                startTask(access_register);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (aboutFile.readFile("access").equals("yes")) {
            Log.i(TAG, "access 등록 되어 있습니다.");
        }

        //UC Group User 등록
        if(TextUtils.isEmpty(aboutFile.readFile("UC_Group_Register")) || TextUtils.isEmpty(aboutFile.readFile("UC_User_Register")))
        {
            if(!(TextUtils.isEmpty(aboutFile.readFile("ews"))))
            {
                ews = aboutFile.readFile("ews");
                Log.d(TAG, "ews : " + ews);

                String uc_group_port=readCloudDNSfile("UC_Group_port");
                String uc_user_port=readCloudDNSfile("UC_User_port");
                uc_group_ip_port = ews+uc_group_port;
                uc_user_ip_port = ews+uc_user_port;

                Log.e(TAG, "uc group ip : " + uc_group_ip_port + " , uc user ip : " + uc_user_ip_port);
                //UC 그룹 등록
                //params[0] : type , [1] : ip:port , [2] : ADD , UPDATE , DELETE , [3] : site code , [4] :dong , [5] : ho ,[6] : resourceNO , [7] : domainName
                String[] uc_group_register = {"uc_group", uc_group_ip_port, "ADD", site_code, mDong, mHo, resourceNo, domainName};
                startTask(uc_group_register);
            }
            else
            {
                Log.d(TAG ,"ews : " + aboutFile.readFile("ews") + " , IP정보가 없어 UC등록을 진행하지 못합니다. ");
                Log.e(TAG, "ews ip 정보가 없습니다.");
            }
        }
        else
        {
            Log.i(TAG ,"UC group , user 가  등록되어 있습니다. ");
        }


        /*try
        {
            //Local Server IP
            ServerIPLocal serverIPLocal = new ServerIPLocal(context);
            LocalserverIP = serverIPLocal.getValue();
            Log.d(TAG, "serverIP : " +LocalserverIP);
        }catch (Exception e)
        {
            e.printStackTrace();
        }*/


        if(LocalserverIP.equals("127.0.0.1"))
        {
            Log.e(TAG, "Local server is not exist");
        }else {
            if(TextUtils.isEmpty(aboutFile.readFile("resourceNo_send")))
            {
                String[] resourceNo_send  = {"resourceNo_send", LocalserverIP ,user_id , mDong, mHo , Mac_address , resourceNo };
                startTask(resourceNo_send);
            }
            else {
                Log.i(TAG, " send resourceNo to Local Server - 로컬 서버로 resourceNo 등록 하였습니다.");
            }
        }
    }

    public void onClick(View view) {
        String change_pwd = pwd.getText().toString();
        //키보드 숨기기
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pwd.getWindowToken(), 0);
        if (view.equals(pwd_change)) {
            Log.d(TAG, " Pwd change");
            customdialog_password_change = new Customdialog_password_change(context);

            customdialog_password_change.show();
        }
        else if (view.equals(user_initial)) {
            Log.d(TAG, " user id initial ");
            Log.d(TAG, "Token  : " + aboutFile.readFile("token"));

            String[] params = {"uc_user", aboutFile.readFile("ews") + readCloudDNSfile("UC_Group_port"), "DELETE",
                   site_code , mDong, mHo, resourceNo, domainName , LocalserverIP , user_id , Mac_address};
            // initial dialog 초기화
            if(TextUtils.isEmpty(aboutFile.readFile("UC_Group_Register")))
            {
                //유라클 초기화만 진행
                Log.d(TAG, "uracle initialization");
                customdialog_init = new Customdialog_init(context, params);
            }
            else
            {
                Log.d(TAG, "uc initialization ");
                customdialog_init = new Customdialog_init(context, params);
            }
            customdialog_init.show();
        } else if (view.equals(findViewById(R.id.back_button_sub))) {
            finish();
        }
    }

    /**
     * 로딩뷰 표시 설정
     *
     * @param isView 표시 유무
     */
    public void setLoadingView(boolean isView) {
        if (isView) {
            // 화면 로딩뷰 표시
            Log.d(TAG, "Loading View display");
            mLoadingContainer.setVisibility(View.VISIBLE);
            all_view.setVisibility(View.GONE);
        } else {
            // 화면 어플 리스트 표시
            Log.d(TAG, "List View display");
            mLoadingContainer.setVisibility(View.GONE);
            all_view.setVisibility(View.VISIBLE);
        }
    }

    // ID 중복 체크 에러 Toast message 핸들러
    class ToastMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    pxd_custom_toast((String) msg.obj);
                    break;
                case 1:
                    setLoadingView(false);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 작업 시작
     */
    public void startTask(String[] params) {
        Log.d(TAG, "startTask");
        Log.d(TAG, "AppTask exe");
        new AppTask().execute(params);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        setLoadingView(true);

        //토큰 요청
        new AppTask().execute("oauth", "authorize", Mac_address, model_name, client_id, client_secret, "sub");

        user_id = aboutFile.readFile("id");
        Log.d(TAG, "id : " + user_id);
        user_pwd = aboutFile.readFile("password");
        Log.d(TAG, "pwd : " + user_pwd);

        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
    }

    //Send Custom Broadcast Message
    public void sendCustomBroadcastMessage(String strBroadcastAction) {
        Intent intent = new Intent(strBroadcastAction);
        context.sendBroadcast(intent);
        intent = null;
    }

    /**
     * 작업 태스크
     *
     * @author nohhs
     */
    private class AppTask extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            // 로딩뷰 시작
            Log.d(TAG, "AppTask : onPreExecute");
        }

        @Override
        protected Void doInBackground(String... params) {
            Log.d(TAG, "AppTask : doInBackground");
//            mHandler.sendEmptyMessage(5);
            // 어플리스트 작업시작
            try {
                jsonHelper_sub.restCall("127.0.0.1", cloud_ip, params[0], params, toastHandler);
                Log.d(TAG, "JSONHelper_sub.restCall()");

            } catch (Exception e) {
                Message msg = SubActivity.getInstance().toastHandler.obtainMessage();
                msg.what = 0;
                msg.obj = String.valueOf(getString(R.string.network_error));
                SubActivity.getInstance().toastHandler.sendMessage(msg);
                Log.e(TAG, "restCall error");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // 어댑터 갱신
            //TODO 이곳에서만 암호회 진행해서 settext 하면 됨
//            SubActivity.getInstance().pwd.setText(aboutFile.readFile("password"));
            try {
                if (TextUtils.isEmpty(aboutFile.readFile("password"))) {
                    Log.d(TAG, "file not exist");
                } else {
                    String pwd_length_string = "*";
                    try
                    {
                        Log.d(TAG, "password length : " + aboutFile.readFile("password"));
                        Log.d(TAG, " pass word int = " + (Integer.valueOf(aboutFile.readFile("password")) -1));
                    }catch (NumberFormatException e)
                    {
                        e.printStackTrace();
                    }

                    for (int i = 0; i < (Integer.valueOf(aboutFile.readFile("password")) - 1); i++) {
                        pwd_length_string = pwd_length_string + "*";
                    }
                    Log.d(TAG, "pwd_length_string : " + pwd_length_string);
                    SubActivity.getInstance().pwd.setText(pwd_length_string);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "onpostExcute catch()");
            }
            setLoadingView(false);
            Log.d(TAG, "AppTask : onPostExecute");
        }
    }

    ;


    public String readCloudDNSfile(String value) {

        FileEx io = new FileEx();
        String[] files = null;
        String server_dns = "";

        try {
            files = io.readFile("/user/app/bin/cloud_svr.i");
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }

        if (files == null) {
            return null;
        }

        if (files.length > 0) {
            // ���� üũ
            if (files == null) {
                return null;
            }
            if ("".equals(files[0])) {
                return null;
            }
            if ("-1".equals(files[0])) {
                return null;
            }
        }
        for (int i = 0; i < files.length; i++) {
            String line = files[i];
            if (line.contains("#")) {
                continue;
            }
            if (line.contains(value)) {

                server_dns = line.replace(value + "=", "");
            }
        }
        return server_dns;
    }

    void getDongho() {
        DongHo dongho = new DongHo(getApplicationContext());
        String tempinfor = dongho.getValue();
        String[] realdata = TextUtils.split(tempinfor, "-");

        if (tempinfor.equals("")) {
            Log.i(TAG, "get(Dong-Ho) is Null");
        } else {
            Log.i(TAG, "get(Dong-Ho) is Success");
        }

        mDong = realdata[0];
        mHo = realdata[1];

        Log.d(TAG, "Dong : " + mDong + " , Ho : " + mHo);
    }

    //IP Home IoT 네비게이션 바 제거
    public void hideNavigationBar(){

        try {
            // 액티비티 아래의 네비게이션 바가 안보이게
            final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

            // This work only for android 4.4+
            getWindow().getDecorView().setSystemUiVisibility(flags);

            // Code below is to handle presses of Volume up or Volume down.
            // Without this, after pressing volume buttons, the navigation bar will
            // show up and won't hide
            final View decorView = getWindow().getDecorView();
            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flags);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
