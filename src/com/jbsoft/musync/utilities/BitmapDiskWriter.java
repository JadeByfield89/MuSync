
package com.jbsoft.musync.utilities;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;

//Class that writes a bitmap to a file on disk
public class BitmapDiskWriter {

    public BitmapDiskWriter() {

    }

    public void writeBitmapToFile(Bitmap b, String artistName) {

        try {
            String directoryPath = Environment.getExternalStorageDirectory()
                    + "/MuSync/ArtistThumbnails/";
            String thumbnailPath = directoryPath + artistName + ".png";

            FileOutputStream stream = new FileOutputStream(thumbnailPath);
            b.compress(CompressFormat.PNG, 100, stream);
            stream.flush();
            stream.close();

        } catch (Exception e) {
            Log.d("BitmapDiskWriter", "Could not save " + artistName + ".png");
        }

    }
}
