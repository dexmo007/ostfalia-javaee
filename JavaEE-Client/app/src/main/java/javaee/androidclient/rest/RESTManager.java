package javaee.androidclient.rest;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

import javaee.androidclient.MainActivity;

/**
 * Handles the GET requests and manages beacons
 *
 * @author Henrik Drefs
 */

public class RESTManager {

    private Context context;
    private RequestQueue queue;

    private List<Beacon> beacons = null;
    private List<Lecture> lectures = null;

    private String serverIP = "ec2-52-34-229-226.us-west-2.compute.amazonaws.com:28080/ServerProject";
    private String beaconsPath = "/api/0.1/tokens/list";
    private String lecturesPath = "/api/0.1/lectures/list";

    /**
     * Start a new Volley-Request queze
     * @param context Applicationcontext
     */
    public RESTManager(Context context) {
        this.context = context;
        this.queue = Volley.newRequestQueue(context);
    }

    /**
     * this adds the GET requestBeacons to the requestBeacons queue, event is triggered on response
     */
    public void requestBeacons() {
        BeaconsResponseListener beaconsResponseListener = new BeaconsResponseListener();
        String url = "http://" + serverIP + beaconsPath;
        StringRequest request = new StringRequest(Request.Method.GET, url, beaconsResponseListener, beaconsResponseListener);
        queue.add(request);
    }

    /**
     * getter for the beacons if loaded, otherwise beacons are requested
     *
     * @return null or list of beacons
     */
    public List<Beacon> getBeacons() {
        if (beacons == null) {
            requestBeacons();
        }
        return beacons;
    }

    /**
     * this adds the GET requestBeacons to the requestBeacons queue, event is triggered on response
     */
    public void requestLectures() {
        LecturesResponseListener lecturesResponseListener = new LecturesResponseListener();
        String url = "http://" + serverIP + lecturesPath;
        StringRequest request = new StringRequest(Request.Method.GET, url, lecturesResponseListener, lecturesResponseListener);
        queue.add(request);
    }

    public List<Lecture> getLectures() {
        if (lectures == null) {
            requestBeacons();
        }
        return lectures;
    }

    public String getServerIP() {
        return serverIP;
    }

    public void setServerIP(String serverIP) {
        this.serverIP = serverIP;
    }

    public String getBeaconsPath() {
        return beaconsPath;
    }

    public void setBeaconsPath(String beaconsPath) {
        this.beaconsPath = beaconsPath;
    }

    public String getLecturesPath() {
        return lecturesPath;
    }

    public void setLecturesPath(String lecturesPath) {
        this.lecturesPath = lecturesPath;
    }

    /**
     * Private class implementing onResponse and onError callback methods
     * Deserializes beacon data if succesfull
     */
    private class BeaconsResponseListener implements Response.Listener<String>, Response.ErrorListener {
        @Override
        public void onResponse(String response) {
            try {
                beacons = JsonUtil.deserializeBeaconList(response);
            } catch (Exception e) {
                MainActivity.displayError("Json Deserialization Error", context);
            }
            //MainActivity.displayInfo("Beacons received!\n" + beacons, context);
        }

        /**
         * Displays error html response as error message
         * @param error
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            MainActivity.displayError(error.getMessage(), context);
        }
    }


    /**
     * Private class implementing onResponse and onError callback methods
     * Deserializes lecture data if successful
     */
    private class LecturesResponseListener implements Response.Listener<String>, Response.ErrorListener {
        /**
         * Takes REST response and turns it from json into lectures
         * @param response
         */
        @Override
        public void onResponse(String response) {
            try {
                lectures = JsonUtil.deserializeLectureList(response);
            } catch (Exception e) {
                MainActivity.displayError("Json Deserialization Error", context);
            }
            //MainActivity.displayInfo("Lectures received!\n" + lectures, context);
        }

        /**
         * Takes REST response and displays Error
         * @param
         */
        @Override
        public void onErrorResponse(VolleyError error) {
            MainActivity.displayError(error.getMessage(), context);
        }
    }
}
