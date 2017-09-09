package javaee.androidclient;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

/**
 * Main start up activity for the app
 *
 * @author Til Koke
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openNavigation(View view) {
        Intent intent = new Intent(this, NavigationActivity.class);
        startActivity(intent);
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    /**
     * Used by multiple acitivites to show Alert/Error on given View
     * @param errorMsg
     * @param parent
     */
    public static void displayError(String errorMsg, View parent) {
        displayError(errorMsg, parent.getContext());
    }

    /**
     * Error Message as popup
     * @param errorMsg
     * @param context
     */
    public static void displayError(String errorMsg, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Error occured!");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setMessage(errorMsg);
        alert.show();
    }

    public static void displayInfo(String msg, Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Information");
        alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.setIcon(android.R.drawable.ic_dialog_info);
        alert.setMessage(msg);
        alert.show();
    }

}
