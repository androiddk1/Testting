package com.moviebook.ar_dynamic1;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * 权限管理父类Activity
 * <p>
 * 需要动态申请权限的界面继承该类即可
 */
public abstract class PermissionActivity extends Activity {

    public static final int PERMISSION_REQUEST_CODE = 0;//系统授权管理页面时的结果参数
    public static final String PACKAGE_URL_SCHEME = "package:";//权限方案
    private boolean isRequestCheck = true;//判断是否需要系统权限检测。防止和系统提示框重叠
    public static final int REQUEST_SETTING_CODE = 0x1299;//从设置界面设置完权限返回标记

    /**
     * 申请权限调用方法
     */
    protected void requestPermission() {
        if (getPermissions() != null) {

            if (permissionSet(getPermissions())) {
                requestPermissions(getPermissions());
            } else {
                getAllGrantedPermission();
            }
        }
    }

    //获取全部权限
    public boolean hasAllPermissionGranted(int[] grantResults) {
        for (int grantResult : grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    //打开系统应用设置(ACTION_APPLICATION_DETAILS_SETTINGS:系统设置权限)
    public void startAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivityForResult(intent, REQUEST_SETTING_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTING_CODE) {
            if (!isRequestCheck) {
                if (getPermissions() != null) {
                    if (permissionSet(getPermissions())) {

                        showMissingPermissionDialog();
                    } else {
                        //获取全部权限,走正常业务
                        getAllGrantedPermission();
                    }
                }
            } else {
                isRequestCheck = true;
            }
        }
    }

    //请求权限去兼容版本
    public void requestPermissions(String... permission) {
        ActivityCompat.requestPermissions(this, permission, PERMISSION_REQUEST_CODE);

    }

    /**
     * 用于权限管理
     * 如果全部授权的话，则直接通过进入
     * 如果权限拒绝，缺失权限时，则使用dialog提示
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (PERMISSION_REQUEST_CODE == requestCode && hasAllPermissionGranted(grantResults)) //判断请求码与请求结果是否一致
        {
            isRequestCheck = true;//需要检测权限，直接进入，否则提示对话框进行设置
            getAllGrantedPermission();
        } else {
            //提示对话框设置
            isRequestCheck = false;
            showMissingPermissionDialog();
        }
    }

    /*
    * 当获取到所需权限后，进行相关业务操作
     */
    public void getAllGrantedPermission() {

    }

    /**
     * 子类必须申明 需要被检测的相关权限集合
     *
     * @return
     */
    protected abstract String[] getPermissions();


    public boolean permissionSet(String... permissions) {
        for (String permission : permissions) {
            if (isLackPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断某个权限是否被锁定
     *
     * @param permission
     * @return
     */
    private boolean isLackPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED;
    }

    //显示对话框提示用户缺少权限
    public void showMissingPermissionDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("帮助");
        builder.setMessage("当前应用缺少必要权限。请点击设置-权限-打开所需权限，同意打开相关权限并返回。");

        builder.setNegativeButton("立即退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.setPositiveButton("去设置开启", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startAppSettings();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }
}
