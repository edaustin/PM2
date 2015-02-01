#include <jni.h>
#include <string.h>
#include <stdint.h>
//#include <android/log.h>
#define DEBUG_TAG "NDK_cE"

//for rand
#include <stdlib.h>

//TODO crypted!
#include <data.h>



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


/////////////////////////////////////////////


 // This is a non-optimized method

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

//22-jul-14
// Similar to eval_7hand. Gets the best 5-card hand from 6 cards.

// Added to allow for 6-card hand evaluations
static int perm6[6][5] = {
  { 0, 1, 2, 3, 4 },
  { 0, 1, 2, 3, 5 },
  { 0, 1, 2, 4, 5 },
  { 0, 1, 3, 4, 5 },
  { 0, 2, 3, 4, 5 },
  { 1, 2, 3, 4, 5 }
};

short eval_6hand(int c1, int c2, int c3, int c4, int c5, int c6)
{

    int hand[6];

    hand[0]=c1;
    hand[1]=c2;
    hand[2]=c3;
    hand[3]=c4;
    hand[4]=c5;
    hand[5]=c6;

  int i, j, q, xbest = 9999, subhand[5];

  for ( i = 0; i < 6; i++ )
  {
    for ( j = 0; j < 5; j++ )
      subhand[j] = hand[ perm6[i][j] ];
    q = eval_5hand_fast(subhand[0], subhand[1], subhand[2], subhand[3],
                        subhand[4]);
    if ( q < xbest )
      xbest = q;
  }
  return( xbest );
}
//06-aug-14 funcs out of main body


                  jint* hero5eval(JNIEnv * env, int* hand) {

                  int HSIZE5=1;  //we return a single item (best hero hand available)
                  jint hero_best5[HSIZE5];
                  jintArray rhero_best5; rhero_best5 = (*env)->NewIntArray(env, HSIZE5);

                                          //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);
                                          hero_best5[0] = eval_5hand_fast(hand[0],hand[1],hand[2],hand[3],hand[4]);
                                          //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Hero has [%d]", hero_best5[0]);
                                          (*env)->SetIntArrayRegion(env, rhero_best5, 0, HSIZE5, hero_best5);
                                          return rhero_best5;

                  }

                  jint* hero6eval(JNIEnv * env, int* hand) {

                  int HSIZE6=1;
                  jint hero_best6[HSIZE6];
                  jintArray rhero_best6; rhero_best6 = (*env)->NewIntArray(env, HSIZE6);

                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);
                                                            //r t f3 f2 f1 pf2 pf1
                                                            hero_best6[0] = eval_6hand(hand[1],hand[2],hand[3],hand[4],hand[5],hand[6]);

                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Hero has [%d]", hero_best6[0]);
                                                             (*env)->SetIntArrayRegion(env, rhero_best6, 0, HSIZE6, hero_best6);
                                                             return rhero_best6;
                   }

                   jint* hero7eval(JNIEnv * env, int* hand) {

                   int HSIZE7=1;
                   jint hero_best7[HSIZE7];
                   jintArray rhero_best7; rhero_best7 = (*env)->NewIntArray(env, HSIZE7);
                   //hero_best7[0]=6186; //default=a high card

                                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);
                                       hero_best7[0] = eval_7hand(hand[0],hand[1],hand[2],hand[3],hand[4],hand[5],hand[6]);
                                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Hero has [%d]", hero_best7[0]);
                                        (*env)->SetIntArrayRegion(env, rhero_best7, 0, HSIZE7, hero_best7);
                                        return rhero_best7;

                    }

//06-aug-14 to allow clang/mllvm-obfuscate to function!
//basic instructions: http://fuzion24.github.io/android/obfuscation/ndk/llvm/o-llvm/2014/07/27/android-obfuscation-o-llvm-ndk/
///data/android-ndk-r10/platforms/android-15/arch-arm/usr/lib/../lib/crtbegin_static.o:crtbrand.c:function _start: error: undefined reference to 'main'
int main(){ return 1;}

