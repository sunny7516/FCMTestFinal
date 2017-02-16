package com.example.tacademy.recyclerviewtest.fcm;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.tacademy.recyclerviewtest.ui.chat.MainActivity;
import com.example.tacademy.recyclerviewtest.R;
import com.example.tacademy.recyclerviewtest.ui.push.ShowPopupActivity;
import com.example.tacademy.recyclerviewtest.db.StorageHelper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import static android.os.Build.VERSION_CODES.JELLY_BEAN;

/**
 *  메시지 수신
 */

public class MyfirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    // 메시지 수신 : 수신 이벤트 발생시 자동 호출
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.i("FCM","수신 메시지:" + remoteMessage.getData().toString());

        // 앱상에서 현재까지 읽지 않은 메시지의 개수
        int count = StorageHelper.getInstance().getInt(this, "APP_NOREAD_MSG");
        // 벳지
        Intent intent = new Intent ("android.intent.action.BADGE_COUNT_UPDATE");
        intent.putExtra("badge_count", count+1);
        intent.putExtra("badge_count_package_name", getPackageName());
        intent.putExtra("badge_count_class_name", getLauncherClassName(this));
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1){
            intent.setFlags(0x00000020);
        }
        sendBroadcast(intent);
        // 카운트 증가 저장
        StorageHelper.getInstance().setInt(this, "APP_NOREAD_MSG", count+1);

        // 알림처리
        // 노티
        sendNotification(remoteMessage.getData().toString());
        // 팝업처리
        Intent popupIntent = new Intent(this, ShowPopupActivity.class);
        popupIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);    // 새로 가동됨(중요!!!!) 명시하지 않음 앱 죽는다
        popupIntent.putExtra("FCM", remoteMessage);
        startActivity(popupIntent);
    }

/*
       Log.getInstance().log("From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.getInstance().log("Message data payload: key1 : " + remoteMessage.getData().get("key1"));
            Log.getInstance().log("Message data payload: key2 : " + remoteMessage.getData().get("key2"));
        }
        if (remoteMessage.getNotification() != null) {
            Log.getInstance().log("Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

    // [END receive_message]
    // 안테나 역할
    // ex) 카톡 알림 받을지 안받을지
    private void sendNotification(String messageBody) {
        // 노티 번호
        int msgID = (int)(Math.random()*100);
        // 구성된 노티를 작동시키는 매니저 역할
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 노티를 누르면 작동할 액티비티를 지정
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("NOTI_ID", msgID);      // 노티를 제거 시 필요한 번호
        intent.putExtra("FCM", messageBody);    // 푸시내용을 액티비티로 전달
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        // 젤리빈 4.1 기준으로 이전과 이후로 나누어 진다.
        if (Build.VERSION.SDK_INT >= JELLY_BEAN) {
            // 신규 방식
        }else{
            // 구방식
        }

        // NotificationCompat를 사용하면 4.1 전후로 같은 스타일로 적용이 가능함
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setTicker("잠시 나타나는 내용임. 노티피케이션에 보임")
                .setProgress(100, 50, false)
                .setSmallIcon(R.mipmap.ic_launcher) // 앱아이콘 ,(채팅 -> 프로필 사진)
                .setContentTitle("FCM Message")     // 알림 제목
                .setContentText(messageBody)        // 내용
                //.setStyle(new NotificationCompat.BigPictureStyle()
                //                .bigPicture(BitmapFactory.)
                //                .bigLargeIcon())    // 빅사진 및 빅 아이콘 설정
                .addAction(android.R.drawable.ic_menu_share, "공유", pendingIntent)   // 버튼누르면 activity 구동
                .setAutoCancel(true)                // 자동 삭제
                .setSound(defaultSoundUri)          // 알림이 발생하면 소리를 낸다.
                .setVibrate(new long[] {500, 100, 500, 100})    // 진동
                .setSmallIcon(R.mipmap.ic_launcher) //앱아이콘
                .setContentTitle("FCM Message")     //제목
                .setContentText(messageBody)        //내용
                .setAutoCancel(true)                //자동삭제
                .setSound(defaultSoundUri)          //알림이 왔을때 소리를 낸다.
                .setContentIntent(pendingIntent);   // 알림을 누르면 구동될 액티비티 지정

        // 노티 발생 : 0 => 노티 번호
        notificationManager.notify(msgID, notificationBuilder.build());
    }

    //class Name 획득
    public String getLauncherClassName(Context context){
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(getPackageName());

        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent,0);

        if(resolveInfos != null && resolveInfos.size()>0){
            Log.i("FCM",resolveInfos.get(0).activityInfo.name);
            return resolveInfos.get(0).activityInfo.name;
        }
        return "";
    }
}