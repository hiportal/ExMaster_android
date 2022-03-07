package com.ex.master;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ex.master.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

import static com.ex.master.util.CommonConstant.MOB_SERVER_URL;

public class PermissionActivity extends AppCompatActivity {
    public static String TAG = PermissionActivity.class.getSimpleName();

    Context context = PermissionActivity.this;
    final int PERMISSION_REQ_CODE = 2;
    final int PERMISSION_MULTI_CODE = 100;
    List<String> listPermisssionsNeeded = new ArrayList<>();

    public static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static String[] permissionName={
            "저장소"

    };
    public static String[] permissionDetail={
            "앱 사용을 위해 저장소 사용을 허용합니다."
    };

    Button btn_confirm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_permission);

        checkPermission();
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String thisPermission : permissions) {
                if (ContextCompat.checkSelfPermission(context, thisPermission) != PackageManager.PERMISSION_GRANTED) {
                    LogUtil.d(TAG, "***** checkPermission() - permission : " + thisPermission);
                    listPermisssionsNeeded.add(thisPermission);
                }
            }
            if (listPermisssionsNeeded.size() <= 0) {
                LogUtil.d(TAG, "***** checkPermission() - goNext");
                goNext(true);
            } else {
                initView();
            }
        } else {
            LogUtil.d(TAG, "***** checkPermission() - SDK Version Low");
            goNext(true);
        }
    }


    private void initView(){
        setContentView(R.layout.activity_permission);

        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermission()){
                    goNext(true);
                }
            }
        });
    }

    private boolean checkAndRequestPermission() {
        LogUtil.d(TAG, "***** checkAndRequestPermission() - permissions Length : "+permissions.length);
        LogUtil.d(TAG, "***** checkAndRequestPermission() - listPermisssionsNeeded Size : "+listPermisssionsNeeded.size());
        if (!listPermisssionsNeeded.isEmpty()) {
            LogUtil.d(TAG, "***** checkAndRequestPermission() - @@ "+listPermisssionsNeeded.toArray(new String[listPermisssionsNeeded.size()]));
            ActivityCompat.requestPermissions(this, listPermisssionsNeeded.toArray(new String[listPermisssionsNeeded.size()]), PERMISSION_REQ_CODE);
            return false;
        }
        return true;
    }

    //권한 요청 시스템 팝업 결과(허용/거부) 콜백
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        LogUtil.d(TAG, "***** onRequestPermissionsResult() - requestCode : "+requestCode);
        LogUtil.d(TAG, "***** onRequestPermissionsResult() - permissions : "+permissions[0]);
        LogUtil.d(TAG, "***** onRequestPermissionsResult() - grantResults length : "+grantResults.length);

        ArrayList<String> deniedPermission = new ArrayList<String>();

        switch (requestCode){
            case PERMISSION_REQ_CODE:
                if(grantResults.length>0){
                    for(int i=0; i<grantResults.length; i++){
                        LogUtil.d(TAG, "***** onRequestPermissionsResult() - result : "+i);
                        if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                            //권한 허가
                        } else {
                            //  권한 거부
                            deniedPermission.add(permissionName[i]);
                        }
                    }
                    LogUtil.d(TAG, "***** onRequestPermissionsResult() - 권한거부 - deniedPermission.size() : "+deniedPermission.size());
//                    Toast.makeText(context, deniedPermission.size()+" 개의 권한이 거부되었습니다.", Toast.LENGTH_LONG).show();
                    if(deniedPermission.size() <= 0){
                        goNext(true);
                    } else {
                        goNext(false);
                    }
                }
                return;
        }
    }

    private void goNext(boolean successGubun){
        LogUtil.d(TAG, "***** goNext() - successGubun : "+successGubun);

        if(successGubun) {
//            Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
            Intent intent = new Intent(PermissionActivity.this, WebViewActivity.class);
//            Intent intent = new Intent(PermissionActivity.this, MainActivity.class);
            intent.putExtra("URL", MOB_SERVER_URL);

            startActivity(intent);
            finish();
        } else {
            //거부된 권한이 존재 시 팝업 후 앱 종료
            showPop();

        }
        //this.finish();
    }

    private void showPop() {
/*
        AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle(MOB_TITLE).setMessage("모든 권한을 허용해 주세요.");

        builder.setPositiveButton("확 인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Toast.makeText(context, "OK Click", Toast.LENGTH_LONG).show();

                //앱 종료
                PermissionActivity.this.finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

*/
        final Dialog dialog = new Dialog(context, R.style.CustomDialog);
        final LayoutInflater layoutInflater = getLayoutInflater();

        View customMessage = layoutInflater.inflate(R.layout.layout_dialog, null);
        final Button btn_confirm = (Button) customMessage.findViewById(R.id.btn_confirm);
        final TextView dialog_content = (TextView)customMessage.findViewById(R.id.dialog_content);
        dialog_content.setText("모든 권한을 허용해 주세요.");
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //앱 종료
                PermissionActivity.this.finishAffinity();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
        dialog.setCancelable(false);
        dialog.setContentView(customMessage);
        dialog.show();
    }

}