//JNIEXPORT jint JNICALL etc....
int Java_com_droidroid_PM2_PrimaryActivity_coreEngine(JNIEnv * env, jobject this,
                                                jstring actor,
                                                jstring actorreturn,
                                                jint c5,
                                                jint c4,
                                                jint c3,
                                                jint c2,
                                                jint c1,

                                                jint c6,
                                                jint c7)
{ //main

//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Entered cEngine");


int VSIZE5=1325;  //pf-f compares villain  1325 for pf-f
int VSIZE6=1325;  //pf-f-t compares
int VSIZE7=1325;  //river

//8-aug-14
//all the available outs available to hero up to 1325
//1325+1325 is a mirror holding the outs card that reached this hand
//we split this when we get back to Java and thus we pass in one array
//8-aug-14 52 max outs of course!
int OUTS=52*2;




jint hero_outs[OUTS];


//hero best is a single int





//villain best is an array of the villains range
jint villain_best5[VSIZE5];
jint villain_best6[VSIZE6];
jint villain_best7[VSIZE7];


//returned
jintArray rhero_outs; rhero_outs = (*env)->NewIntArray(env, OUTS);






jintArray rvillain_best5; rvillain_best5 = (*env)->NewIntArray(env, VSIZE5);
jintArray rvillain_best6; rvillain_best6 = (*env)->NewIntArray(env, VSIZE6);
jintArray rvillain_best7; rvillain_best7 = (*env)->NewIntArray(env, VSIZE7);

//27-7-14
jintArray rvillain_flop6; rvillain_flop6 = (*env)->NewIntArray(env, VSIZE6*2);
jintArray rvillain_flop7; rvillain_flop7 = (*env)->NewIntArray(env, VSIZE7*2);




int suit = 0x8000;



//for debug only
                 //HEARTS

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



//6-aug-14 funcs moved

  //jboolean isCopy;
  //const char * szLogThis = (*env)->GetStringUTFChars(env, logThis, &isCopy);

  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%s]", szLogThis);
  //(*env)->ReleaseStringUTFChars(env, logThis, szLogThis);

    /*                       at flop    at t/r
           jint c5,          f3           r
           jint c4,          f2           t
           jint c3,          f1           f3
           jint c2,          pf2          f2
           jint c1,          pf1          f1

           jint c6,          --           pf2
           jint c7           --           pf1


    */
     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx  c1 [%d]", c1);
     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx  c2 [%d]", c2);

     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx   c3 [%d]", c3);
     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx   c4 [%d]", c4);
     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx   c5 [%d]", c5);

     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx    c6 [%d]", c6);
     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  cardx    c7 [%d]", c7);

  jboolean isCopy;
  const char * szActor = (*env)->GetStringUTFChars(env, actor, &isCopy);
  //27-7-14
  const char * szActorReturn = (*env)->GetStringUTFChars(env, actorreturn, &isCopy);

  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%s]", szActor);
  //27-7-14
  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%s]", szActorReturn);






      int ct;
      if (strcmp(szActor,"hero7") == 0) ct=7; else ct=5;

      int hand[ct];
      int res;



//************************ Hero ***********************/




      //5
               if (strcmp(szActor,"hero5") == 0) {
               //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: HERO5 TRACK");
                hand[0]=c1;
                hand[1]=c2;
                hand[2]=c3;
                hand[3]=c4;
                hand[4]=c5;

                                //test - should show equal for everything as the hands wil be the same!
                                //int* test = compare_two_hands(hand, hand,deck);
                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine hero5");
                return (int)hero5eval(env, hand);
                }




      //6
                if (strcmp(szActor,"hero6") == 0) {
                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: HERO6 TRACK");
                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Hero6 holds: [%d] [%d] [%d] [%d] [%d] [%d] [%d]", 0, c4, c3, c2, c1, c6, c7);
                            hand[0]=0;//r
                            hand[1]=c4; //turn at this stage
                            hand[2]=c3;//f3
                            hand[3]=c2;//f2
                            hand[4]=c1;//f1
                            hand[5]=c6;//pf2
                            hand[6]=c7;//pf1
                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine hero6");
                            return (int)hero6eval(env, hand);
                }



      //7
               if (strcmp(szActor,"hero7") == 0) {
               //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: HERO7 TRACK");
                            hand[0]=c5;//r
                            hand[1]=c4;//t
                            hand[2]=c3;//f3
                            hand[3]=c2;//f2
                            hand[4]=c1;//f1
                            hand[5]=c6;//pf2
                            hand[6]=c7;//pf1
                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine hero7");
                            return (int)hero7eval(env, hand);

                }




