#include <jni.h>
#include <string.h>
#include <stdint.h>
//#include <android/log.h>
#define DEBUG_TAG "NDK_EA"
//for rand
#include <stdlib.h>
//for cpu
#include <unistd.h>

//data TODO encryption!
#include <data.h>


//lookup table fastest
 short sea_check(int lf1, int lf2, int *deck)
 {

            if ((deck[lf1]>16700000) && (deck[lf2]>16700000)) return 1; //broadway

            //return pairs


            //low pairs
            if (((deck[lf1]   >=69634)       &&  (deck[lf1]   <=98306))         &&  ((deck[lf2]   >=69634)       &&  (deck[lf2]   <=98306)))        return 1;    //2
            if (((deck[lf1]   >=135427)      &&  (deck[lf1]   <=164099))        &&  ((deck[lf2]   >=135427)      &&  (deck[lf2]   <=164099)))       return 1;    //3
            if (((deck[lf1]   >=266757)      &&  (deck[lf1]   <=295429))        &&  ((deck[lf2]   >=266757)      &&  (deck[lf2]   <=295429)))       return 1;    //4
            if (((deck[lf1]   >=529159)      &&  (deck[lf1]   <=557831))        &&  ((deck[lf2]   >=529159)      &&  (deck[lf2]   <=557831)))       return 1;    //5
            if (((deck[lf1]   >=1053707)     &&  (deck[lf1]   <=1082379))       &&  ((deck[lf2]   >=1053707)     &&  (deck[lf2]   <=1082379)))      return 1;    //6
            if (((deck[lf1]   >=2102541)     &&  (deck[lf1]   <=2131213))       &&  ((deck[lf2]   >=2102541)     &&  (deck[lf2]   <=2131213)))      return 1;    //7
            if (((deck[lf1]   >=4199953)     &&  (deck[lf1]   <=4228625))       &&  ((deck[lf2]   >=4199953)     &&  (deck[lf2]   <=4228625)))      return 1;    //8
            if (((deck[lf1]   >=8394515)     &&  (deck[lf1]   <=8423187))       &&  ((deck[lf2]   >=8394515)     &&  (deck[lf2]   <=8423187)))      return 1;    //9

            //high pairs
            if (((deck[lf1]   >=16783383)    &&  (deck[lf1]   <=16812055))      &&  ((deck[lf2]   >=16783383)    &&  (deck[lf2]   <=16812055)))     return 1;    //T
            if (((deck[lf1]   >=33560861)    &&  (deck[lf1]   <=33589533))      &&  ((deck[lf2]   >=33560861)    &&  (deck[lf2]   <=33589533)))     return 1;    //J
            if (((deck[lf1]   >=67115551)    &&  (deck[lf1]   <=67144223))      &&  ((deck[lf2]   >=67115551)    &&  (deck[lf2]   <=67144223)))     return 1;    //Q
            if (((deck[lf1]   >=134224677)   &&  (deck[lf1]   <=134253349))     &&  ((deck[lf2]   >=134224677)   &&  (deck[lf2]   <=134253349)))    return 1;    //K
            if (((deck[lf1]   >=268442665)   &&  (deck[lf1]   <=268471337))     &&  ((deck[lf2]   >=268442665)   &&  (deck[lf2]   <=268471337)))    return 1;    //A

            //default
            return 0;
 }


 short eval_7hand( int c1, int c2, int c3, int c4, int c5, int c6, int c7)
 {
    int hand[7];

    hand[0]=c1;
    hand[1]=c2;
    hand[2]=c3;
    hand[3]=c4;
    hand[4]=c5;
    hand[5]=c6;
    hand[6]=c7;

     int i, j, q, xbest = 9999, subhand[5];

 	for ( i = 0; i < 21; i++ )
 	{
 		for ( j = 0; j < 5; j++ )
 			subhand[j] = hand[ perm7[i][j] ];
 		q = eval_5hand_fast( subhand[0],subhand[1],subhand[2],subhand[3],subhand[4]);
 		if ( q < xbest )
 			xbest = q;
 	}
 	return( xbest );
 }


   unsigned find_fast(unsigned u)
   {
       unsigned a, b, r;
       u += 0xe91aaa35;
       u ^= u >> 16;
       u += u << 8;
       u ^= u >> 4;
       b  = (u >> 8) & 0x1ff;
       a  = (u + (u << 2)) >> 19;
       r  = a ^ hash_adjust[b];
       return r;
   }

   int eval_5hand_fast(int fc1, int fc2, int fc3, int fc4, int fc5)
   {
       int q = (fc1 | fc2 | fc3 | fc4 | fc5) >> 16;
       short s;
       if (fc1 & fc2 & fc3 & fc4 & fc5 & 0xf000) return flushes[q]; // check for flushes and straight flushes
       if ((s = unique5[q]))                return s;          // check for straights and high card hands
       return hash_values[find_fast((fc1 & 0xff) * (fc2 & 0xff) * (fc3 & 0xff) * (fc4 & 0xff) * (fc5 & 0xff))];
   }





