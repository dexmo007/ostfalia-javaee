package javaee.androidclient;

import android.content.Context;
import android.util.Pair;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javaee.androidclient.rest.RESTManager;
import walker.blue.tri.lib.Trilateration;

/**
 * Created by Til Koke on 01/06/2016.
 * Implements Triangulation and various other position related calculation
 * returns values to listeners
 * all returned values are absolute in regards to lat and lon
 * of upperLeft and lowerRight corner
 */
public class BTNavigator implements RangeNotifier {

    private static final int ITERATIONS_AVERAGE = 5;

    //Map = <Beacon-ID, Beacon-Range as Double>
    private List<Map<String, Double>> scanResults;
    private Map<String, Double> averagedResults;
    private RESTManager restManager;

    private double upperLeftLat;
    private double upperLeftLon;
    private double lowerRightLat;
    private double lowerRightLon;

    private final static int LATITUDE_INDEX = 0;
    private final static int LONGITUDE_INDEX = 1;
    private final static int HEIGHT_INDEX = 2;

    private int closestRoom = 0;

    private List<TrilaterationListener> resultListeners;

    public BTNavigator(Context context, RESTManager restManager) {
        scanResults = new LinkedList<Map<String, Double>>();
        averagedResults = new HashMap<String, Double>();
        this.restManager = restManager;

        upperLeftLat = Double.valueOf(context.getResources().getString(R.string.map_geo_bounds_upperleft_lat));
        upperLeftLon = Double.valueOf(context.getResources().getString(R.string.map_geo_bounds_upperleft_lon));
        lowerRightLat = Double.valueOf(context.getResources().getString(R.string.map_geo_bounds_lowerright_lat));
        lowerRightLon = Double.valueOf(context.getResources().getString(R.string.map_geo_bounds_lowerright_lon));

        resultListeners = new ArrayList<TrilaterationListener>();
    }

    //pos 0 = latitude
    //pos 1 = longitude
    //pos 2 = height
    private double[][] calculateLocation(Map<String, Double> bluetoothBeacons, List<javaee.androidclient.rest.Beacon> dataBeacons) {
        List<Double> distancesList = new ArrayList<Double>();
        List<Double[]> positionsList = new ArrayList<Double[]>();

        for (Map.Entry<String, Double> e : bluetoothBeacons.entrySet()) {
            for (javaee.androidclient.rest.Beacon dB : dataBeacons) {
                if (e.getKey().equals(dB.getBeaconId())) {
                    distancesList.add(e.getValue());
                    Double[] positions = new Double[3];
                    positions[0] = normalize(dB.getLatitude(), upperLeftLat, lowerRightLat);
                    positions[1] = normalize(dB.getLongitude(), upperLeftLon, lowerRightLon);
                    positions[2] = 0.0;
                    positionsList.add(positions);
                }
            }
        }

        double[] distancesArray = new double[distancesList.size()];
        for (int i = 0; i < distancesArray.length; i++) {
            distancesArray[i] = distancesList.get(i);
        }

        System.out.println(distancesList.size());
        System.out.println(positionsList.size());

        double[][] positionsArray = new double[positionsList.size()][];
        for (int i = 0; i < distancesArray.length; i++) {
            positionsArray[i] = new double[3];
            positionsArray[i][LATITUDE_INDEX] = positionsList.get(i)[LATITUDE_INDEX];
            positionsArray[i][LONGITUDE_INDEX] = positionsList.get(i)[LONGITUDE_INDEX];
            positionsArray[i][HEIGHT_INDEX] = positionsList.get(i)[HEIGHT_INDEX];
        }

        try{
            return new Trilateration().calculateLocation(distancesArray, positionsArray);
        } catch (Exception e) {
            return null;
        }
    }

    public Set<String> getScannedIds() {
        Set<String> uniqueIds = new HashSet<>();
        for(Map<String, Double> result: scanResults) {
            uniqueIds.addAll(result.keySet());
        }
        return uniqueIds;
    }

    private Map<String, Double> averageOverScannedBeacons(List<Map<String, Double>> scanResults) {
        Map<String, List<Double>> allIdsWithDistances = new HashMap<String, List<Double>>();

        /*
        map ID to every distance, that has been found yet
         */
        for(Map<String, Double> resultMap: scanResults) {
            for(Map.Entry<String, Double> resultEntry : resultMap.entrySet()) {
                String id = resultEntry.getKey();
                List<Double> distanceList = allIdsWithDistances.get(id);
                if(allIdsWithDistances.containsKey(id) == false) {
                    distanceList = new ArrayList<Double>();
                    allIdsWithDistances.put(id, distanceList);
                }
                distanceList.add(resultEntry.getValue());
            }
        }

        /*
        loop over map and calculate average distance
         */
        Map<String, Double> averagesMap = new HashMap<String, Double>();
        for(Map.Entry<String, List<Double>> idWithDistances : allIdsWithDistances.entrySet()) {
            double sum = 0;
            for(Double distance : idWithDistances.getValue()) {
                sum += distance;
            }
            double avg = sum / idWithDistances.getValue().size();
            averagesMap.put(idWithDistances.getKey(), avg);
        }

        return averagesMap;
    }

