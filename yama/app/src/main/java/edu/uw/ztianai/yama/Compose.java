package edu.uw.ztianai.yama;

import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * Created by Tianai Zhao on 16/4/23.
 * Write and send a message to a specific recipient
 */
public class Compose extends AppCompatActivity{

    public static final String ACTION_SMS_SENT = "edu.uw.ztianai.yama.ACTION_SMS_SENT";
    private static final int SMS_SENT_CODE = 1;
    private static final int PICK_CONTACT_REQUEST = 1;

    private static final String TAG = "Compose";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Button send = (Button)findViewById(R.id.btnSend);
        Button contact = (Button)findViewById(R.id.btnSearch);
        FloatingActionButton mail = (FloatingActionButton) findViewById(R.id.fabMail);

        //On click listener to find contact information
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
                pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
            }
        });

        //On click listener for sending a message
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText inputRecipient = (EditText)findViewById(R.id.recipient);
                EditText inputMessage = (EditText)findViewById(R.id.message);

                String recipient = inputRecipient.getText().toString();
                String message = inputMessage.getText().toString();

                Intent intent = new Intent(ACTION_SMS_SENT);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Compose.this, SMS_SENT_CODE, intent, 0);

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(recipient, null, message, pendingIntent, null);
                }catch (Exception e){
                }


                //Reset fields to empty so that user can send another message
                inputMessage.setText("");
                inputRecipient.setText("");
            }
        });

        //On click listener for user to see all the received messages
        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Compose.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Getting phone number back from contacts
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request to respond
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Uri contactUri = data.getData();
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                EditText recipient = (EditText)findViewById(R.id.recipient);
                recipient.setText(number);
            }
        }
    }
}
