package javaee.androidclient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;

import com.ortiz.touch.TouchImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Extends the TouchImageView class by markers for beacons and position
 *
 * @author Henrik Drefs
 * @author Til Koke
 */
public class MapImageView extends TouchImageView {

    private static final BitmapFactory.Options bfOptions = new BitmapFactory.Options();

    static {
        bfOptions.inScaled = false;
    }

    private Bitmap positionMarker;
    private Bitmap infoMarker;
    private Bitmap beaconMarker;

    private Canvas overlayCanvas;
    private Bitmap overlayBmp;

    public static final int MARKER_SIZE = 8;
    private Paint paint = new Paint();

    private List<MapMarker> markersToDraw;
    private MapMarker oldPosMarker;

    public MapImageView(Context context) {
        super(context);
        loadBitmaps();
    }

    public MapImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        loadBitmaps();
    }

    public MapImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        loadBitmaps();

    }

    private void loadBitmaps() {
        positionMarker = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.marker_position, bfOptions);
        infoMarker = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.marker_info, bfOptions);
        beaconMarker = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.marker_beacon, bfOptions);

        positionMarker = Bitmap.createScaledBitmap(positionMarker, MARKER_SIZE, MARKER_SIZE, false);
        beaconMarker = Bitmap.createScaledBitmap(beaconMarker, MARKER_SIZE, MARKER_SIZE, false);

        Bitmap tmpBackGroundBmp = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.floor0_eg_lowres);
        int overlayBmpWidth = tmpBackGroundBmp.getWidth();
        int overlayBmpHeight = tmpBackGroundBmp.getHeight();

        overlayBmp = Bitmap.createBitmap(overlayBmpWidth, overlayBmpHeight, Bitmap.Config.ARGB_8888);
        overlayCanvas = new Canvas(overlayBmp);

        markersToDraw = new ArrayList<MapMarker>();
    }

    private void findFinalSizeBeforeDraw() {
        final ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                MapImageView.this.getViewTreeObserver().removeOnPreDrawListener(this);
                overlayBmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                overlayCanvas = new Canvas(overlayBmp);
                return true;
            }
        };
        MapImageView.this.getViewTreeObserver().addOnPreDrawListener(preDrawListener);
    }

    /**
     * set the position of the position marker
     * throws the old marker out
     * Params in Pixels!
     * @param x new absolute x
     * @param y new absolute y
     */
    public void setPosMarker(float x, float y) {
        MapMarker newPosMarker = new MapMarker(positionMarker, x, y);
        markersToDraw.remove(oldPosMarker);
        markersToDraw.add(newPosMarker);
        oldPosMarker = newPosMarker;
        // onDraw is called afterwards
    }

    /**
     * add a new Beacon Marker
     * Params in Pixels!
     * @param x new absolute x
     * @param y new absolute y
     */
    public void addBeaconMarker(float x, float y) {
        markersToDraw.add(new MapMarker(beaconMarker, x, y));
    }

    /**
     * adds the marker drawing to onDraw()-method
     *
     * @param canvas canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        overlayCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        //overlayCanvas.drawBitmap(positionMarker, posMarkerX, posMarkerY, null);
        for(MapMarker marker : markersToDraw) {
            overlayCanvas.drawBitmap(marker.getImage(), marker.getX(), marker.getY(), null);
        }
        canvas.drawBitmap(overlayBmp, getImageMatrix(), paint);
    }

    /**
     * decode bitmap from resource without scaling
     *
     * @param resId resource id
     */
    @Override
    public void setImageResource(int resId) {
        Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), resId, bfOptions);
        setImageBitmap(bitmap);
    }

    private class MapMarker {
        private Bitmap image;
        private float x;
        private float y;

        public MapMarker(Bitmap image, float x, float y) {
            this.image = image;
            this.x = x;
            this.y = y;
        }

        public Bitmap getImage() {
            return image;
        }

        public void setImage(Bitmap image) {
            this.image = image;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }
    }
}
