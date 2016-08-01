package edu.uw.ztianai.yama;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Telephony;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

/**
 * Created by Tianai Zhao on 16/4/23.
 * Allow user to read the past received messages
 */

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    private static final String TAG = "MAIN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Listens to see if the FloatingActionButton is clicked, if clicked, take user to the compose page
        FloatingActionButton compose = (FloatingActionButton) findViewById(R.id.fab);
        compose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Compose.class);
                startActivity(intent);
            }
        });

        //Initializes the loader
        getLoaderManager().initLoader(0, null, this);

        AdapterView listView = (AdapterView)findViewById(R.id.messageListView);

        //Set up the adapter with necessary information about a message received
        adapter = new SimpleCursorAdapter(this,
                R.layout.list_item,
                null,
                new String[] {Telephony.Sms.Conversations.ADDRESS, Telephony.Sms.Conversations.BODY, Telephony.Sms.Conversations.DATE, Telephony.Sms.Conversations._ID},
                new int[] {R.id.name, R.id.message, R.id.time},
                0);

        //Turn the long format time to a readable format
        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if(columnIndex == cursor.getColumnIndex(Telephony.Sms.Conversations.DATE)) {
                    long date = cursor.getLong(cursor.getColumnIndex(Telephony.Sms.Conversations.DATE));
                    SimpleDateFormat formatter = new SimpleDateFormat("h:mm aa MM-dd-yyyy");
                    String formatDate = formatter.format(date);
                    TextView time = (TextView)view.findViewById(R.id.time);
                    time.setText(formatDate);
                    return true;
                }
                return false;
            }
        });

        listView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = new String[] {Telephony.Sms.Conversations.ADDRESS, Telephony.Sms.Conversations.BODY, Telephony.Sms.Conversations.DATE, Telephony.Sms.Conversations._ID};
        CursorLoader loader = new CursorLoader(getApplicationContext(), Uri.parse("content://sms/inbox"), projection, null, null, null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }


    //Option menu for the application
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}