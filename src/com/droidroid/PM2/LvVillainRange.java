package com.droidroid.PM2;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

//import android.util.Log;


// ListView for Villain Range

public class LvVillainRange {

    //inner class
    protected class myListObject {
        List myList;
        int[] myStatus;
    }

    final String TAG ="LvVillainRange";

    final int CARD_LO=1;
    final int CARD_HI=2;
    final int CARD_EQ=3;
    final int CARD_HERO_HI=4;
    final int CARD_HI_FLAG=8;
    final int CARD_LO_FLAG=9;

    List<Map<String, String>> vL = new ArrayList<Map<String, String>>();
    Map<String, String> dataMap;




    SimpleAdapter gSA(Context ctx, String[] vLs, int[] vh, int hh, String[] cardArray, int[] pf1, int[] pf2, String ek){

    //populate list
    myListObject mlo = initList(vLs,vh,hh,cardArray, pf1, pf2, ctx, ek);
    List vL2 = mlo.myList;
    final int ms[] = mlo.myStatus;

    //create adapter
    SimpleAdapter mAdapter=
            new SimpleAdapter(ctx,       //context
                              vL2,        //List<? extends Map<String,?>>data
                              android.R.layout.simple_list_item_2,    //resource
                              new String[] {"key1","key2"},   //key filters
                              new int[] {android.R.id.text1, android.R.id.text2}) {
                                    //subclass SimpleAdapter override getView
                                    public View getView(int position, View convertView, ViewGroup parent) {
                                        View view = super.getView(position, convertView, parent);
                                        TextView text1 = (TextView) view.findViewById(android.R.id.text2);
                                        view.setBackgroundColor(Color.BLACK); //default

                                        //Log.d(TAG, "SA "+ms[position]);

                                       if (ms[position] == CARD_HI)  {
                                            text1.setTextColor(Color.RED);
                                        }
                                        else if
                                                (ms[position] == CARD_LO)
                                                {text1.setTextColor(Color.YELLOW);}

                                        else if (ms[position] == CARD_EQ)
                                        { text1.setTextColor(Color.GREEN);
                                          view.setBackgroundColor(Color.DKGRAY);
                                        }
                                        else if (ms[position] == CARD_HERO_HI) //currently same
                                        { text1.setTextColor(Color.GREEN);
                                            view.setBackgroundColor(Color.DKGRAY);
                                        }
                                        else //unusual case
                                        { text1.setTextColor(Color.CYAN);
                                            view.setBackgroundColor(Color.DKGRAY);
                                            view.setBackgroundResource(R.drawable.customborders_redv2);
                                        }


                                        return view;
                                        }
                                    };

            return mAdapter;
            }



