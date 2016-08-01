package edu.uw.ztianai.yama;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Telephony;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by Tianai Zhao on 16/4/23.
 * Use BroadcastReceiver to notify users with different events
 */
public class MyReceiver extends BroadcastReceiver {

    private static final int NOTIFY_CODE = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == Compose.ACTION_SMS_SENT) {  //If there is a SMS_SENT action
            if(getResultCode() == Activity.RESULT_OK){  //If the result is okay, then notify user with a Toast
                Toast.makeText(context, "Message sent!", Toast.LENGTH_SHORT).show();

            }else{ //If the message didn't send successfully, toast an error message to user
                Toast.makeText(context, "Error sending message", Toast.LENGTH_SHORT).show();
            }

        }else if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){ //If user received a SMS
            Bundle bundle = intent.getExtras();
            if(bundle != null){
                SmsMessage[] messagesArray = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                String address = "";
                String body = "";

                for(SmsMessage message : messagesArray){ //Process all the messages
                    address = message.getDisplayOriginatingAddress(); //get the sender number
                    body = message.getDisplayMessageBody(); //get the message from the sender

                    //Build the notification bar with icon, title and body text
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.notification_icon)
                                    .setContentTitle(address)
                                    .setContentText(body);

                    //Set priority, sound and vibration for the notification
                    mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
                    mBuilder.setVibrate(new long[]{0, 500, 500, 5000});
                    mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

                    //Creates an explicit intent so that user can see all the messages
                    Intent resultIntent = new Intent(context, MainActivity.class);

                    //Stack builder help with navigating backward actions
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(NOTIFY_CODE, mBuilder.build());

                    //Update preferences of the user
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    Boolean reply = prefs.getBoolean("pref_reply", false);
                    String autoMessage = prefs.getString("message", "Message Received.");

                    //If the user prefer to auto reply an message, then send the auto reply message to the sender
                    if(reply){
                        SmsManager sms = SmsManager.getDefault();
                        sms.sendTextMessage(address, null, autoMessage, null, null);
                        Toast.makeText(context, "Auto-Reply Message Sent", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }
}
