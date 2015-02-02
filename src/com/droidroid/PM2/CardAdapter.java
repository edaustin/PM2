package com.droidroid.PM2;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

class CardAdapter extends AbstractWheelTextAdapter {

    final String TAG  = "CardAdapter";

    //the deck and card images are updated in global scope and here we use the most 'current' value
    //this simply writes a wheel with all previous selected values removed (via the global structure updating)

    // deck
    String cards[];
    // Card Flags
    int cardz[];


    /**
     * Constructor
     */
    protected CardAdapter(Context context, String icards[], int icardz[]) {
        super(context, R.layout.card_layout, NO_RESOURCE);
        //ObjectUtils.showToast("Constructor called", context);

        // deck
        cards =  icards;
        // Card Flags
        cardz = icardz;

        //set the name descriptor of the card
        setItemTextResource(R.id.card_name);
    }



    @Override
    public View getItem(int index, View cachedView, ViewGroup parent) {
        //ObjectUtils.showToast("getItem "+index, context);


        //compression (of already compressed images) *vastly* increased wheel speed to ultra smooth also eliminated the out of memory errors

        //http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        //used context.getResources() instead of getResources()
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), cardz[index], options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;
        //ObjectUtils.showToast("bm size "+imageHeight+"/"+imageWidth, context);



        View view = super.getItem(index, cachedView, parent);
        ImageView img = (ImageView) view.findViewById(R.id.card_image);

        ////http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
        img.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), cardz[index], 100, 100));
/*      4-aug-14
        myAsyncObject mao = new myAsyncObject();
        mao.iv = img;
        mao.i = index;
        bmd bd = new bmd();
        bd.execute(mao);
*/

        //img.setImageResource(cardz[index]);

        return view;
    }

    @Override
    public int getItemsCount() {
        return cards.length;
    }

    @Override
    protected CharSequence getItemText(int index) {
        return cards[index];
    }

    //http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image


        final int height = options.outHeight;
        final int width = options.outWidth;

        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

        //return 4;
    }




    //http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);



        // Calculate inSampleSize
        //options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //14-FEB-14 OOM error fix?
        //options.inSampleSize = 4;
        options.inPurgeable = true;


        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }
/*
    private class bmd extends AsyncTask<myAsyncObject, Void, Void> {
        protected Void doInBackground(myAsyncObject... obj) {

            ImageView img = obj[0].iv;
            int index = obj[0].i;

            img.setImageBitmap(decodeSampledBitmapFromResource(context.getResources(), cardz[index], 100, 100));



        return null;
        }

        protected void onProgressUpdate(Integer... progress) {

        }
        protected void onPostExecute(Bitmap ds) {
            //ui stuff needs to be done here not in dib! i.e. img. ....
        }
    }

    //inner class
    protected class myAsyncObject {
        ImageView iv;
        int i;
    }
*/
}
