package id.net.iconpln.apps.ito.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import id.net.iconpln.apps.ito.helper.MainApplication;

/**
 * Created by Ozcan on 23/03/2017.
 */

public class ImageUtil {

    public static byte[] encodeBase64(String filePath, String fileName, ImageSize imageSize) {
        return encodeBase64(new File(MainApplication.IMAGE_PATH + compressImage(filePath, fileName, imageSize)));
    }

    public static byte[] encodeBase64(File param) {
        //TODO spring are going removed, use android api to replace this method
        byte[] bytes = new byte[(int) param.length()];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(param));
            buf.read(bytes, 0, bytes.length);
            buf.close();
            return Base64.encode(bytes, 0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static File convertBitmapToFile(Bitmap bitmap, String fileName) {
        File imageFile = new File(MainApplication.IMAGE_PATH, fileName);
        OutputStream os = null;
        try {
            if (imageFile.createNewFile()) {
                os = new BufferedOutputStream(new FileOutputStream(imageFile));
                os.flush();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, os);
            }
        } catch (Exception e) {
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return imageFile;
    }

    /**
     * Return image name
     *
     * @param filePath
     * @param fileName
     * @return
     */
    public static String compressImage(String filePath, String fileName, ImageSize imageSize) {
        Bitmap scaledBitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;
//      max Height and width values of the compressed image is taken as 816x612
        float maxHeight = imageSize.height;
        float maxWidth = imageSize.width;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;
            }
        }

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);       //setting inSampleSize value allows to loadTrip a scaled down version of the original image
        options.inJustDecodeBounds = false;     //inJustDecodeBounds set to false to loadTrip the actual bitmap
        options.inPurgeable = true;     //this options allow android to claim the bitmap memory if it runs low on memory
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options);   //loadTrip the bitmap from its path
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        assert scaledBitmap != null;
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, middleX - bitmap.getWidth() / 2, middleY - bitmap.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            } else if (orientation == 3) {
                matrix.postRotate(180);
            } else if (orientation == 8) {
                matrix.postRotate(270);
            }

            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (scaledBitmap != null) {
            if (new File(MainApplication.IMAGE_PATH, fileName).exists()) {
                new File(MainApplication.IMAGE_PATH, fileName).delete();
            }
        }

        return convertBitmapToFile(scaledBitmap, fileName).getName();
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    public enum ImageSize {
        SMALL_COMPRESS(340.0f, 270.0f);
        private float width;
        private float height;

        ImageSize(float width, float height) {
            this.width = width;
            this.height = height;
        }
    }
}
