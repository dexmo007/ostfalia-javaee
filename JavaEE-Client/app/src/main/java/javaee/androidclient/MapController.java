package javaee.androidclient;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.widget.TextView;

/**
 * Handles display and movement control of navigation interface
 *
 * @author Til Koke
 * @author Henrik Drefs
 */
public class MapController {
    // replaced with resource string: resources.getString(R.string.msg_image_loading_error);
//    private static final String MSG_IMAGE_LOADING_ERROR = "Problem loading Bitmap.";
    /**
     * maximum zoom factor of MapImageView
     */
    public static final float MAX_ZOOM = 5f;
    private String[] floorStrings;
    private int currentFloor = 0;
    private int[] floorDrawables = {R.drawable.floor0_eg_lowres, R.drawable.floor1_1og_lowres, R.drawable.floor2_dg_lowres};

    private Resources resources;

    private MapImageView naImageView;
    private TextView naTextView;

    /**
     * Initialises arrays of drawables and strings for easy floor changing
     * Initialises the BitmapRegionDecoder, used to only load necessary part of floor layout
     *
     * @param resources   resources of the app
     * @param naImageView image view to display map
     * @param naTextView  text view for floor
     */
    public MapController(Resources resources, MapImageView naImageView, TextView naTextView) {
        this.resources = resources;
        this.naImageView = naImageView;
        this.naTextView = naTextView;
        floorStrings = resources.getStringArray(R.array.floors_array);

        currentFloor = 0;
        naTextView.setText(floorStrings[0]);

        naImageView.setImageResource(floorDrawables[currentFloor]);
        naImageView.setMaxZoom(MAX_ZOOM);
    }

    /**
     * changes the displayed floor
     *
     * @param direction +1 moves one floor up, -1 moves one floor down
     */
    public void changeFloor(int direction) {
        currentFloor = (currentFloor + direction);
        if (currentFloor < 0) {
            currentFloor = floorDrawables.length - 1;
        }
        currentFloor = currentFloor % floorDrawables.length;

        naImageView.setImageResource(floorDrawables[currentFloor]);
        naTextView.setText(floorStrings[currentFloor]);
    }

    /**
     * Sets position marker in absolute pixel values
     * @param x
     * @param y
     */
    public void setPosMarker(float x, float y) {
        naImageView.setPosMarker(x, y);
    }

    /**
     * sets the position marker using percentages of width and height
     * 0,0 ---- 1,0
     * |         |
     * |         |
     * 0,1 ---- 1,1
     *
     * @param x percentage of width
     * @param y percentage of height
     */
    public void setPosMarkerRelative(@FloatRange(from = 0, to = 1) float x, @FloatRange(from = 0, to = 1) float y) {
        Drawable drawable = naImageView.getDrawable();
        float absX = x * (drawable.getIntrinsicWidth() - MapImageView.MARKER_SIZE);
        float absY = y * (drawable.getIntrinsicHeight() - MapImageView.MARKER_SIZE);
        naImageView.setPosMarker(absX, absY);
    }


    /**
     * Adds a beacon marker in absolute pixel values
     * @param x
     * @param y
     */
    public void addBeaconMarker(float x, float y) {
        naImageView.addBeaconMarker(x, y);
    }

    /**
     * adds a position marker using percentages of width and height
     * 0,0 ---- 1,0
     * |         |
     * |         |
     * 0,1 ---- 1,1
     *
     * @param x percentage of width
     * @param y percentage of height
     */
    public void addBeaconMarkerRelative(@FloatRange(from = 0, to = 1) float x, @FloatRange(from = 0, to = 1) float y) {
        Drawable drawable = naImageView.getDrawable();
        float absX = x * (drawable.getIntrinsicWidth() - MapImageView.MARKER_SIZE);
        float absY = y * (drawable.getIntrinsicHeight() - MapImageView.MARKER_SIZE);
        naImageView.addBeaconMarker(absX, absY);
    }
}










