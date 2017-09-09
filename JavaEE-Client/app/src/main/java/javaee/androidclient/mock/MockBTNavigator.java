package javaee.androidclient.mock;

import android.content.Context;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.Identifier;

import java.util.ArrayList;
import java.util.List;

import javaee.androidclient.BTNavigator;
import javaee.androidclient.R;
import javaee.androidclient.rest.RESTManager;

/**
 * Mocks bluetooth data
 * Inserts custom data into Altbeacon callback methods
 */
public class MockBTNavigator extends BTNavigator{

    //private Map<String, Double> mockBTResults;
    private List<Beacon> mockBT;

    /**
     * Initialises data from resourcestrings
     * @param context
     * @param restManager
     */
    public MockBTNavigator(Context context, RESTManager restManager) {
        super(context, restManager);

        mockBT = new ArrayList<Beacon>();

        String[] idsFromResource = context.getResources().getStringArray(R.array.mock_beacons_ids);
        String[] distancesFromResource = context.getResources().getStringArray(R.array.mock_beacons_distances);

        for(int i = 0; i < idsFromResource.length; i++) {
            String id = idsFromResource[i];
            double distance = Double.valueOf(distancesFromResource[i]);

            if(distance > 0 ) {
                MockBeacon mb = new MockBeacon();
                mb.setId(id);
                mb.setDistance(distance);
                mockBT.add(mb);
            }
        }
    }

    /**
     * Injects mock data into Ranging-Callback
     */
    public void mockLocating() {
        for(int i = 0; i< 5; i++) {
            didRangeBeaconsInRegion(mockBT, null);
        }
    }

    /**
     * Class used for mocking a Beacon from AltBeacon library
     */
    private class MockBeacon extends Beacon {
        private String id;
        private double distance;

        private class MockIdentifier extends Identifier  {
            private String string;

            protected MockIdentifier(byte[] value) {
                super(value);
            }

            public void setString(String string) {
                this.string = string;
            }

            @Override
            public String toString() {
                return string;
            }
        }

        @Override
        public Identifier getId1() {
            MockIdentifier mi = new MockIdentifier(new byte[0]);
            mi.setString(id);
            return mi;
        }

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        @Override
        public List<Identifier> getIdentifiers() {
            ArrayList<Identifier> fids = new ArrayList<Identifier>();
            fids.add(new MockIdentifier(new byte[0]));
            return fids;
        }
    }
}