    myListObject initList(String[] pL, int[] vh, int hh, String[] cardArray, int[] pfa, int[] pfb, Context ctx, String ek) {
        // We populate the list

        SharedPreferences prefs = ctx.getSharedPreferences("com.droidroid.PM2", Context.MODE_PRIVATE);


        int i;
        int tmp=0;
        int diff=0;
        int mpos=0;
        int hpos=1;
        String st="";
        String dp="";
        String dpx="";

        String strippedHand="";


        int[] cardStatus = new int[7462]; //TODO 30-7-14
        String prevCard="";
        //int nc=0; //moved out so can be used in alternate function
        int cs=0;
        int nc=0;



        //TODO 30-7-14 66AQ2
        final String ignoredCard="66AQ2"; //27-7-14 need to resolve issues with this erroneous card

        String turn="";
        String river="";

//28-07-14 TODO check exist/try
        //hero current cards
        //extract flop from cardArray
        String flop1="";
        String flop2="";
        String flop3="";

        //1-8-14 - wheels not finished scrolling error as we don't have the cards!
        try {

            flop1 = cardArray[2].substring(0,1).trim();
            flop2  = cardArray[3].substring(0,1).trim();
            flop3  = cardArray[4].substring(0,1).trim();
        } catch (Exception e) {
            //Log.wtf(TAG,"err "+e); ObjectUtils.showToast("Let the wheels settle!",ctx);
        }




        int vlohands=0;
        int vhihands=0;



        if (cardArray[5] !="") turn  = cardArray[5].substring(0,1).trim();
        if (cardArray[6] !="") river  = cardArray[6].substring(0,1).trim();
        //Log.d(TAG,"extracted cards were "+flop1+flop2+flop3+turn+river);



        //pre-scan of all valid hands
        int cdp=0;
        String tdp="";
        String oldtdp="";
        for (i=0; i < pL.length; i++) if (st != pL[i] && vh[i]>0) {

            //Log.d(TAG,"pL[i]="+pL[i]);

            tdp = pL[i].replaceAll("\\s+","");
            if (tdp.equals(oldtdp)) oldtdp=tdp;
            else { cdp+=1;
                //Log.d(TAG,"tdp="+tdp);
                oldtdp=tdp;}
        }


        float pfEquityL[] = new float[pL.length];
        float pfEquityH[] = new float[pL.length];
        String pfArray[]  = new String[2];
        float pfhs=0;
        int lvPos=0;
        boolean topdog=false;



        final int CUTOFF=70;
        int co=0;


        final int Alg_TenMax = 1;
        final int Alg_SixMax = 2;
        final int Alg_Random = 3;
        final int Alg_ThreeMax = 4;



        for (i=0; i < pL.length; i++) {


            //28-7-14 here we can extract all pf cards for each hand (post flop)
            //    if (vh[i]>0 && !(pL[i].replaceAll("\\s+","").equals(ignoredCard))) {
            //if pfa/pfb are zero this indicates a hero (invalid) hand as the ndk evalX uses 0 to eliminate these hands in pf
            if ((pfa[i] > 0) && (pfb[i] > 0)) {

            //build pf range for the hole cards that can support this turn/river action

            final String suit1 = PokerAlgs.getCardValue(pfa[i]).toString().substring(3,4);
            final String suit2 = PokerAlgs.getCardValue(pfb[i]).toString().substring(3,4);
            final String value1 = PokerAlgs.getCardValue(pfa[i]).toString().substring(1,2);
            final String value2 = PokerAlgs.getCardValue(pfb[i]).toString().substring(1,2);


            pfArray[0] = value1 + " " + suit1;
            pfArray[1] = value2 + " " + suit2;

            pfhs = PokerAlgs.preFlopHandStrength(Alg_Random, pfArray);

                if (pfhs > pfEquityH[i]) {
                    pfEquityH[i]=pfhs;
                }



                //Log.d(TAG, "hh="+hh+" pL[" + i + "]=" + pL[i] + " vh[" + i + "]=" + vh[i] + " pfa=" + PokerAlgs.getCardValue(pfa[i]) + " pfb=" + PokerAlgs.getCardValue(pfb[i])+" pf="+value1+value2+" str="+pfhs);
            }

        //    }



            //st is our previous card
            //pL[i] is string of best hand such as KKT98
            //vh[i] is int of cac best hand
            if (st != pL[i] && vh[i]>0)  //discard blanks (no result) and ??? ignore identical villain results (derived from different pf suits for example)
                {
                 mpos++;
                //diff = i-tmp;
                dp = pL[i].replaceAll("\\s+",""); //+"("+diff+")";g //dp is string hand
                    //Log.d(TAG," reading dp="+dp);

                //lookup type of hand
                    if (vh[i] > 6185)  { dpx="High Card";}    // 1277 high card
                    else if (vh[i] > 3325)     {dpx="One Pair";}       // 2860 one pair
                    else if (vh[i] > 2467)     {dpx="Two Pair";}       //  858 two pair
                    else if (vh[i] > 1609)     {dpx="Three of a Kind";}//  858 three-kind
                    else if (vh[i] > 1599)     {dpx="Straight"; }       //   10 straights
                    else if (vh[i] > 322)      {dpx="Flush";}           // 1277 flushes
                    else if (vh[i] > 166)      {dpx="Full House 3+2";}      //  156 full house
                    else if (vh[i] > 10)       {dpx="Four of a Kind";}   //  156 four-kind
                    else if (vh[i]>1)          {dpx="Straight Flush";}      //   10 straight-flushes
                    else if (vh[i]== 1)        {dpx="Royal Flush";}
                    else if (vh[i] ==0)        dpx="HI-LO-ER Royal Flush?"; //legacy





                    int str=100-(vh[i]*100/7462); //str relative to entire range
                    if (str==0) str=1;

                    //villain better
                    if (vh[i] < hh) {
                        //Log.d(TAG,"Entered CARD_HI with "+dp);
                        cs=CARD_HI;
                        dpx+=" (>)"+" ["+str+"%]";
                        //dpy=mpos+"/"+cdp;
                        vhihands++;

                        //if (pfEquityH[i]>0) {
                        //    //dpx += " alg.rnd=" + pfEquityH[i];
                        //    if (pfEquityH[i]>=CUTOFF) { cs=CARD_HI_FLAG; co++; }
                        //}

                    }
                    else
                    //hero better
                    if (vh[i] > hh) {
                        //Log.d(TAG,"Entered CARD_LO with "+dp);
                        cs=CARD_LO;
                        dpx+=" (<)"+" ["+str+"%]";
                        //dpy=mpos+"/"+cdp;

                        //if (pfEquityH[i]>0) {
                        //    //dpx += " alg.rnd=" + pfEquityH[i];
                        //    if (pfEquityH[i]>=CUTOFF) { cs=CARD_LO_FLAG; co++; }
                        //}

                        vlohands++;


                                //hero unique, we only write once as vh[i]>hh continues through the iterations...
                                if (!topdog) {

                                    final String hdp=heroToHand(hh,ctx, ek);//kkkqj = qqqkj after
                                    cardStatus[nc] = CARD_HERO_HI;
                                    nc++;
                                    prefs.edit().putString("sh"+lvPos, "-hero-").apply();
                                    lvPos=writeUniqueHand(lvPos,hdp,ctx);
                                    prefs.edit().putInt("mpos", mpos).apply(); //actual pos used to track QAH bubble
                                    hpos=mpos;
                                    //dpy=mpos+"/"+cdp;
                                    topdog=true; //only once
                                }


                    }
                    else
                    //equal
                    if (vh[i] == hh) {
                        //Log.d(TAG,"Entered CARD_EQ with "+dp);
                        dpx+=" (=)";
                        //dpy=mpos+"/"+cdp;
                        cs=CARD_EQ;
                        cardStatus[nc] = cs;
                        nc++;
                        hpos=mpos;
                        prefs.edit().putInt("mpos", mpos).apply(); //actual pos used to track QAH bubble
                        topdog=true;
                        }
                     // else vh[1]<hh (better than) at position 1 (defaults to cyan)



                    //Strip the flop from the Villain hand to determine what their pre-flop must be to attain this hand
                    //remove flop1/2/3 from pl[i]


                    if (!dp.equals(ignoredCard)){ //stripped out also later physically via erroneous outs
                        lvPos++;




                        if (turn.isEmpty() && river.isEmpty()) {
                            strippedHand = dp.replaceFirst(Pattern.quote(flop1), "").trim();
                            strippedHand = strippedHand.replaceFirst(Pattern.quote(flop2), "");
                            strippedHand = strippedHand.replaceFirst(Pattern.quote(flop3), "");
                        }
                        //TODO 30-7-14 don't run this one for hero as the return is the minimum to make the hand from another villain generated value identical hand
                        //if incomplete at t/r then pull via ndk t/r routine 27-7-14
                        else {
                            strippedHand = (String) PokerAlgs.getCardValue(pfa[i]).productElement(0) + PokerAlgs.getCardValue(pfb[i]).productElement(0);
                        }
                        prefs.edit().putString("sh"+lvPos, strippedHand).apply();



                        //PokerAlgs.preFlopHandStrength(alg:Int, pfCards: Array[String]): Float
                        //PokerAlgs.chenPfRank (pfCards: Array[String]): Int
                    }



                //ObjectUtils.debugLog(TAG,"h="+hh+" v="+vh[i]);

                //equate hero rank (hh) with Villain range
                st = pL[i];

                if (!dp.equals(ignoredCard)){ //issues with this erroneous hand
                //ObjectUtils.debugLog(TAG,"dp="+dp);

                     //we don't want to write twice
                        dataMap = new HashMap<String, String>(2);
                        dataMap.put("key1", dp);  //Hand
                        dataMap.put("key2", dpx); //Hand Description
                        vL.add(dataMap); //we don't want to write twice


                    if (!dp.equals(prevCard)) {
                        //unique card
                        //30-7-14 semi-kludge
                        //however if we have already written CARD_EQ card we don't want to give this next card the same status
                        if (cs==CARD_EQ) cs=CARD_LO;
                        //same with unique hi
                        //if (cs==CARD_HERO_HI) cs=CARD_LO;

                        cardStatus[nc] = cs;
                        nc++;
                    }
                    prevCard=dp;

                //tmp=i;
                }}

        }

        //even more kludge - save our hero pos center on list
        if (mpos>4) hpos=hpos-4;
        prefs.edit().putInt("misc0", hpos).apply();
        //save our ranking
        if (mpos>4) hpos=hpos+4; //add it back kludge

        //7-aug-14 fix for wheel div zero crash
        try {
            //if (lvPos > 0)
            prefs.edit().putInt("ph", 100 - (hpos * 100) / lvPos).apply();
        } catch (Exception e)
        {
            //Log.d(TAG, " wheel value crash! "+e);
            ObjectUtils.showToast("Let the wheels settle!",ctx);
            prefs.edit().putInt("ph", 1).apply(); //temp
        }

        final int vlo = 0;
        final int vhi = cdp-lvPos;
        final int vcohands=co;
        prefs.edit().putInt("pvlo", vlohands).apply(); //vlo hands
        prefs.edit().putInt("pvhi", vhihands).apply(); //vhi hands
        prefs.edit().putInt("pvco", vcohands).apply(); //vco hands
        prefs.edit().putInt("nuts", hpos).apply(); //vco hands

        myListObject mo = new myListObject();
        mo.myList = vL;
        mo.myStatus = cardStatus;

    return mo;
    }

    //write out at appropriate time if it doesn't exist in generated cards due to being unique and thus not available to a villain generated hand
    public String heroToHand(int hh, Context ctx, String ek){

        Crypto_htable cry = new Crypto_htable();
        rot47 rto = new rot47();

        try {
            cry.decrypt(ctx, R.raw.mash, rto.decode(ek));
        } catch (Exception e) {
            e.printStackTrace();
        }

        //1-8-14 Temp bug fix
        //java.lang.ArrayIndexOutOfBoundsException: length=7462; index=-1
        if (hh==0) {
            hh = 1;
            //Log.wtf(TAG,"heroToHand fed rubbish!"); //we must be doing a -1 sub on an array!
        }

        String hand = cry.htableTable_3[hh-1].replaceAll("\\s+","");

        //Log.d(TAG, "Entered writeHeroIfUnique with " + hand);

        return hand;
    }

    public int writeUniqueHand(int lvp, String dp, Context ctx){



        dataMap = new HashMap<String, String>(2);
        dataMap.put("key1", dp);  //Hand
        dataMap.put("key2", "(unique)"); //Hand Description
        vL.add(dataMap); //we don't want to write twice

        lvp++;


    return lvp;
    }

}
