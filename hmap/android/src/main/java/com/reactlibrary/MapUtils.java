package com.reactlibrary;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.model.BitmapDescriptor;
import com.huawei.hms.maps.model.BitmapDescriptorFactory;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.maps.model.Marker;
import com.huawei.hms.maps.model.MarkerOptions;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class MapUtils {
    public static final String TAG = "HMSMapUtils";

    /**
     * create a marker and adds it to the map
     *
     * @param id      the order of the marker, used for navigating the marker from javascript
     * @param lat     latitude of the marker
     * @param lng     longitude of the marker*
     * @param title   title of the marker to be send back to javascript side
     * @param snippet marker description to be sent back to javascript side
     */
    public static void createMarker(HuaweiMap huaweiMap,
                                    Map<Integer, Marker> markers,
                                    BitmapDescriptor defaultIcon,
                                    int id,
                                    double lat,
                                    double lng,
                                    String title,
                                    String snippet,
                                    String imageUrl) {

        if (huaweiMap == null) return;

        MarkerOptions m = new MarkerOptions();

        m.position(new LatLng(lat, lng));
        m.title(title);
        m.snippet(snippet);

        if (imageUrl == null || imageUrl.length() < 3) {
            if (defaultIcon != null){
                m.icon(defaultIcon);
            }
            Marker marker = huaweiMap.addMarker(m);
            marker.setTag(id);
            markers.put(id, marker);
        } else {
            new Thread(() -> {
                BitmapDescriptor image = markerIconFromUrl(imageUrl);
                m.icon(image);
                Marker marker = huaweiMap.addMarker(m);
                marker.setTag(id);
                markers.put(id, marker);
            }).start();
        }

    }

    public void createMarker(HuaweiMap huaweiMap,
                             Map<Integer, Marker> markers,
                             BitmapDescriptor defaultIcon,
                             double lat,
                             double lng) {

        createMarker(
                huaweiMap,
                markers,
                defaultIcon,
                -1,
                lat,
                lng,
                " title! ",
                "created from long press",
                null );

    }


    /**
     * downloads the bitmap from the link and scaling it down
     *
     * @param link link to image (marker icon)
     * @return bitmapDescriptor to be used by the map the show the icon as marker in the map
     */
    public static BitmapDescriptor markerIconFromUrl(String link) {
        Log.d(MapHmsManager.TAG , "makeIconFromLink: " + link)  ;
        if ("default".equals(link)) {
            return null;
        }
        try {//dimens : 250 * 235
            URL url = new URL(link);

            Bitmap b  = null ;
            try{
                 b = BitmapFactory.decodeStream(url.openConnection().getInputStream());

            }catch (Exception e){
                //

               }

            if(b == null ){
                return null ;
            }
            //b = Bitmap.createScaledBitmap(b, MARKER_WIDTH, MARKER_HEIGHT, false);
            b = resize(b, Constants.MARKER_WIDTH, Constants.MARKER_HEIGHT);

            Log.d(TAG, "Utils > bitmap info " + b.getByteCount() + " " + b.getHeight() + "X" + b.getWidth());
            return BitmapDescriptorFactory.fromBitmap(b);
        } catch (Exception e) {
            Log.d(TAG, "failed to get bitmap " + e.toString());
        }
        return null;
    }

    /**
     * resize a map while keeping the aspect ratio, the image dimension will equal
     * or be smaller than the specified dimension
     *
     * @param image     the bitmap to be resized
     * @param maxWidth  target width
     * @param maxHeight target height
     * @return new scaled down bitmap
     */
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

}
