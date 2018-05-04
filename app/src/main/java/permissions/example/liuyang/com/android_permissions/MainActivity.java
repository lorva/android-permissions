package permissions.example.liuyang.com.android_permissions;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Android 6.0 动态申请权限测试
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();

    }

    //=========================== Android 6.0 权限生情代码测试 开始==================

//    参考资料：
//    https://developer.android.com/guide/topics/security/permissions#normal-dangerous
//    https://github.com/googlesamples/android-RuntimePermissions
//
//    https://www.cnblogs.com/xmcx1995/p/5870191.html
//    https://www.cnblogs.com/zhangqie/p/7562959.html

    private static final int REQUEST_CODE = 12345;
    private static final int REQUEST_CODE_SETTING = 54321;

    /**
     * 此测试应用需要用到的危险权限
     * 最全权限列表见SVN文档： Android_6.0_危险权限列表.txt
     */
    private static String[] mPermissionList = new String[] { Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION };

    List<String> permissionListNeed = new ArrayList<String>();
    private void checkPermissions() {
        // 要申请的权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            for (int i = 0; i < mPermissionList.length; i++) {
                if (checkSelfPermission(mPermissionList[i]) != PackageManager.PERMISSION_GRANTED) {
                    // 未授权
                    permissionListNeed.add(mPermissionList[i]);
                }
            }

            if (permissionListNeed != null && permissionListNeed.size() > 0) {
                // 有未授权权限，则申请
                Log.e("checkPermissions", "有未授权权限，则申请");

                String[] permis = permissionListNeed.toArray(new String[permissionListNeed.size()]);
                requestPermissions(permis, REQUEST_CODE);
            }
            else{
                //已经全部授权
                Log.e("checkPermissions", "已经全部授权");
            }
        }
    }

    /**
     * 申请权限回调方法
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        Log.e("onRequestPermissionsResult", "requestCode = " + requestCode);

        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean needTip = false;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                        // 判断是否勾选禁止后不再询问
                        boolean showRequestPermission = shouldShowRequestPermissionRationale(permissions[i]);
                        if (showRequestPermission == false) {
                            needTip = true;
                            Log.e("onRequestPermissionsResult", "权限被拒绝且不再询问：" + permissions[i]);
                            break;
                        }
                    }
                    else{
                        //
                        Log.e("onRequestPermissionsResult", "已授权：" + permissions[i]);
                    }
                }

                if(needTip){
                    //提示用户去应用设置界面手动开启权限
                    showDialogGoToAppSettting();
                }

            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void showDialogGoToAppSettting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("需要以下权限才能正常使用应用");
        builder.setMessage("1.读写SD卡内容\n2.读取手机状态和身份\n3.访问大致位置信息\n");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //到应用设置
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName())); // 根据包名打开对应的设置界面
//				startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_SETTING);  //设置完成之后返回界面onActivityResult检测处理
            }
        });
        builder.create().show();
    }


//    /**
//     * Check that all given permissions have been granted by verifying that each entry in the
//     * given array is of the value {@link PackageManager#PERMISSION_GRANTED}.
//     *
//     * @see Activity#onRequestPermissionsResult(int, String[], int[])
//     */
//    public static boolean verifyPermissions(int[] grantResults) {
//        // At least one result must be checked.
//        if (grantResults.length < 1) {
//            return false;
//        }
//
//        // Verify that each required permission has been granted, otherwise return false.
//        for (int result : grantResults) {
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                return false;
//            }
//        }
//        return true;
//    }


    //=========================== Android 6.0 权限生情代码测试 结束==================


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 设置权限界面返回
        if (requestCode == REQUEST_CODE_SETTING) {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 检查权限是否已经获取
                boolean allIsPermit = true;
                for (int i = 0; i < mPermissionList.length; i++) {
                    if (checkSelfPermission(mPermissionList[i]) != PackageManager.PERMISSION_GRANTED) {
                        // 未授权
                        allIsPermit = false;
                        break;
                    }
                }
                if (allIsPermit) {
                    Toast.makeText(this, "权限设置成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "权限设置未完成", Toast.LENGTH_SHORT).show();
                    // 重新提示去设置界面授权
                    showDialogGoToAppSettting();
                }
            }

        }

    }


//	public static boolean checkPermission(Context context, String permission) {
//		boolean result = false;
//		if (Build.VERSION.SDK_INT >= 23) {
//			try {
//				Class<?> clazz = Class.forName("android.content.Context");
//				Method method = clazz.getMethod("checkSelfPermission", String.class);
//				int rest = (Integer) method.invoke(context, permission);
//				if (rest == PackageManager.PERMISSION_GRANTED) {
//					result = true;
//				} else {
//					result = false;
//				}
//			} catch (Exception e) {
//				result = false;
//			}
//		} else {
//			PackageManager pm = context.getPackageManager();
//			if (pm.checkPermission(permission, context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
//				result = true;
//			}
//		}
//		return result;
//	}


}