//************************ Outs ***********************/

                                if (strcmp(szActor,"outs5") == 0) {
                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: zzzOUTS5 TRACK");
                                                hand[0]=c1; //pf
                                                hand[1]=c2; //pf
                                                hand[2]=c3; //f
                                                hand[3]=c4; //f
                                                hand[4]=c5; //f
                                }


                                                if (strcmp(szActor,"outs6") == 0) {
                                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: OUTS6 TRACK");
                                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Outs6 holds: [%d] [%d] [%d] [%d] [%d] [%d] [%d]", 0, c4, c3, c2, c1, c6, c7);
                                                            hand[0]=0;//r
                                                            hand[1]=c4; //turn at this stage
                                                            hand[2]=c3;//f3
                                                            hand[3]=c2;//f2
                                                            hand[4]=c1;//f1
                                                            hand[5]=c6;//pf2
                                                            hand[6]=c7;//pf1
                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Entered cEngine outs6");
                                                }



//************************ Villain ***********************/


                if (strcmp(szActor,"villain5") == 0) {
                 //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: VILLAIN5 TRACK");
                                hand[0]=c1;
                                hand[1]=c2;
                                hand[2]=c3;
                                hand[3]=c4;
                                hand[4]=c5;
                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Entered cEngine villain5");

                }

                                if (strcmp(szActor,"villain6") == 0) {
                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: VILLAIN6 TRACK");
                                                           hand[0]=0;//r
                                                           hand[1]=c4; //turn at this stage
                                                           hand[2]=c3;//f3
                                                           hand[3]=c2;//f2
                                                           hand[4]=c1;//f1
                                                           hand[5]=c6;//pf2
                                                           hand[6]=c7;//pf1
                                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Entered cEngine villain6");
                                }

                                               if (strcmp(szActor,"villain7") == 0) {
                                               //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: HERO7 TRACK");
                                                            hand[0]=c5;//r
                                                            hand[1]=c4;//t
                                                            hand[2]=c3;//f3
                                                            hand[3]=c2;//f2
                                                            hand[4]=c1;//f1
                                                            hand[5]=c6;//pf2
                                                            hand[6]=c7;//pf1
                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Entered cEngine villain7");
                                                }







   //HERO5 best (enumerate single)

  if (strcmp(szActor,"hero5") == 0) {
        //stub code moved
        } //hero



      //HERO6 best (enumerate single)


        else if (strcmp(szActor,"hero6") == 0) {
              //stub code moved
              } //hero


   //HERO7 best (enumerate single)


     else if (strcmp(szActor,"hero7") == 0) {
            //stub code moved
           } //hero

   //VILLAIN5 best (enumerate all)

                else if (strcmp(szActor,"villain5") == 0)
                  {  //actor=="villain"
                  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);


                         int hh = eval_5hand_fast(hand[0],hand[1],hand[2],hand[3],hand[4]);  //disregard hero's hand

                                                                        //copy hero before mutated for equity
                                                                        int hha[8];
                                                                        hha[0]=hand[0];
                                                                        hha[1]=hand[1];
                                                                        hha[2]=hand[2];
                                                                        hha[3]=hand[3];
                                                                        hha[4]=hand[4];


                         int aa, bb, cc, dd, ee, ii, jj, nn, tmp;

                         int zr=0;
                         for (zr=0; zr<VSIZE5; zr++) villain_best5[zr]=6186; // init lowest hand by default




                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Hero holds: [%d] [%d] [%d] [%d] [%d]", c1,c2, c3, c4, c5);

                          short td;
                          int nDeck[51]={0}; //minus pf/flop cards (we don't generate these in our pf/flop combination) - we replace with a zero
                          for (td=0; td<52; ++td) {
                                                    //minus pf cards (we don't generate these in our pf/flop combination)
                                                    //we also remove our fixed flop cards from the pf cards as we can't double up (note: &&,|| had issues)

                                                            if (c1 != deck[td]) {
                                                               if (c2 != deck[td]){
                                                                  if (c3 != deck[td]){
                                                                     if (c4 != deck[td]){
                                                                        if (c5 != deck[td]){
                                                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: deck [%d] store is [%d]", td, deck[td]);
                                                                           nDeck[td] = deck[td];
                                                                        }//c5
                                                                     }//c4
                                                                  } //c3
                                                               } //c2
                                                            }//c1

                                                  } //td loop

                                                  //for (td=0; td<52; ++td) __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: nDeck is [%d] [%d] [%s]", td, nDeck[td], adeck[td]);