int main(){ return 1;}


//equityAnalysis
//we only pass the hero as we then extract the flop and randomize the villain


JNIEXPORT jfloatArray JNICALL Java_com_droidroid_PM2_EquityBroadcastServiceWrapper_equityAnalysisCore(JNIEnv * env, jobject this,
                                                jarray hero_hand_jni, jint sea) {


                                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, " NDK:LC: Entered equityAnalysis with SEA [%d] ",sea);


//jsize len=(*env)->GetArrayLength(env,hero_hand);
jintArray *hero_hand=(*env)->GetIntArrayElements(env,hero_hand_jni,0);

jfloatArray equityResults; equityResults = (*env)->NewFloatArray(env, 3);

                                       int deck[52];

                                       //HEARTS
                                       deck[0]   =98306;    //2
                                       deck[1]   =164099;    //3
                                       deck[2]   =295429;    //4
                                       deck[3]   =557831;    //5
                                       deck[4]   =1082379;    //6
                                       deck[5]   =2131213;    //7
                                       deck[6]   =4228625;    //8
                                       deck[7]   =8423187;    //9
                                       deck[8]   =16812055;    //T
                                       deck[9]   =33589533;    //J
                                       deck[10]   =67144223;   //Q
                                       deck[11]   =134253349;  //K
                                       deck[12]   =268471337;  //A

                                       //DIAMONDS
                                       deck[13]  =81922;
                                       deck[14]  =147715;
                                       deck[15]  =279045;
                                       deck[16]  =541447;
                                       deck[17]  =1065995;
                                       deck[18]  =2114829;
                                       deck[19]  =4212241;
                                       deck[20]  =8406803;
                                       deck[21]  =16795671;
                                       deck[22]  =33573149;
                                       deck[23]  =67127839;
                                       deck[24]  =134236965;
                                       deck[25]  =268454953;

                                       //SPADES
                                       deck[26]  =73730;
                                       deck[27]  =139523;
                                       deck[28]  =270853;
                                       deck[29]  =533255;
                                       deck[30]  =1057803;
                                       deck[31]  =2106637;
                                       deck[32]  =4204049;
                                       deck[33]  =8398611;
                                       deck[34]  =16787479;
                                       deck[35]  =33564957;
                                       deck[36]  =67119647;
                                       deck[37]  =134228773;
                                       deck[38]  =268446761;

                                       //CLUBS
                                       deck[39]   =69634;
                                       deck[40]   =135427;
                                       deck[41]   =266757;
                                       deck[42]   =529159;
                                       deck[43]   =1053707;
                                       deck[44]   =2102541;
                                       deck[45]   =4199953;
                                       deck[46]   =8394515;
                                       deck[47]   =16783383;
                                       deck[48]   =33560861;
                                       deck[49]   =67115551;
                                       deck[50]   =134224677;
                                       deck[51]   =268442665;

                                        const char *adeck[52];

                                        adeck[0]   ="2h";    //2
                                        adeck[1]   ="3h";    //3
                                        adeck[2]   ="4h";    //4
                                        adeck[3]   ="5h";    //5
                                        adeck[4]   ="6h";    //6
                                        adeck[5]   ="7h";    //7
                                        adeck[6]   ="8h";    //8
                                        adeck[7]   ="9h";    //9
                                        adeck[8]   ="Th";    //T
                                        adeck[9]   ="Jh";    //J
                                        adeck[10]   ="Qh";   //Q
                                        adeck[11]   ="Kh";  //K
                                        adeck[12]   = "Ah";  //A

                                        //DIAMONDS
                                        adeck[13]  ="2d";
                                        adeck[14]  ="3d";
                                        adeck[15]  ="4d";
                                        adeck[16]  ="5d";
                                        adeck[17]  ="6d";
                                        adeck[18]  ="7d";
                                        adeck[19]  ="8d";
                                        adeck[20]  ="9d";
                                        adeck[21]  ="Td";
                                        adeck[22]  ="Jd";
                                        adeck[23]  ="Qd";
                                        adeck[24]  ="Kd";
                                        adeck[25]  ="Ad";

                                        //SPADES
                                        adeck[26]  ="2s";
                                        adeck[27]  ="3s";
                                        adeck[28]  ="4s";
                                        adeck[29]  ="5s";
                                        adeck[30]  ="6s";
                                        adeck[31]  ="7s";
                                        adeck[32]  ="8s";
                                        adeck[33]  ="9s";
                                        adeck[34]  ="Ts";
                                        adeck[35]  ="Js";
                                        adeck[36]  ="Qs";
                                        adeck[37]  ="Ks";
                                        adeck[38]  ="As";

                                        //CLUBS
                                        adeck[39]   ="2c";
                                        adeck[40]   ="3c";
                                        adeck[41]   ="4c";
                                        adeck[42]   ="5c";
                                        adeck[43]   ="6c";
                                        adeck[44]   ="7c";
                                        adeck[45]   ="8c";
                                        adeck[46]   ="9c";
                                        adeck[47]   ="Tc";
                                        adeck[48]   ="Jc";
                                        adeck[49]   ="Qc";
                                        adeck[50]   ="Kc";
                                        adeck[51]   ="Ac";



 //monte carlo
 //all we require is pf/flop string of h
 //mc: randomize pf+tr (for v) and compare h+tr (ignoring pf match)

 //0.1 13-aug-14    code without factoring in hero, test flop case


                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: passed in [%d] [%d] [%d] [%d] [%d] [%d] [%d]",(int)hero_hand[0], (int)hero_hand[1],(int)hero_hand[2],(int)hero_hand[3],(int)hero_hand[4],(int)hero_hand[5],(int)hero_hand[6]);
                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: passed in [%d] [%d] [%d] [%d] [%d] [%d] [%d]",(int)hero_hand[0], (int)hero_hand[1],(int)hero_hand[2],(int)hero_hand[3],(int)hero_hand[4],(int)hero_hand[5],(int)hero_hand[6]);


                      //seed rng
                      srand(time(NULL));

                      //no of cpus affect monte-carlo iterations
                      short cpu = sysconf(_SC_NPROCESSORS_CONF);
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: CPU=[%d]",cpu);
                      int samples=30000; //default is a dual-core device
                      if (cpu==1) samples=20000; //1 cpu
                      else
                      if (cpu==2) samples=30000; //dual-core
                      else
                      if (cpu==3) samples=40000; //3 cpu
                      else
                      if (cpu>3 && cpu<8 ) samples=50000; //quad or higher cpu
                      else
                      if (cpu>=8) samples=60000; // slightly higher

                      int randomSample=0;


                      //generate for villain
                      int v_pre_flop_a=0;
                      int v_pre_flop_b=0;

                      //generate for hero/villain
                      int turn=0;
                      int river=0;
                      //tmp
                      int temp_turn=0;
                      int temp_river=0;




                      //get the c values for the passed flop
                      int h_pre_flop_a =0;
                      int h_pre_flop_b =0;

                      int flop_a=0;
                      int flop_b=0;
                      int flop_c=0;


                      //if hero_hand[5] populated we are at the River
                      //simply override this card on the T/R generation
                      int atStage=0;
                      int fixed_turn=0;
                      int fixed_river=0;



                      if (hero_hand[5]==0){
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, " NDK:LC: equityAnalysis Flop");
                         //get the c values for the passed flop
                        h_pre_flop_a =(int)hero_hand[4];
                        h_pre_flop_b =(int)hero_hand[3];

                        flop_a=(int)hero_hand[2];
                        flop_b=(int)hero_hand[1];
                        flop_c=(int)hero_hand[0];


                        //if hero_hand[5] populated we are at the River
                        //simply override this card on the T/R generation
                        atStage=0;
                      }

                      //reversals due to ill-thought out design!
                      //simply overwrite in testing
                      else if (hero_hand[6]==0){

                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, " NDK:LC: equityAnalysis Turn");


                          h_pre_flop_a =(int)hero_hand[5];
                          h_pre_flop_b =(int)hero_hand[4];

                          flop_a=(int)hero_hand[3];
                          flop_b=(int)hero_hand[2];
                          flop_c=(int)hero_hand[1];

                          atStage=1;
                          fixed_turn = (int)hero_hand[0];

                          }

                       else //river
                       {

                        //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG,  " NDK:LC: equityAnalysis River");


                         h_pre_flop_a =(int)hero_hand[6];
                         h_pre_flop_b =(int)hero_hand[5];

                         flop_a=(int)hero_hand[4];
                         flop_b=(int)hero_hand[3];
                         flop_c=(int)hero_hand[2];

                         atStage=2;
                         fixed_turn = (int)hero_hand[1];
                         fixed_river = (int)hero_hand[0];

                         }






                      //temp to store cards used
                      int temp_deck[52];

                      //compare
                      short hero_best7=0;
                      short villain_best7=0;

                      //return array
                      float res[3]={0,0,0};


                        //debug storage for var->value/suit
                        int v1,v2,v3,v4,v5,v6,v7;


                      int ti=0;
                      //get turn/river index



                      short temp_turn_index=0;
                      if (fixed_turn>0) {
                      for (ti=0; ti <52; ti++) {
                      if (deck[ti]==fixed_turn) temp_turn_index=ti;
                      }
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG,"we found turn [%s]",adeck[temp_turn_index]);
                      }
                      //get river index

                      short temp_river_index=0;
                      if (fixed_river>0) {
                      for (ti=0; ti <52; ti++) {
                      if (deck[ti]==fixed_river) temp_river_index=ti;
                      }
                      }
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG,"we found river [%s]",adeck[temp_turn_index]);




                      for (randomSample=0; randomSample<samples; randomSample++) {
                      // 1...n

                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC PROCESSING [%d]",randomSample);

                            memcpy(temp_deck, deck, sizeof temp_deck);

                                            int c=0;
                                            //remove hero pre-flop and flop cards from current card array
                                            if (atStage==0)
                                            {for (c=0; c<52; c++)
                                            if (deck[c] == h_pre_flop_a || deck[c] == h_pre_flop_b || deck[c] == flop_a || deck[c] == flop_b || deck[c] == flop_c)
                                                    {
                                                    //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: (5) setting to zero: [%d]",c);
                                                    temp_deck[c]=0;
                                                    }
                                            }
                                            else if (atStage==1) //atTurn>0 ==1 we also remove turn as well as pf/f
                                            {for (c=0; c<52; c++)
                                            if (deck[c] == fixed_turn || deck[c] == h_pre_flop_a || deck[c] == h_pre_flop_b || deck[c] == flop_a || deck[c] == flop_b || deck[c] == flop_c)
                                                    {
                                                    //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: (6) setting to zero: [%s]",adeck[c]);
                                                    temp_deck[c]=0;
                                                    }
                                            }
                                            else if (atStage==2) //atTurn>0 ==1 we also remove turn as well as pf/f
                                            {for (c=0; c<52; c++)
                                            if (deck[c] == fixed_river || deck[c] == fixed_turn || deck[c] == h_pre_flop_a || deck[c] == h_pre_flop_b || deck[c] == flop_a || deck[c] == flop_b || deck[c] == flop_c)
                                                    {
                                                    //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: (6) setting to zero: [%s]",adeck[c]);
                                                    temp_deck[c]=0;
                                                    }
                                             }


                            //generate Turn/River Cards
                            temp_turn=0;
                            temp_river=0;

                            if (atStage==0)
                            {
                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 test1");
                            temp_turn = (int)(rand()  % 52 + 0);
                            temp_river = (int)(rand() % 52 + 0);
                            while (temp_turn == temp_river || temp_deck[temp_turn] ==0 || temp_deck[temp_river] ==0 ) {
                                     temp_turn = (int)(rand()  % 52 + 0);
                                     temp_river = (int)(rand() % 52 + 0);
                                     }
                                  turn = deck[temp_turn];
                                  river= deck[temp_river];

                                  //take the two cards out of the deck
                                  temp_deck[temp_turn]=0; //already done if turn was available
                                  temp_deck[temp_river]=0;

                             }
                             else if (atStage==1)//turn
                             {

                             temp_river = (int)(rand() % 52 + 0);
                             while ( temp_deck[temp_river] ==0 ) {
                                      temp_river = (int)(rand() % 52 + 0);
                                      }
                                   river= deck[temp_river];

                                   //this is fixed
                                   turn = fixed_turn; // or deck[temp_turn_index];

                             }
                             else if (atStage==2)//river
                             {
                                        river=fixed_river;
                                        turn =fixed_turn; // or deck[temp_turn_index];
                             }



                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: rt gen was [%s] [%s]",adeck[temp_river], adeck[temp_turn]);


                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: [%d] rnd [%d] [%d]",randomSample, temp_turn,temp_river);




                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: doing pf");
                                    //rnd villain PF lookup in card array to convert
                                    //make sure not identical to hero pf or the f cards or the t r cards

                                   //16-aug-14 depending on SEA generate only a specific pre-flop range (i.e. a decent one)


                            if (sea==1) //standard

                                   do {
                                                     v_pre_flop_a = (int)(rand() % 52 + 0);
                                                     v_pre_flop_b = (int)(rand() % 52 + 0);

                                           } while ((v_pre_flop_a == v_pre_flop_b) || (temp_deck[v_pre_flop_a]==0) || (temp_deck[v_pre_flop_b]==0));

                            else //(sea=2)

                                  do {
                                                                                v_pre_flop_a = (int)(rand() % 52 + 0);
                                                                                v_pre_flop_b = (int)(rand() % 52 + 0);

                                          } while ((v_pre_flop_a == v_pre_flop_b) || (temp_deck[v_pre_flop_a]==0) || (temp_deck[v_pre_flop_b]==0)
                                            //additional criteria here
                                            || (sea_check(v_pre_flop_a,v_pre_flop_b, deck)==0)
                                            );

                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: pfa pfb [%s] [%s]",adeck[v_pre_flop_a],adeck[v_pre_flop_b]);





                                           //debug
                                           v1=v_pre_flop_a;
                                           v2=v_pre_flop_b;

                                           v_pre_flop_a=deck[v_pre_flop_a];
                                           v_pre_flop_b=deck[v_pre_flop_b];

                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: done pf");

                                          //compare
                                            hero_best7 = eval_7hand(    river,
                                                                        turn,
                                                                        flop_c,
                                                                        flop_b,
                                                                        flop_a,
                                                                        h_pre_flop_a,
                                                                        h_pre_flop_b);

                                            villain_best7 = eval_7hand( river,
                                                                        turn,
                                                                        flop_c,
                                                                        flop_b,
                                                                        flop_a,
                                                                        v_pre_flop_b,
                                                                        v_pre_flop_a);


                                          if (hero_best7 < villain_best7)  {
                                          res[0]++; //better
                                          //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: h better [%d] than v [%d]",hero_best7,villain_best7);
                                          //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: h better v  [%s] [%s] [%d] [%d] [%d] [%d] [%d]",adeck[v7], adeck[v6],flop_c,flop_b,flop_a,h_pre_flop_b,h_pre_flop_a);
                                           }
                                           else if (hero_best7 == villain_best7) {
                                           res[1]++; //equal
                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: h = v  [%d] == [%d]",hero_best7,villain_best7);
                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: h equal v  [%s] [%s] [%d] [%d] [%d] [%s] [%s]",adeck[v7], adeck[v6],flop_c,flop_b,flop_a,adeck[v2], adeck[v1]);
                                           }
                                           else {res[2]++; //villain better
                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: v better  [%d] than h [%d]",villain_best7,hero_best7);
                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: v better h  [%d] [%d] [%d] [%d] [%d] [%s] [%s]",river, turn,flop_c,flop_b,flop_a,adeck[v2], adeck[v1]);
                                           }

                      } //random_samples


                    //normalize and return in int array hwins,eq,vwins
                     res[0]= (((res[0]) * (100)) / (samples));
                     res[1] = (((res[1]) * (100)) / (samples));
                     res[2] = (((res[2]) * (100)) / (samples));

                     //16-8-14 mask inaccuracies
                     if (res[0]>99.6) { res[0]=100; res[1]=0; res[2]=0;};
                     if (res[2]>99.6) { res[0]=0; res[1]=0; res[2]=100;};



                     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: h = v [%f] [%f] [%f]",res[0],res[1],res[2]);


 (*env)->SetFloatArrayRegion(env, equityResults, 0, 3, res);
  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, " NDK:LC: Exited equityAnalysis");
 return equityResults;

} //equityAnalysis
