package com.droidroid.PM2;

import android.content.Context;
import android.content.SharedPreferences;
//import android.content.res.ColorStateList;
import android.graphics.Color;
//import android.util.Log;
import android.view.Gravity;
//import android.view.View;
import android.widget.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;

/**
 * Created by ed on 8/3/14.
 */
public class OutsTable {

    final String TAG = "OutsTable";

    int factoredOutsAdjust=0;

    public TableLayout doTable(Context c, int[] o, int[] outsNo, Crypto_htable cry, int stage) {



        SharedPreferences prefs = c.getSharedPreferences("com.droidroid.PM2", Context.MODE_PRIVATE);
        final int filter_none_hero = prefs.getInt("filter-none-hero",1);


        Context ctx = c;
        int[] outs = o;



        final int STAGE=stage;

        //dynamic number of columns (i.e. if flop/turn) needs to bet set
        final String[] columnt = {"Hand", "Outs", "T Odds", "TR Odds"};
        final String[] columnr = {"Hand", "Outs", "R Odds"};


        //need to collapse flushes i.e. QH flush!

        //need to eliminate hands where it improves the flop only!



        //current number of rows
        int totalOuts=0;
        int noOuts = outs.length; //to add final totals
        int i;
        //scan
        String[] row = new String[noOuts+1]; //max, add one for TOTALS
        String[] row2 = new String[noOuts+1]; //max, add one for TOTALS
        int[] irow = new int[noOuts+1]; //add one for TOTALS


        String[] range = new String[noOuts+1]; //max, type of out hand range
        int[] ocr = new int[noOuts+1]; //max, no of outs of each type
        String endr="";
        String prev="";
        int storedIndex=0;
        int overallIndex=0;
        boolean flag=false;


        //new array
        String[] frow = new String[noOuts+1];
        String[] frow2 = new String[noOuts+1];
        int[] foutsNo = new int[outsNo.length+1];
        int[] firow = new int[noOuts+1]; //add one for TOTALS
        int tempStoredIndex=0;
        int tempStoredOuts=0;


        int oc=0;
        int sindex=0;

        //5-aug-14 build a consolidated array first

        for (i=0; i < outs.length; i++)

        {

            //is it even a valid hand for hero
            if (PokerAlgs.isValidHero(outs[i], ObjectUtils.currentPlayerCardsArrayDesc() , cry))

                {

                    row[i] = cry.htableTable_2[outs[i] - 1];
                    row2[i] = cry.htableTable_3[outs[i] - 1];
                    foutsNo[storedIndex] = outsNo[i];

                    //Log.d(TAG,"a fend row[i] prev "+row[i]+"/"+prev);


                    //if row is not same as previous its a new type
                    //we only want one of each type of out such as "Ace-High Flush"
                    if (!row[i].equals(prev)) {

                        //Log.d(TAG,"a2 fend row[i] prev "+row[i]+"/"+prev);

                        if (!prev.equals("") && (!endr.equals("")))
                        {
                            //Log.d(TAG,"b fend of range1="+endr);
                            endr="";
                            frow2[tempStoredIndex]="..range..";
                            foutsNo[tempStoredIndex]=tempStoredOuts+1; //add one 7-aug-14
                            tempStoredOuts=0;
                        }


                        //displayed at start of each unique type of hand
                        //Log.d(TAG,"c fend si="+storedIndex+" Unique out is "+row[i]+" starting at "+cry.htableTable_3[outs[i] - 1]);

                        //7-aug-14 if we have a flush this will reduce our outs for any pair by one as the alg only calcs once for each hand and returns highest available
                        //this discounts its use as the hand 'overlaps' another potential hand

                        //if a pair predicted the outs MUST be 3 as always three cards left UNLESS we already had a pair!
                    //    if (outs[i]> 3325 && outs[i] < 6186) //it's a pair
                    //    {
                    //        Log.d(TAG,"fouts pair modification for "+cry.htableTable_3[outs[i] - 1]+" current outs ="+outs[i]);
                    //        if ( (outsNo[i]) == 2)  {
                    //            Log.d(TAG,"fouts pair new outs =3! for "+cry.htableTable_3[outs[i] - 1]);
                    //            outsNo[i]=3;
                    //            factoredOutsAdjust+=1; //add one to our factored outs to deduct from total factored outs
                    //        }
                    //    }
                    //    Log.d(TAG,"fouts factored outs are "+factoredOutsAdjust+" for "+outs[i]);

                        range[i]=cry.htableTable_3[outs[i] - 1];

                        //fixed
                        frow[storedIndex] = row[i];
                        frow2[storedIndex] = row2[i];
                        firow[storedIndex] = 1;

                        //default from outsNo array
                        foutsNo[storedIndex] = outsNo[i];


                        tempStoredIndex=storedIndex; //needed? use sI?
                        storedIndex++;
                        prev=row[i];

                    }
                    else //row=prev
                    {
                        //transitioning through the same hands
                        endr=cry.htableTable_3[outs[i] - 1];
                        tempStoredOuts++;
                    }


                    totalOuts=totalOuts+outsNo[i];
                    irow[i]=1;


                } //valid

            else //place as option 9-aug-14 TODO show all hands

            if (filter_none_hero ==1) //show all hands

                    //use filter_none_hero
                    {
                        row[i] = "[" + cry.htableTable_2[outs[i] - 1] + "]"; //test
                        row2[i] = "[" + cry.htableTable_3[outs[i] - 1] + "]"; //test
                        irow[i]=0;


                        //7-aug-14 if we have a flush this will reduce our outs for any pair by one as the alg only calcs once for each hand and returns highest available
                        //this discounts its use as the hand 'overlaps' another potential hand

                        //if a pair predicted the outs MUST be 3 as always three cards left UNLESS we already had a pair!
                        if (outs[i]> 3325 && outs[i] < 6186) //it's a pair
                        {
                            //Log.d(TAG,"x pair modification for "+cry.htableTable_3[outs[i] - 1]+" current outs ="+outs[i]);
                            if ( (outsNo[i]) == 2)  {
                                //Log.d(TAG,"x pair new outs =3! for "+cry.htableTable_3[outs[i] - 1]);
                                outsNo[i]=3;
                            }
                        }

                        //fixed
                        frow[storedIndex] = row[i];
                        frow2[storedIndex] = row2[i];
                        foutsNo[storedIndex] = outsNo[i];
                        firow[storedIndex]=irow[i];

                        storedIndex++;
                    } //filter

            overallIndex++;



        }


        //if (!endr.equals("")) Log.d(TAG,"d fend of range2="+endr);
        endr=null;

        //row[noOuts]="TOTALS";
        //fixed
        frow[storedIndex] = "TOTAL";






        //6-aug-14 we now need to calculate factored total outs for the irow hands using row2 (unfiltered)
        //we set an array of 52 cards
        //we add to the array as many times as we wish
        //we iterate and simply count>0

        //we can use arraydesc to retrieve the complete suite information

        String theCard="";
        for (int ra=0;ra < row2.length;ra++){

            theCard=row2[ra];
            //if (irow[ra]==1) Log.d(TAG, "unfactored the card is "+theCard);

        }






        //5-Aug-14
        //from the consolidated built array convert to a displayable one


        int rl = row.length+1; //add for titles and for final totals
        String[] column=null;

        if (STAGE==1) column = columnt; else if (STAGE==2) column = columnr; //5-aug-14 kludgy

        int cl = column.length+1;

        //fixed
        //shorten frow from its original template length
        ArrayList<String> removed = new ArrayList<String>();
            for (String str : frow)
                if (str != null) removed.add(str);
        frow = removed.toArray(new String[0]);

        //same for frow2
        ArrayList<String> removed2 = new ArrayList<String>();
        for (String str : frow2)
            if (str != null) removed2.add(str);
        frow2 = removed2.toArray(new String[0]);


        int frl = frow.length+1; //add for titles and for final totals




        //ScrollView sv = new ScrollView(ctx);
        TableLayout tableLayout = createTableLayout(frow2, firow, frow, column, frl, cl, foutsNo, totalOuts, ctx);
        //HorizontalScrollView hsv = new HorizontalScrollView(ctx);

        //hsv.addView(tableLayout);
        return tableLayout;

        //sv.addView(hsv);
        //return sv;
    }