//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: Test1");

                     		                            int card1, card2, hc = 0;

                     		                            for (card1=0; card1 < 52; card1++) {  //iterate through entire new deck of 52 cards  when no zeros in ndeck (- hero's 2 flop cards - 3 community)
                     		                            //first card
                     		                                hand[0] = nDeck[card1]; //pf - our first villain card

//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: Test2");
                     		                                    for (card2=card1+1; card2 < 52; card2++) {
                     		                                        hand[1] = nDeck[card2]; //pf - our second villain card


                                                                    //res                   pf       pf       f      f      f
                     		                                        if (hand[0]>0 && hand[1]>0) tmp = eval_5hand_fast( hand[0],hand[1], hand[2],hand[3],hand[4] );


//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: Test3");


//11-aug-14, exhaustive test
//if (card1==1 && card2==2) {int* test = compare_two_hands(hha, hand,deck);}

//__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "zEQUITY2 NDK:LC: Exited Equity Exhaustive with initial [%d] vs. [%d] - the result was %d/%d/%d",hh,tmp,test[0],test[1],test[2]);

                                                                //    if (tmp != hh) { //disregard hh entire hand

                                                                            //if (tmp < best2[0]) { //only add if a better hand
                                                                            //best2[0]=tmp;

                                                                            //if (hand[0]>0 && hand[1]>0)
                                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d] Villain5 best cards are pf: [%d] [%d] [%d] [%d] [%d] rank: [%d]", hc, card1,card2, hand[2], hand[3], hand[4], tmp);


                                                                            //}//best


                                                                            //ignore any returns that have our flop cards in the preflop hand
                                                                            //as this duplicates the heros hand






                                                                            //grab all comparisons (ignore no matches)
                                                                            //15-aug-14 temp fix - ignore >7462 see PrimaryActivity.scala approx line 1628
                                                                           if ((nDeck[card1]>0) && (nDeck[card2]>0) && tmp<7463 && tmp>0)  {
                                                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d] - Villain5 match stored rank [%d]", hc,tmp);
                                                                           villain_best5[hc]=tmp;

                                                                           //}
                                                                           hc+=1;

                     		                                        } //tmp=hh
                     		                                    } //inner 50

                                                        } //outer 50
                                                        //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Villain total hands compared [%d]",hc);

                     //need to reverse on return
                     //if (strcmp(szActor,"villain5") == 0)  __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: We selected [%s]", szActor);
                     // move from the temp structure to the java structure

                     //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "EQUITY NDK:LC: finished equity analysis also! ");
                    //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine villain5");
                     (*env)->SetIntArrayRegion(env, rvillain_best5, 0, VSIZE5, villain_best5);
                     return (int)rvillain_best5;



            } //villain5


    //TODO NOW
   //VILLAIN7 best (enumerate all)
   //TODO: V6/V7 for enumeration
   //15-2-14 V6
     else if (strcmp(szActor,"villain6") == 0)
                       {  //actor=="villain"
                       //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);

                              int hh = eval_7hand(hand[6],hand[5],hand[4],hand[3],hand[2],hand[1],hand[0]);  //disregard hero's hand

                              int aa, bb, cc, dd, ee, ii, jj, nn, tmp;

                              int best6[VSIZE6]; //for 6 card eval
                              //27-7-14
                              int flop6[VSIZE6*2];
                              int zr=0;
                              for (zr=0; zr<VSIZE6; zr++) best6[zr]=6186; // init lowest hand by default



                                                                                                                                          //pf pf  f   f   f  t
                                //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: cardx V6 holds: [%d] [%d] [%d] [%d] [%d] [%d]", c7,c6, c1, c2, c3, c4);



                                /*
                                                                            hand[0]=0;//r
                                                                            hand[1]=c4; //turn at this stage
                                                                            hand[2]=c3;//f3
                                                                            hand[3]=c2;//f2
                                                                            hand[4]=c1;//f1
                                                                            hand[5]=c6;//pf2
                                                                            hand[6]=c7;//pf1
                                */

                               short td;
                               int nDeck[51]={0}; //minus pf/flop cards (we don't generate these in our pf/flop combination) - we replace with a zero
                               for (td=0; td<52; ++td) {
                     //minus pf cards (we don't generate these in our pf/flop combination)
                     //we also remove our fixed flop cards from the pf cards as we can't double up (note: &&,|| had issues)

                             if (c7 != deck[td]) { //pf
                                if (c6 != deck[td]){  //pf

                                   if (c1 != deck[td]){ //f
                                      if (c2 != deck[td]){  //f
                                         if (c3 != deck[td]){  //f

                                            if (c4 != deck[td]){   //t

                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: villain6 deck [%d] store is [%d]", td,deck[td]);
                                            nDeck[td] = deck[td];
                                          }//c4
                                         }//c3
                                      }//c2
                                   } //c1
                                } //c6
                             }//c7

                   } //td loop

                   //for (td=0; td<52; ++td) __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: villain6 nDeck is [%d] [%d] [%s]", td, nDeck[td], adeck[td]);



                        int card1, card2, card3, hc=0, hf=0;
                        int vtop = 9999;

                        for (card1=0; card1 < 52; card1++) {  //iterate through entire new deck of 52 cards  when no zeros in ndeck (- hero's 2 flop cards - 3 community)
                        //first card
                            hand[5] = nDeck[card1]; //pf - our first villain card


                                for (card2=card1+1; card2 < 52; card2++) {
                                    hand[6] = nDeck[card2]; //pf - our second villain card


                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG,"NDK:LC [%d] Villain6 card1=[%d] card2=[%d]",hc, card1,card2);

                                                                              //pf    //pf     //f        //f      //f     //t
                                                            if (strcmp(szActorReturn,"preflop") != 0) tmp = eval_6hand( hand[6],hand[5], hand[4],hand[3],hand[2],hand[1]);

                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: EVAL6 villain6 nDeck is [%d] [%d] - [%d] [%d] [%d] - [%d] [%d] vresult=[%d]", hand[6],hand[5], hand[4],hand[3],hand[2],hand[1],hand[0],tmp);


                                 //    if (tmp != hh) { //disregard hh entire hand

                                             //if (tmp < best2[0]) { //only add if a better hand
                                             //best2[0]=tmp;
                                             //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d]. Villain6 best cards at turn are: [%s] [%s] - [%d] [%d] [%d] - [%s] [%d] - rank: [%d]", hc, adeck[card1],adeck[card2], hand[4], hand[3], hand[2], adeck[card3] , hand[0], tmp);
                                             //}//best


                                             //ignore any returns that have our flop cards in the preflop hand
                                             //as this duplicates the heros hand





                                            if (strcmp(szActorReturn,"preflop") == 0)
                                            {
                                             //27-67-14
                                             flop6[hf]=hand[6];
                                             flop6[hf+1]=hand[5];
                                             hf+=2;
                                            }
                                            else
                                            //grab all comparisons (ignore no matches)
                                            //15-aug-14 temp fix - ignore >7462 see PrimaryActivity.scala approx line 1628
                                            //24-aug-14
                                            if ((nDeck[card1]>0) && (nDeck[card2]>0) && tmp<7463 && tmp>0)  {
                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Villain match stored rank [%d]", tmp);
                                                best6[hc]=tmp;
                                                if (tmp < vtop) vtop=tmp;
                                                hc+=1; //moved into conditional
                                            }

                                //    } //tmp=hh



                                } //inner 50

                         } //outer 50
                         //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Villain6 total hands compared [%d] highest is [%d]",hc,vtop);


                          //need to reverse on return
                          //if (strcmp(szActor,"villain6") == 0)  __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: We selected [%s]", szActor);

                          //27-7-14
                          if (strcmp(szActorReturn,"preflop") == 0) {
                                        // move from the temp structure to the java structure
                                                    (*env)->SetIntArrayRegion(env, rvillain_flop6, 0, VSIZE6*2, flop6);
                                                    //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine villain6 pf");
                                                    return (int)rvillain_flop6;
                          }

                          else {        //default villain6
                                        // move from the temp structure to the java structure
                                                      (*env)->SetIntArrayRegion(env, rvillain_best6, 0, VSIZE6, best6);
                                                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine villain6");
                                                      return (int)rvillain_best6;
                          }

