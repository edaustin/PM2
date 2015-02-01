package com.droidroid.PM2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.Preference;
//import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import scala.Array;

import java.util.*;

/**
 * Created by ed on 7/31/14.
 */
public class LvHeroOuts {

    BaseAdapter oA(final Context ctx, final int[] heroOutsAtFlop, final int[] on, final Crypto_htable cry) {

        final String TAG = "LvHeroOuts";
        //6-aug-14 not needed as pre-screened before passed
        //final String ignoredCard="66AQ2"; //27-7-14 need to resolve issues with this erroneous card - see PrimaryActivity doc for cause!

        //set data structures for our misc data (scaffolding)
        String myHand;

        List<Map<String, String>> vL = new ArrayList<Map<String, String>>();
        Map<String, String> dataMap;

        //populate the List used to pass to the adapter
        final int hl = heroOutsAtFlop.length;

        SharedPreferences prefs = ctx.getSharedPreferences("com.droidroid.PM2", Context.MODE_PRIVATE);
        final int filter_none_hero = prefs.getInt("filter-none-hero",1);

        for (int i = 0; i < hl; i++) {

            //Log.d(TAG, ""+heroOutsAtFlop[i]+" "+(cry.htableTable_2[heroOutsAtFlop[i]-1])+" "+(cry.htableTable_3[heroOutsAtFlop[i]-1]));

            dataMap = new HashMap<String, String>(2);
            final String cc1=cry.htableTable_3[heroOutsAtFlop[i] - 1].replaceAll("\\s+","");


            if ((filter_none_hero == 2) //show only hero discard others
                &&
               (!PokerAlgs.isValidHero( (heroOutsAtFlop[i]),ObjectUtils.currentPlayerCardsArrayDesc(), cry))) {

                //don't put invalid hands if qa set
            }

            else
            {

            //TODO 31-7-14 66AQ2
            //if (!cc1.equals(ignoredCard)) {
                dataMap.put("key1", cry.htableTable_2[heroOutsAtFlop[i] - 1]+" ["+on[i]+"]");  //Hand
                dataMap.put("key2", cc1 + " (" + (i + 1) + "/" + hl + ")"); //Hand Description
                vL.add(dataMap);
            //}

            }
        }

        //define the adapter SimpleAdapter that uses simple_list_item_2

        //http://stackoverflow.com/questions/21453511/simpleadapter-notifydatasetchanged
        //changed SimpleAdapter to its parent BaseAdapter
        //
        BaseAdapter mAdapter =
                new SimpleAdapter(ctx,       //context
                        vL,        //List<? extends Map<String,?>>data
                        android.R.layout.simple_list_item_2,    //resource
                        new String[]{"key1", "key2"},   //key filters
                        new int[]{android.R.id.text1, android.R.id.text2}) {
                                        //subclass SimpleAdapter override getView
                                        public View getView(int position, View convertView, ViewGroup parent) {
                                            View view = super.getView(position, convertView, parent);
                                            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                            TextView text2 = (TextView) view.findViewById(android.R.id.text2);

                                            if ((!PokerAlgs.isValidHero( heroOutsAtFlop[position],ObjectUtils.currentPlayerCardsArrayDesc(), cry))
                                                && ((filter_none_hero == 1))) //shows all hands
                                                {
                                                text1.setTextColor(Color.GRAY);
                                                text2.setTextColor(Color.GRAY);
                                                }
                                                else //shows only valid
                                                {
                                                text1.setTextColor(Color.WHITE);
                                                text2.setTextColor(Color.GREEN);
                                                }


                                            //if (position==0) view.setBackgroundResource(R.drawable.customborders_green);

                                            return view;

                                        } //oA

                        };

        return mAdapter;

    }
}