    //06-aug-14 TODO cleanup when no outs i.e. AH straight AKQJT

    private TableLayout createTableLayout(String[] row2, int[] irow, String[] rv, String[] cv, int rowCount, int columnCount, int[] outsNo, int totalOuts, Context ctx) {
        // 1) Create a tableLayout and its params
        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
        TableLayout tableLayout = new TableLayout(ctx);
        tableLayout.setBackgroundColor(Color.BLACK);

        // 2) create tableRow params
        TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
        tableRowParams.setMargins(1, 1, 1, 1);
        tableRowParams.weight = 1f;

        double turn_outs=0;
        float river_final=0;

        int tOuts=0;
        DecimalFormat df = new DecimalFormat("#.##");

        for (int r = 0; r < rowCount; r++) {
            // 3) create tableRow
            TableRow tableRow = new TableRow(ctx);
            //tableRow.setBackgroundColor(Color.BLACK);

            for (int c = 0; c < columnCount; c++) {
                // 4) create textView
                TextView textView = new TextView(ctx);
                //  textView.setText(String.valueOf(j));
                //textView.setBackgroundColor(Color.WHITE);
                textView.setGravity(Gravity.LEFT);

                //r,c

                //TITLE
                if (r == 0 && c == 0) {
                    textView.setTextColor(Color.CYAN);
                    textView.setText("Type of Out");
                } else if (r == 0) {
                    textView.setTextColor(Color.CYAN);
                    textView.setText(cv[c - 1]); //col titles

                  //TYPE OF OUT
                } else if (c == 0) {

                    textView.setText(rv[r - 1]); //row titles
                    if (irow[r-1]==1) textView.setTextColor(Color.GREEN);
                    if (r == rv.length) textView.setTextColor(Color.parseColor(("#ffa500")));
                } else {

                    //textView.setText("" + r+"/"+c);
                    if (r < rowCount-1) {

                        //HAND ROW
                        if (c == 1) { textView.setText("" + row2[r - 1]);
                            if (irow[r-1]==1) textView.setTextColor(Color.YELLOW);
                            //tOuts=tOuts+outsNo[r - 1];
                        }



                        //NO OUTS ROW
                        if (c == 2) { textView.setText("" + outsNo[r - 1]);
                                      if (irow[r-1]==1) textView.setTextColor(Color.YELLOW);
                                      //tOuts=tOuts+outsNo[r - 1];
                        }

                        //TURN ROWS
                        if (c == 3) {

                            turn_outs = outsNo[r - 1];

                            double todds = ((52f - 5f) / ((double)outsNo[r - 1]))-1;
                            todds=(double)Math.round(todds * 10) / 10;
                            textView.setText("" + todds + "-1"); //turn
                            if (irow[r-1]==1) textView.setTextColor(Color.YELLOW);
                        }


                        //RIVER ROWS
                        if (c == 4) {

                            //(38/47)*(37/46) = 65%. The chance of you hitting your flush is 100% - 65% = 35%.

                            double outs = (double)outsNo[r - 1];

                            //Log.d(TAG,"turn outs="+turn_outs);
                            //Log.d(TAG,"river outs="+outs);

                            //n% chance of making by the river
                            river_final =       (float)(1f - (
                                                ( (47f-turn_outs) / 47f)
                                                *
                                                ( (46f-outs) / 46f  )
                                                ))*100f;
                            //Log.d(TAG,"1 result="+river_final);

                            //Divide the percentage by 100 to convert from a percentage to a decimal. For example, if you have 40 percent, divide 40 by 100 to get 0.4.

                            river_final = river_final /100f;
                            //Log.d(TAG,"2 result="+river_final);

                            //Divide 1 by the percentage expressed as a decimal. In this example, divide 1 by 0.4 to get 2.5.

                            river_final = 1/river_final;
                            //Log.d(TAG,"3 result="+river_final);

                            //Subtract 1 from the result to find the first number of the odds ratio. In this example, subtract 1 from 2.5 to get 1.5.

                            river_final = river_final-1;
                            //Log.d(TAG,"4 result="+river_final);

                            //Substitute the result in for X in the odds ratio X-to-1. In this example, substitute in 1.5 to find that 40 percent converts to an odds ratio of 1.5-to-1.

                            //Log.d(TAG,"5 result="+river_final+"-1");

                            double rodds = river_final;


                            //double rodds = ((52f - 6f) / ((double) outsNo[r - 1]))-1;
                            rodds=(double)Math.round(rodds * 10) / 10;
                            textView.setText("" + rodds + "-1"); //river
                            if (irow[r-1]==1) textView.setTextColor(Color.YELLOW);
                        }


                    }
                    else //last row
                    {

                        //HAND
                        if (c == 1) {
                            textView.setTextColor(Color.parseColor(("#ffa500")));
                            textView.setText("--");  //total number of outs
                        }
                        //OUTS
                        if (c == 2) {
                            textView.setTextColor(Color.parseColor(("#ffa500")));
                            textView.setText(""+(totalOuts));  //total number of outs
                        }

                        //TURN LAST ROW
                        if (c == 3) {

                            double todds=0;
                            double total_todds=0;

                            todds = ((52f - 5f) / ((double) totalOuts)) - 1;
                            total_todds = (double) Math.round(todds * 10) / 10;

                            //18-aug-14 override nonsense - also resolves nuts issue
                            if (total_todds <250) textView.setText("" + total_todds + "-1"); //turn
                            else  textView.setText("--"); //turn

                            textView.setTextColor(Color.parseColor(("#ffa500")));

                        }


                        //RIVER LAST ROW
                        if (c == 4) {

                            double todds=0;
                            double total_todds=0;
                            float t1=0;
                            float t2=0;
                            //Log.d(TAG,"0 total outs="+totalOuts);

                            t1= ( (47f-(float)totalOuts) / 47f);
                            //Log.d(TAG,"0 t1="+t1);
                            t2= ( (46f-(float)totalOuts) / 46f  );
                            //Log.d(TAG,"0 t2="+t2);
                            //Log.d(TAG,"0 t1*t2="+(t1*t2));

                            //n% chance of making by the river
                            river_final = (float)(1 - (t1*t2)) *100;

                            //Log.d(TAG,"1 total outs="+river_final);

                            river_final = river_final /100;//Log.d(TAG,"2 total outs="+river_final);
                            river_final = 1/river_final;//Log.d(TAG,"3 total outs="+river_final);
                            river_final = river_final-1;//Log.d(TAG,"4 total outs="+river_final);
                            todds = river_final;







                            //todds = ((52f - 6f) / ((double) totalOuts)) - 1;
                            total_todds = (double) Math.round(todds * 10) / 10;


                            //18-aug-14 override nonsense - also resolves nuts issue
                            if (total_todds <250)  textView.setText("" + total_todds + "-1"); //river
                            else  textView.setText("--"); //turn

                            textView.setTextColor(Color.parseColor(("#ffa500")));
                        }
                    }

                }

                // 5) add textView to tableRow
                tableRow.addView(textView, tableRowParams);
            }

            // 6) add tableRow to tableLayout
            tableLayout.addView(tableRow, tableLayoutParams);
        }

        return tableLayout;
    }

}