//zzzz
                 } //villain6
                      else if (strcmp(szActor,"villain7") == 0)
                                        {  //actor=="villain"
                                        //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);

                                               int hh = eval_7hand(hand[6],hand[5],hand[4],hand[3],hand[2],hand[1],hand[0]);  //disregard hero's

                                               int aa, bb, cc, dd, ee, ii, jj, nn, tmp;

                                               int best7[VSIZE7]; //for 6 card eval

                                               //27-7-14
                                               int flop7[VSIZE7*2];

                                               int zr=0;
                                               for (zr=0; zr<VSIZE7; zr++) best7[zr]=6186; // init lowest hand by default


                                                                                                                                                                         //pf pf  f   f   f  t
                                                 //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: cardx V6 holds: [%d] [%d] [%d] [%d] [%d] [%d]", c7,c6, c1, c2, c3, c4);



                                                 /*
                                                                                             hand[0]=0;//r
                                                                                             hand[1]=c4; //turn at this stage
                                                                                             hand[2]=c3;//f3
                                                                                             hand[3]=c2;//f2
                                                                                             hand[4]=c1;//f1
                                                                                             hand[5]=c6;//pf2
                                                                                             hand[6]=c7;//pf1
                                                 */

                        short td;
                        int nDeck[51]={0}; //minus pf/flop cards (we don't generate these in our pf/flop combination) - we replace with a zero
                        for (td=0; td<52; ++td) {
                                                  //minus pf cards (we don't generate these in our pf/flop combination)
                                                  //we also remove our fixed flop cards from the pf cards as we can't double up (note: &&,|| had issues)

                                                          if (c7 != deck[td]) { //pf
                                                             if (c6 != deck[td]){  //pf

                                                                if (c1 != deck[td]){ //f
                                                                   if (c2 != deck[td]){  //f
                                                                      if (c3 != deck[td]){  //f

                                                                         if (c4 != deck[td]){   //t

                                                                                 if (c5 != deck[td]){   //r

                                                                         //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: villain7 deck [%d] store is [%d]", td,deck[td]);
                                                                         nDeck[td] = deck[td];
                                                                         } //c5
                                                                       }//c4
                                                                      }//c3
                                                                   }//c2
                                                                } //c1
                                                             } //c6
                                                          }//c7

                                                } //td loop

                                                                        //for (td=0; td<52; ++td) __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: villain7 nDeck is [%d] [%d] [%s]", td, nDeck[td], adeck[td]);



                        int card1, card2, card3, hc=0, hf=0;
                        int vtop = 9999;

                        for (card1=0; card1 < 52; card1++) {  //iterate through entire new deck of 52 cards  when no zeros in ndeck (- hero's 2 flop cards - 3 community)
                        //first card
                        hand[5] = nDeck[card1]; //pf - our first villain card


                        for (card2=card1+1; card2 < 52; card2++) {
                        hand[6] = nDeck[card2]; //pf - our second villain card


                             //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG,"NDK:LC [%d] Villain7 card1=[%d] card2=[%d]",hc, card1,card2);

                                               //pf    //pf     //f        //f      //f     //t    //r
                             if (strcmp(szActorReturn,"preflop") != 0) tmp = eval_7hand( hand[6],hand[5], hand[4],hand[3],hand[2],hand[1],hand[0]);

                             //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: EVAL7 villain7 nDeck is [%d] [%d] - [%d] [%d] [%d] - [%d] [%d] vresult=[%d]", hand[6],hand[5], hand[4],hand[3],hand[2],hand[1],hand[0],tmp);



                        //    if (tmp != hh) { //disregard hh entire hand

                        //if (tmp < best2[0]) { //only add if a better hand
                        //best2[0]=tmp;
                        //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d]. Villain6 best cards at turn are: [%s] [%s] - [%d] [%d] [%d] - [%s] [%d] - rank: [%d]", hc, adeck[card1],adeck[card2], hand[4], hand[3], hand[2], adeck[card3] , hand[0], tmp);
                        //}//best


                        //ignore any returns that have our flop cards in the preflop hand
                        //as this duplicates the heros hand





                        if (strcmp(szActorReturn,"preflop") == 0)
                        {
                        //27-67-14
                        flop7[hf]=hand[6];
                        flop7[hf+1]=hand[5];
                        hf+=2;
                        }
                        else
                        //grab all comparisons (ignore no matches)
                         //grab all comparisons (ignore no matches)
                        //15-aug-14 temp fix - ignore >7462 see PrimaryActivity.scala approx line 1628
                        if ((nDeck[card1]>0) && (nDeck[card2]>0) && tmp<7463 && tmp>0) {
                        //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Villain match stored rank [%d]", tmp);
                        best7[hc]=tmp;
                        if (tmp < vtop) vtop=tmp;
                        hc+=1; //moved into conditional
                        }




                        //    } //tmp=hh



                            } //inner 50

                      } //outer 50
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Villain6 total hands compared [%d] highest is [%d]",hc,vtop);


                       //need to reverse on return
                       //if (strcmp(szActor,"villain7") == 0)  __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: We selected [%s]", szActor);


                       //27-7-14
                                                 if (strcmp(szActorReturn,"preflop") == 0) {
                                                               // move from the temp structure to the java structure
                                                                           (*env)->SetIntArrayRegion(env, rvillain_flop7, 0, VSIZE7*2, flop7);
                                                                           //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine villain7 pf");
                                                                           return (int)rvillain_flop7;
                                                 }

                                                 else {        //default villain6
                                                               // move from the temp structure to the java structure
                                                                             (*env)->SetIntArrayRegion(env, rvillain_best7, 0, VSIZE7, best7);
                                                                             //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine villain7");
                                                                             return (int)rvillain_best7;
                                                 }



                                  } //villain7






