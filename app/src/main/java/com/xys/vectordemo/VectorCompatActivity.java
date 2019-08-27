package com.xys.vectordemo;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VectorCompatActivity extends AppCompatActivity {
    ImageView mImageView;
    private Context othercontext;
    private SharedPreferences sp;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_l);
        mImageView = (ImageView) findViewById(R.id.image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                Toast.makeText(VectorCompatActivity.this, "点击！", Toast.LENGTH_LONG).show();
            }
        });
        //getOrherAppSP();
        //getXMLData("/storage/emulated/0/dvr/token.xml");

        //wow入侵时间表
        //timeStrToMillisecond("2019-01-01 00:00:00");
        //00:00:00 -> 1546272000000
        //07:00:00 -> 1546297200000
        Log.e("====", "==============*******=" + timeStrToMillisecond("2019-03-20 14:00:00"));
        //开始 ：getDateTimeFromMillisecond(1546272000000L);
        //结束 ：getDateTimeFromMillisecond(1546297200000L);
        //7h=25200 * 1000,17h=61200 * 1000,19h=68400 * 1000,12h=43200 * 1000
        long start = timeStrToMillisecond("2019-08-24 20:00:00");//1546333200000L;
        long end = start + 25200 * 1000L;
        long deltaRound = 43200 * 1000L;
        long deltaStartEnd = 25200 * 1000L;
        for (int i = 0; i < 100; i++) {
            Log.e("====", "开始时间：" + getDateTimeFromMillisecond(start) + "  结束时间：" + getDateTimeFromMillisecond(end) + getCanendarTimeFromMillisecond(start));
            start = end + deltaRound;
            end = start + deltaStartEnd;
        }
        //notifyy();
        getMobileDbm();
        showRAMInfo();
        showROMInfo();
        showDisplayInfo();
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(VectorCompatActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(VectorCompatActivity.this, Manifest.permission.CALL_PHONE)) {
                Toast.makeText(VectorCompatActivity.this, "请授权！", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(VectorCompatActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 1);
            }
        } else {
            //CallPhone();
            CallPhoneEmergency();
        }
    }

    private void CallPhone() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + "112"));
        startActivity(intent);
    }


    void CallPhoneEmergency() {
        Intent intent = new Intent("android.intent.action.CALL_PRIVILEGED", Uri.parse("tel:112"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void anim(View view) {
        ImageView imageView = (ImageView) view;
        Drawable drawable = imageView.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void switchToL() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(new Intent(VectorCompatActivity.this, LActivity.class));
        } else {
            Toast.makeText(VectorCompatActivity.this, "系统版本不支持L plus", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            switchToL();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getOrherAppSP() {
        try {
            othercontext = createPackageContext("com.luobin.dvr", Context.CONTEXT_IGNORE_SECURITY);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        sp = othercontext.getSharedPreferences("token", Context.MODE_WORLD_READABLE);
        String name = sp.getString("name", "");
        String phone = sp.getString("phone", "");
        Log.e("====", "=============getOrherAppSP-name=" + name + "--phone=" + phone);
    }

    private String getXMLData(String fileName) {
        StringBuffer stringBuffer = new StringBuffer();
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        try {
            inputStream = getResources().getAssets().open(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            bufferedReader.close();
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.e("====", "xml-decode:" + stringBuffer.toString());
        return stringBuffer.toString();
    }


    /**
     * 将时间转化成毫秒
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Long timeStrToMillisecond(String time) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Long second = format.parse(time).getTime();
            return second;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1l;
    }

    /**
     * 将毫秒转化成固定格式的时间
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getDateTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String dateStr = simpleDateFormat.format(date);
        return dateStr;
    }

    /**
     * 将毫秒转化成固定日期
     * 时间格式: yyyy-MM-dd HH:mm:ss
     *
     * @param millisecond
     * @return
     */
    public static String getCanendarTimeFromMillisecond(Long millisecond) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(millisecond);
        String[] arr = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        String day_of_week = "  " + arr[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        return day_of_week;
    }

    public void getMobileDbm() {
        int dbm = -1;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        List<CellInfo> cellInfoList = tm.getAllCellInfo();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (null != cellInfoList) {
                for (CellInfo cellInfo : cellInfoList) {
                    if (cellInfo instanceof CellInfoGsm) {
                        CellSignalStrengthGsm cellSignalStrengthGsm = ((CellInfoGsm) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthGsm.getDbm();
                        Log.e("====", "cellSignalStrengthGsm" + cellSignalStrengthGsm.toString());
                    } else if (cellInfo instanceof CellInfoCdma) {
                        CellSignalStrengthCdma cellSignalStrengthCdma = ((CellInfoCdma) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthCdma.getDbm();
                        Log.e("====", "cellSignalStrengthCdma" + cellSignalStrengthCdma.toString());
                    } else if (cellInfo instanceof CellInfoWcdma) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = ((CellInfoWcdma) cellInfo).getCellSignalStrength();
                            dbm = cellSignalStrengthWcdma.getDbm();
                            Log.e("====", "cellSignalStrengthWcdma" + cellSignalStrengthWcdma.toString());
                        }
                    } else if (cellInfo instanceof CellInfoLte) {
                        CellSignalStrengthLte cellSignalStrengthLte = ((CellInfoLte) cellInfo).getCellSignalStrength();
                        dbm = cellSignalStrengthLte.getDbm();
                        Log.e("====", "cellSignalStrengthLte.getAsuLevel()\t" + cellSignalStrengthLte.getAsuLevel());
                        //Log.e("====", "cellSignalStrengthLte.getCqi()\t" + cellSignalStrengthLte.getCqi());
                        Log.e("====", "cellSignalStrengthLte.getDbm()\t " + cellSignalStrengthLte.getDbm());
                        Log.e("====", "cellSignalStrengthLte.getLevel()\t " + cellSignalStrengthLte.getLevel());
                        //Log.e("====", "cellSignalStrengthLte.getRsrp()\t " + cellSignalStrengthLte.getRsrp());
                        //Log.e("====", "cellSignalStrengthLte.getRsrq()\t " + cellSignalStrengthLte.getRsrq());
                        //Log.e("====", "cellSignalStrengthLte.getRssnr()\t " + cellSignalStrengthLte.getRssnr());
                        Log.e("====", "cellSignalStrengthLte.getTimingAdvance()\t " + cellSignalStrengthLte.getTimingAdvance());
                    }
                }
            }
        }
    }

    public void notifyy() {
        final NotificationManager nm = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification =
                new Notification.Builder(getApplicationContext())
                        .setWhen(System.currentTimeMillis())
                        .setContentTitle("电量已充满!")
                        //.setContentText("setContentText")
                        //.setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
                        .setSmallIcon(R.drawable.ic_qs_signal_3g)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_qs_signal_4g))
                        .setTicker("123")
                        //.setColor(getResources().getColor(
                        //        R.color.colorAccent, this.getTheme()))
                        .setColor(Color.RED)
                        //.setFlag(Notification.FLAG_AUTO_CANCEL, true)
                        ///.setFlag(Notification.FLAG_ONLY_ALERT_ONCE, true)
                        .setAutoCancel(true)
                        .setOnlyAlertOnce(true)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setFullScreenIntent(null, true)
                        //.setDefaults(Notification.DEFAULT_VIBRATE)
                        //.setContentIntent(PendingIntent.getActivity(this, 0, clickInten t, 0))
                        //.setDeleteIntent(PendingIntent.getBroadcast(this, 0, deleteInte nt, 0))
                        .build();
        nm.notify(1024, notification);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    nm.cancel(1024);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //获取 ram rom info
    //////////////////////////////////////////////////////
    /*显示RAM的可用和总容量，RAM相当于电脑的内存条*/
    private void showRAMInfo() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        String[] available = fileSize(mi.availMem);
        String[] total = fileSize(mi.totalMem);
        Log.e("====", "RAM " + available[0] + available[1] + "/" + total[0] + total[1]);
    }

    /*显示ROM的可用和总容量，ROM相当于电脑的C盘*/
    private void showROMInfo() {
        File file = Environment.getDataDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();

        String[] total = fileSize(totalBlocks * blockSize);
        String[] available = fileSize(availableBlocks * blockSize);
        Log.e("====", "ROM " + available[0] + available[1] + "/" + total[0] + total[1]);
    }

    /*显示SD卡的可用和总容量，SD卡就相当于电脑C盘以外的硬盘*/
    private void showSDInfo() {
        if (Environment.getExternalStorageState().equals
                (Environment.MEDIA_MOUNTED)) {
            File file = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long totalBlocks = statFs.getBlockCount();
            long availableBlocks = statFs.getAvailableBlocks();

            String[] total = fileSize(totalBlocks * blockSize);
            String[] available = fileSize(availableBlocks * blockSize);
            Log.e("====", "SD " + available[0] + available[1] + "/" + total[0] + total[1]);
        } else {
            Log.e("====", "SD CARD 已删除");
        }
    }

    /*返回为字符串数组[0]为大小[1]为单位KB或者MB*/
    private String[] fileSize(long size) {
        String str = "";
        if (size >= 1024) {
            str = "KB";
            size /= 1024;
            if (size >= 1024) {
                str = "MB";
                size /= 1024;
            }
        }
        /*将每3个数字用,分隔如:1,000*/
        DecimalFormat formatter = new DecimalFormat();
        formatter.setGroupingSize(3);
        String result[] = new String[2];
        result[0] = formatter.format(size);
        result[1] = str;
        return result;
    }

    /////////////////////////////////////////////////////
    private void showDisplayInfo() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        Log.e("====", "screenWidth=" + screenWidth + "--screenHeight=" + screenHeight);
    }
}
