package javaee.androidclient.mock;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import javaee.androidclient.R;
import javaee.androidclient.rest.Beacon;
import javaee.androidclient.rest.RESTManager;

/**
 * Created by Til Koke on 01/06/2016.
 * Gives some fake Beacons for Testing
 * In case Webserver cant be reached
 */
public class MockRESTManager extends RESTManager {

    private List<Beacon> mockData;

    /**
     * Data will be pulled from "strings.xml"
     * @param context
     */
    public MockRESTManager(Context context) {
        super(context);
        String[] idsFromResource = context.getResources().getStringArray(R.array.mock_beacons_ids);
        String[] latsFromResource = context.getResources().getStringArray(R.array.mock_beacons_latitudes);
        String[] lonsFromResource = context.getResources().getStringArray(R.array.mock_beacons_longitudes);

        mockData = new ArrayList<Beacon>();

        for(int i = 0; i < idsFromResource.length; i++) {
            Beacon mockBeacon = new Beacon();
            mockBeacon.setBeaconId(idsFromResource[i]);
            mockBeacon.setLatitude(Double.valueOf(latsFromResource[i]));
            mockBeacon.setLongitude(Double.valueOf(lonsFromResource[i]));
            mockBeacon.setHeight(2.0);

            mockData.add(mockBeacon);
        }
    }

    @Override
    public List<Beacon> getBeacons() {
        return mockData;
    }
}