    /**
     * Normalizes input parameter
     * @param input
     * @return from 0 to 1
     */
    private double normalize(double input, double origMin, double origMax) {
        return (input - origMin) / (origMax - origMin);
    }

    protected void startCalculation() {

        if(scanResults.size() >= ITERATIONS_AVERAGE) {
            averagedResults = averageOverScannedBeacons(scanResults);
            double[][] locResults = calculateLocation(averagedResults, restManager.getBeacons());

            if(locResults==null) {
                return;
            }

            for(double[] rs : locResults) {
                System.out.println(Arrays.toString(rs));
            }

            double latitude = locResults[LATITUDE_INDEX][0];
            double longitude = locResults[LONGITUDE_INDEX][0];

            if(longitude == Double.NaN ||latitude == Double.NaN) {
                return;
            }

            double normalizedY = normalize(latitude, upperLeftLat, lowerRightLat);
            double normalizedX = normalize(latitude, upperLeftLon, lowerRightLon);

            System.out.println(resultListeners.toString());

            for(TrilaterationListener l : resultListeners) {
                l.foundPosition(latitude, longitude);
            }
        }
    }

    /**
     * puts all IDs into one string
     * @param b
     * @return
     */
    private String getFullBTID(Beacon b) {
        String idComplete = b.getId1().toString();
        if(b.getIdentifiers().size()>1) {
            idComplete += " " + b.getId2().toString();
        }
        if(b.getIdentifiers().size() > 2) {
            idComplete += " " + b.getId3().toString();
        }
        return idComplete;
    }

    /**
     * Gets called by ALTBeacon Library about every 1 second
     * Multiple functions:
     * Get closest room from metadata
     * Gather distances and starts trilateration
     * Returns list of all found beacon position to everybody who cares
     * @param beacons
     * @param region
     */
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        List<javaee.androidclient.rest.Beacon> restBeacons = restManager.getBeacons();

        if(restBeacons != null) {
            double closestDistance = Double.MAX_VALUE;
            Beacon closestBTBeacon = null;
            for(Beacon b : beacons) {
                if(b.getDistance() < closestDistance) {
                    closestDistance = b.getDistance();
                    closestBTBeacon = b;
                }
            }
            if(closestBTBeacon != null) {
                for(javaee.androidclient.rest.Beacon restBeacon : restBeacons) {
                    if(restBeacon.getBeaconId().equals(getFullBTID(closestBTBeacon))) {
                        if(restBeacon.getRoom() != null) {
                            closestRoom = restBeacon.getRoom();
                        }
                    }
                }
            }
        }

        //start actual Triangulation ONLY if at least 3 beacons are found
        if (beacons.size() >= 2) {
            Map<String, Double> idsAndDistances = new HashMap<String, Double>();
            for (Beacon b : beacons) {
                idsAndDistances.put(getFullBTID(b), b.getDistance()*0.2);
            }


            scanResults.add(idsAndDistances);
            startCalculation();


            //inform listeners that we found beacons, for drawing etc.
            if(restBeacons != null) {
                Set<String> uniqueIds = getScannedIds();
                List<Pair<Double, Double>> coords = new ArrayList<Pair<Double, Double>>();
                for(javaee.androidclient.rest.Beacon b : restBeacons) {
                    if(uniqueIds.contains(b.getBeaconId())) {
                        double normY = normalize(b.getLatitude(), upperLeftLat, lowerRightLat);
                        double normX = normalize(b.getLongitude(), upperLeftLon, lowerRightLon);
                        coords.add(new Pair<Double, Double>(normX, normY));
                    }
                }
                for(TrilaterationListener l : resultListeners) {
                    l.foundBeaconsAt(coords);
                }
            }
        }
    }

    /**
     * based on beacon Metadata
     * from closest beacon
     * @return
     */
    public int getClosestRoom() {
        return closestRoom;
    }

    public void addResultListener(TrilaterationListener listener) {
        resultListeners.add(listener);
    }

    /**
     * Simple Callback method that gives result
     * from 0-1
     */
    public interface TrilaterationListener {
        void foundPosition(double x, double y);
        void foundBeaconsAt(List<Pair<Double, Double>> coords);
    }
}
