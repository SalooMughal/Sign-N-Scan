package com.pixelz360.docsign.imagetopdf.creator.RoomDb.adapters;

import android.graphics.Bitmap;

public class ThumbnailResult {


        Bitmap thumbnail;
        int pageCount;

        public ThumbnailResult(Bitmap thumbnail, int pageCount) {
            this.thumbnail = thumbnail;
            this.pageCount = pageCount;
        }


}
