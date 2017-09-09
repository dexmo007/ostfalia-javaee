package javaee.androidclient;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.suitebuilder.annotation.Smoke;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaee.androidclient.mock.MockBTNavigator;
import javaee.androidclient.mock.MockRESTManager;
import javaee.androidclient.rest.Lecture;
import javaee.androidclient.rest.RESTManager;
import walker.blue.tri.lib.Trilateration;

/**
 * Activity displaying the map and navigation
 *
 * @author Til Koke, Henrik Drefs
 */
public class NavigationActivity extends AppCompatActivity implements BeaconConsumer, BTNavigator.TrilaterationListener {

    public static boolean MOCK_BLUETOOTH = false;
    public static boolean MOCK_REST = false;

    private Region rangingRegion;
    private MapController mapController;
    private BeaconManager beaconManager;

    private RESTManager restManager;
    private MapImageView imageView;

    private BTNavigator btNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = (MapImageView) findViewById(R.id.navImageView);
        mapController = new MapController(getResources(), imageView, (TextView) findViewById(R.id.navigationTextView));

        // initialize REST manager and request beacons
        restManager = new RESTManager(this);
        restManager.requestBeacons();
        restManager.requestLectures();
    }

    @Override
    protected void onDestroy() {
        if (beaconManager != null) {
            beaconManager.unbind(this);
        }
        super.onDestroy();
    }

    public void btnMoveUp(View view) {
        mapController.changeFloor(+1);
    }

    public void btnMoveDown(View view) {
        mapController.changeFloor(-1);
    }

    /**
     * Activates Bluetooth if nece
     */
    public void btnFindPosition(View view) {
        // for testing purposes
        //mapController.setPosMarkerRelative(((float) Math.random()), (float) Math.random());
        if (MOCK_BLUETOOTH) {
            if(btNavigator==null) {
                btNavigator = new MockBTNavigator(this, restManager);
                btNavigator.addResultListener(this);
            }
            ((MockBTNavigator)btNavigator).mockLocating();

        } else {

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if (mBluetoothAdapter == null) {
                MainActivity.displayError(getString(R.string.msg_no_bluetooth_error), view);
            } else {
                if (mBluetoothAdapter.isEnabled()) {
                    btNavigator = new BTNavigator(getApplicationContext(), restManager);
                    setupBeaconManager();
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 1);
                }
            }
        }
    }

    private void setupBeaconManager() {
        //see https://altbeacon.github.io/android-beacon-library/samples.html
        beaconManager = BeaconManager.getInstanceForApplication(this);
        //beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        //String literals matching every major beacon
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-20v"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    /**
     * Display info based on closest Beacon with room number meta data
     * @param view
     */
    public void btnRoomInfo(View view) {
        if(btNavigator != null) {
            if(btNavigator.getClosestRoom() != 0) {
                int closestRoom = btNavigator.getClosestRoom();
                Calendar cal = Calendar.getInstance();
                String weekday = "";
                for(Lecture lecture : restManager.getLectures()) {
                    if(lecture.getRoom().equals(String.valueOf(closestRoom))
                            && lecture.getDay().equals(weekday)) {
                        TextView infoText = (TextView) findViewById(R.id.roomInfoText);
                        infoText.setVisibility(View.VISIBLE);
                        infoText.setText(lecture.getTutor() + "\n" + lecture.getSubject());
                    }
                }
            }
        }
    }

    /**
     * Activated if Beaconmanager is bound successfully
     * Initialises Bluetoothnavigator
     */
    @Override
    public void onBeaconServiceConnect() {
        beaconManager.setRangeNotifier(btNavigator);
        btNavigator.addResultListener(this);
        rangingRegion = new Region("myRangingUniqueId", null, null, null);
        try {
            beaconManager.startRangingBeaconsInRegion(rangingRegion);
        } catch (RemoteException e) {
            MainActivity.displayError(getString(R.string.msg_no_ranging_error), this);
        }
    }

    /**
     * Callback from Navigator, place position
     * @param x relative Coordinates
     * @param y relative Coordinates
     */
    @Override
    public void foundPosition(double x, double y) {
        mapController.setPosMarkerRelative((float)x, (float)y);
    }

    /**
     * Callback from Navigator, place BeaconMarkers on Map
     * @param coords
     */
    @Override
    public void foundBeaconsAt(List<Pair<Double, Double>> coords) {
        for(Pair<Double, Double> location : coords) {
            //yay casting ist awesome?!
            //System.out.println(location.first);
            //System.out.println(location.second);
            mapController.addBeaconMarkerRelative((float) (double) location.first, (float) (double) location.second);
        }
    }
}
