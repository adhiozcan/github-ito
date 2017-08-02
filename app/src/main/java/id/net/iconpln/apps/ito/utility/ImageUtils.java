package id.net.iconpln.apps.ito.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Ozcan on 30/03/2017.
 */

public class ImageUtils {
    private static final String TAG                 = ImageUtils.class.getSimpleName();
    private static final int    IMG_WIDTH           = 320;
    private static final int    IMG_HEIGHT          = 480;
    private static       int    COMPRESSION_QUALITY = 100;

    public static String getURLEncodeBase64(Context context, Uri photo) {
        if (photo == null) return null;

        String urlEncode = null;
        String base64    = getEncoded64ImageString(context, photo);
        try {
            if (base64 != null) {
                urlEncode = URLEncoder.encode(getEncoded64ImageString(context, photo), "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return urlEncode;
    }

    private static String getEncoded64ImageString(Context context, Uri photo) {
        // check if image is still default image.
        //if (imgResource.getTag().equals("default_image")) return null;

        Log.d(TAG, "\n[Starting]-------------------------------------------------");
        //Log.d(TAG, "Image from source id : " + imgResource.getId());
        Log.d(TAG, "[Step 0/3] Preparing");
        Bitmap bitmap = CameraUtils.uriToBitmap(context, Uri.parse("file://" + photo));

        bitmap = scaleDown(bitmap, IMG_HEIGHT, IMG_WIDTH);
        bitmap = compressImage(bitmap);
        byte[] imageToPost = getBitmapByteArray(bitmap);
        return Base64.encodeToString(imageToPost, Base64.NO_WRAP);
    }

    private static Bitmap scaleDown(Bitmap bitmap, int height, int width) {
        Log.d(TAG, "[Step 1/3] Scaling");
        return Bitmap.createScaledBitmap(bitmap, height, width, true);
    }

    private static Bitmap compressImage(Bitmap bitmap) {
        Log.d(TAG, "[Step 2/3] Compression");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteFormat = stream.toByteArray();

        for (int i = 0; i < 12; i++) {
            ByteArrayOutputStream streamCompressed = new ByteArrayOutputStream();
            if (byteFormat.length > 21048) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, streamCompressed);
                byteFormat = streamCompressed.toByteArray();
                COMPRESSION_QUALITY -= 5;

                Log.d(TAG, "\t|---Compress on Iteration " + i + " with compression level "
                        + COMPRESSION_QUALITY + "\t\tResult size " + byteFormat.length / 1024 + " kb");
            }
        }

        InputStream           inputStream = new ByteArrayInputStream(byteFormat);
        BitmapFactory.Options option      = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeStream(inputStream, null, option);
        return bitmap;
    }

    private static byte[] getBitmapByteArray(Bitmap bitmap) {
        Log.d(TAG, "[Step 3/3] Get The Result");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, stream);
        byte[] byteFormat = stream.toByteArray();
        Log.d(TAG, "\t|---Final Size " + byteFormat.length / 1024 + "kb");
        resetCompressionQuality();
        return byteFormat;
    }

    private static void resetCompressionQuality() {
        ImageUtils.COMPRESSION_QUALITY = 100;
    }

    public static Bitmap addWatermark(Bitmap src, String kodePetugas, String tanggal, String latitude, String longitude) {
        int    w      = src.getWidth();
        int    h      = src.getHeight();
        Bitmap result = Bitmap.createBitmap(w, h, src.getConfig());

        Paint bgPaint = new Paint();
        bgPaint.setColor(Color.parseColor("#AA000000"));  //transparent black,change opacity by changing hex value "AA" between "00" and "FF"

        Paint paint = new Paint();
        paint.setTextSize(7);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);

        //should draw background first,order is important
        int   left   = 0;
        int   right  = w;
        int   bottom = h;
        float top    = (float) (bottom - (h * .25));

        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(src, 0, 0, null);
        canvas.drawRect(left, top, right, bottom, bgPaint);
        canvas.drawText(kodePetugas + " / " + tanggal, 4, top + 12, paint);
        canvas.drawText("x : " + latitude, 4, top + 22, paint);
        canvas.drawText("y : " + longitude, 4, top + 32, paint);
        return result;
    }

    public static void saveImage(String path, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