//outs for Hero flop,turn and river here 18-2-14

                                                        else if (strcmp(szActor,"outs5") == 0 || strcmp(szActor,"outs6") == 0 ) {
                                                        //calculate the outs array for the flop
                                                        //initially turn/river combined
                                                        //we pass back the array for sorting/collating in Scala

                  //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: branching [%s]", szActor);
//1-8-14 bug fix? FROM 6 HAND WITH 0 ARG TO 5 HAND
                          int hh;

                          if (strcmp(szActor,"outs5") == 0) hh= eval_5hand_fast(hand[1],hand[0],
                                                                                hand[4],hand[3],hand[2]);  //disregard hero's hand
                                                                                //h5=turn

                          if (strcmp(szActor,"outs6") == 0) hh = eval_6hand(    hand[5],hand[6],
                                                                                hand[4],hand[3],hand[2],
                                                                                hand[1]);  //disregard hero's hand
                                                                                //h0=river


                          int aa, bb, cc, dd, ee, ii, jj, nn, tmp;

                          int outs[OUTS];
                          int zr=0;
                          for (zr=0; zr<OUTS; zr++) outs[zr]=6186; // init lowest hand by default

                                                                                                                                      //pf pf  f   f   f  t
                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: cardx V6 holds: [%d] [%d] [%d] [%d] [%d] [%d]", c7,c6, c1, c2, c3, c4);



                            /*
                                                                        hand[0]=0;//r
                                                                        hand[1]=c4; //turn at this stage
                                                                        hand[2]=c3;//f3
                                                                        hand[3]=c2;//f2
                                                                        hand[4]=c1;//f1
                                                                        hand[5]=c6;//pf2
                                                                        hand[6]=c7;//pf1
                            */


                           int td=0;
                           int maxcard=0;
                           int nDeck[51]={0}; //minus pf/flop cards (we don't generate these in our pf/flop combination) - we replace with a zero
                           if (strcmp(szActor,"outs5") == 0) for (td=0; td<52; td++)
                                                     //minus pf cards (we don't generate these in our pf/flop combination)
                                                     //we also remove our fixed flop cards from the pf cards as we can't double up (note: &&,|| had issues)
                                                     {
                                                             if (c1 != deck[td]) { //pf
                                                                if (c2 != deck[td]){  //pf
                                                                   if (c3 != deck[td]){ //f
                                                                      if (c4 != deck[td]){  //f
                                                                         if (c5 != deck[td]){  //f
                                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: outs deck [%d] store is [%d]", td, deck[td]);
                                                                            nDeck[td] = deck[td];
                                                                            maxcard++;
                                                                         }//c5
                                                                      }//c4
                                                                   } //c3
                                                                } //c2
                                                             }//c1
                                                     }

                                                     else for (td=0; td<52; td++) //must be outs6
                                                     {
                                                              if (c1 != deck[td]) {
                                                                 if (c2 != deck[td]){
                                                                    if (c3 != deck[td]){
                                                                       if (c4 != deck[td]){
                                                                          if (c7 != deck[td]){
                                                                            if (c6 != deck[td]) {
                                                                             //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: outs deck [%d] store is [%d]", td,deck[td]);
                                                                             nDeck[td] = deck[td];
                                                                             maxcard++;
                                                                            }//c6
                                                                          }//c7
                                                                       }//c4
                                                                    } //c3
                                                                 } //c2
                                                              }//c1
                                                     }

                                                     //will reach to 49 (actual 50) on turn.. -2 preflop cards



                                                   //for (td=0; td<52; ++td) __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG," NDK:LC: outs nDeck is [%d] [%d] [%s]", td, nDeck[td], adeck[td]);



                                                        int card1, hc=0;
                                                        int vtop = 9999;

                                                        //24-aug-14 maxcard fed from the valid deck, not 52 in valid deck and after 50 corrupt...

                                                         //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d]. Outs5/6 maxcard=[%d]",maxcard,maxcard);

                                                        for (card1=0; card1 < 52; card1++) {  //iterate thru entire new deck of 52 cards  when no zeros in ndeck (- hero's 2 flop cards - 3 community)
                                                        //first card

                                                            if (strcmp(szActor,"outs5") == 0) {
                                                            hand[5] = nDeck[card1]; //t
                                                            tmp = eval_6hand( hand[5], hand[4],hand[3],hand[2],hand[1],hand[0]);
                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d]. Outs5 best cards at turn are: [%d] [%d] - [%d] [%d] [%d] - [%d] - rank: [%d]", hc, hand[0], hand[1], hand[2], hand[3], hand[4] , hand[5], tmp);
                                                            }

                                                            if (strcmp(szActor,"outs6") == 0) {
                                                            hand[0] = nDeck[card1]; //r
                                                            tmp = eval_7hand( hand[6],hand[5], hand[4],hand[3],hand[2],hand[1],hand[0]);
                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: [%d]. Outs6 best cards at turn are: [%d] [%d] - [%d] [%d] [%d] - [%d] [%d] - rank: [%d]", hc, hand[6], hand[5], hand[4], hand[3], hand[2], hand[1] , hand[0], tmp);
                                                            }

                                                            //24-aug-14 added conditionals
                                                            //24-aug-14 added don't return high cards 7463 to 6186
                                                            if ((nDeck[card1]>0) && tmp<6186 && tmp>0)  {
                                                            //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: Outs5 rank [%d]", tmp);
                                                            outs[hc]=tmp;
                                                            //8-aug-14 added card that produces this result (the out)
                                                            outs[hc+(OUTS/2)]=nDeck[card1];
                                                            if (tmp < vtop) vtop=tmp;
                                                            }

                                                            hc+=1;

                                                         } //outer 50
                                                         //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: outs5 total hands compared [%d] highest is [%d]",hc,vtop);


                      //need to reverse on return
                      //if (strcmp(szActor,"outs5") == 0)  __android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC: We selected [%s]", szActor);
                      // move from the temp structure to the java structure
                      //__android_log_print(ANDROID_LOG_DEBUG, DEBUG_TAG, "NDK:LC:  Exit cEngine hero outs5/6");
                      (*env)->SetIntArrayRegion(env, rhero_outs, 0, OUTS, outs);
                      return (int)rhero_outs;


                                                        }


} //main