package com.droidroid.PM2

//import android.util.Log
//import com.droidroid.PM2.ObjectUtils

/*
 * Date: 1/15/14
 * Time: 9:10 PM
 */


/*
        169 pre-flop combinations  (52x51/2=1326, ignore order = 169, as As Ac same as Ah Ad etc.)

        flop
        19,600 if you include your two hole cards information =  (50 | 3 ) 50x49x48 / 3 x 2 x 1 = 19,600  from http://en.wikipedia.org/wiki/Combination
        22,100 if you exclude your two hole cards information =  (52 | 3) 52x51x50 / 3 x 2 x 1 = 22,100

        so we need to work with 19,600 flop combinations x169 pre-flop combinations = 3,312,400 possible flop combinations verified from http://www.liacs.nl/assets/Bachelorscripties/2006-09RvdZwanSHenstra.pdf

        Actually (52 | 5) = ${{52}\choose{5}} = 2,598,960 according to http://people.math.sfu.ca/~alspach/comp18/
        Unique however After enumerating and collapsing all the 2.6 million unique five-card poker hands, we wind up with just 7462 distinct poker hand values
 */


 /* References

 Opponent Modeling in Poker - Darse Billings, Denis Papp, Jonathan Schaeffer, Duane Szafron

 Artiﬁcial intelligence and data mining applied to no-limit Texas Hold’em Sjoerd Henstra & Robin van der Zwan
 http://www.liacs.nl/assets/Bachelorscripties/2006-09RvdZwanSHenstra.pdf



  */


object PokerAlgs {

  val TAG = "PokerAlgs"

  final val Alg_TenMax:Int = 1
  final val Alg_SixMax:Int = 2
  final val Alg_Random:Int = 3
  final val Alg_ThreeMax:Int = 4


  final val Random_pfCutoff = 82 //% = implies 18% tight
  final val TenMax_pfCutoff = 82 //% = implies 18% tight
  final val Six_Max_pfCutoff = 82 //% = implies 18% tight
  final val Chen_pfCutoff = 10 //% TODO



  //module: Probability of receiving a specific hand with 5 Cards (2 preflop, 3 flop) (2,598,960 samples)
  //http://people.math.sfu.ca/~alspach/comp18/
  //http://www.cwu.edu/~glasbys/POKER.HTM

/*
The number of ways to choose 5 cards from a deck of 52 cards is (52 choose 5) = 2,598,960.

Obviously doesn't take pf/flop history into account (NOR T/R). Therefore really is only indicative of random distribution.
                                          Approximate
Poker Hand       Probability of Hand       Probability (2 d.p. denominators)
---------------  ----------------------   ------------
Royal Flush:             4 in 2,598,960 =  1/649,740
Straight Flush:         36 in 2,598,960 =  1/72,193.33   //40 in alspach!
Four of a Kind:        624 in 2,598,960 =  1/4,165
Full House:          3,744 in 2,598,960 =  1/694.17
Flush:               5,108 in 2,598,960 =  1/508.80
Straight:           10,200 in 2,598,960 =  1/255.8
Three of a Kind:    54,912 in 2,598,960 =  1/47.33
Two Pair:          123,552 in 2,598,960 =  1/21.04
One Pair:        1,098,240 in 2,598,960 =  1/2.37
Nothing:         1,302,540 in 2,598,960 =  1/1.96
*/


  //module: Equivalence Class Hand Strength Evaluator (via NDK code)

  def htable(theCards:Array[String]): Array[Int] = {   //TODO: slit into two algs htable5 and htable7 - not this convoluted logic


    //needs to return hero's highest available hand
    //and villains
    //to achieve this run with the 169 (-1) available pairs that the villain may hold and then sort into relative rank and %chance of holding


    //format is array Array(5) of pf,f items in std form V S
    //first we need to break into values

    val tfivCards_v: Array[String] = Array.fill[String](5)("")
    val tfivCards_s: Array[String] = Array.fill[String](5)("")

    val tsevCards_v: Array[String] = Array.fill[String](7)("")
    val tsevCards_s: Array[String] = Array.fill[String](7)("")

    var cl = List[Tuple2[String,String]]()



    if (theCards(5) == "") { //flop
    //clear the tuple
    cl = List()


    ObjectUtils.debugLog(TAG, "5 Card htable Evaluation ")
    //pf
    val c1 = theCards(0)
    val c2 = theCards(1)


    //f
    val c3 = theCards(2)
    val c4 = theCards(3)
    val c5 = theCards(4)

    //if we are scrolling back from a River we need to repopulate the pre/flop differently



    if (theCards(6) != "") {

      val c1 = theCards(2)
      val c2 = theCards(3)

      val c3 = theCards(4)
      val c4 = theCards(5)
      val c5 = theCards(6)

    }



    tfivCards_v(0) = c1.drop(1).trim
    tfivCards_s(0)= c1.dropRight(1).trim

    tfivCards_v(1) = c2.drop(1).trim
    tfivCards_s(1) = c2.dropRight(1).trim

    tfivCards_v(2) = c3.drop(1).trim
    tfivCards_s(2) = c3.dropRight(1).trim

    tfivCards_v(3) = c4.drop(1).trim
    tfivCards_s(3) = c4.dropRight(1).trim

    tfivCards_v(4) = c5.drop(1).trim
    tfivCards_s(4) = c5.dropRight(1).trim

    cl ::= Tuple2(tfivCards_s(0),tfivCards_v(0))
    cl ::= Tuple2(tfivCards_s(1),tfivCards_v(1))
    cl ::= Tuple2(tfivCards_s(2),tfivCards_v(2))
    cl ::= Tuple2(tfivCards_s(3),tfivCards_v(3))
    cl ::= Tuple2(tfivCards_s(4) ,tfivCards_v(4))


    }//5

    if (theCards(6) != "") { //flop
      //clear the tuple
      cl = List()

      ObjectUtils.debugLog(TAG, "7 Card htable Evaluation ")
      //pf
      val c1 = theCards(0)
      val c2 = theCards(1)
      //f
      val c3 = theCards(2)
      val c4 = theCards(3)
      val c5 = theCards(4)
      //tr
      val c6 = theCards(5)
      val c7 = theCards(6)

      tsevCards_v(0) = c1.drop(1).trim
      tsevCards_s(0)= c1.dropRight(1).trim

      tsevCards_v(1) = c2.drop(1).trim
      tsevCards_s(1) = c2.dropRight(1).trim

      tsevCards_v(2) = c3.drop(1).trim
      tsevCards_s(2) = c3.dropRight(1).trim

      tsevCards_v(3) = c4.drop(1).trim
      tsevCards_s(3) = c4.dropRight(1).trim

      tsevCards_v(4) = c5.drop(1).trim
      tsevCards_s(4) = c5.dropRight(1).trim

      tsevCards_v(5) = c6.drop(1).trim
      tsevCards_s(5) = c6.dropRight(1).trim

      tsevCards_v(6) = c7.drop(1).trim       //23-jan-14 corrected
      tsevCards_s(6) = c7.dropRight(1).trim

      cl ::= Tuple2(tsevCards_s(0),tsevCards_v(0))
      cl ::= Tuple2(tsevCards_s(1),tsevCards_v(1))
      cl ::= Tuple2(tsevCards_s(2),tsevCards_v(2))
      cl ::= Tuple2(tsevCards_s(3),tsevCards_v(3))
      cl ::= Tuple2(tsevCards_s(4) ,tsevCards_v(4))
      cl ::= Tuple2(tsevCards_s(5) ,tsevCards_v(5))
      cl ::= Tuple2(tsevCards_s(6) ,tsevCards_v(6))


    }//7

   else if (theCards(5) != "") { //turn
      //clear the tuple
      cl = List()

      ObjectUtils.debugLog(TAG, "6 Card htable Evaluation ")
      //pf
      val c1 = theCards(0)
      val c2 = theCards(1)
      //f
      val c3 = theCards(2)
      val c4 = theCards(3)
      val c5 = theCards(4)
      //tr
      val c6 = theCards(5)


      tsevCards_v(0) = c1.drop(1).trim
      tsevCards_s(0)= c1.dropRight(1).trim

      tsevCards_v(1) = c2.drop(1).trim
      tsevCards_s(1) = c2.dropRight(1).trim

      tsevCards_v(2) = c3.drop(1).trim
      tsevCards_s(2) = c3.dropRight(1).trim

      tsevCards_v(3) = c4.drop(1).trim
      tsevCards_s(3) = c4.dropRight(1).trim

      tsevCards_v(4) = c5.drop(1).trim
      tsevCards_s(4) = c5.dropRight(1).trim

      tsevCards_v(5) = c6.drop(1).trim
      tsevCards_s(5) = c6.dropRight(1).trim


      cl ::= Tuple2(tsevCards_s(0),tsevCards_v(0))
      cl ::= Tuple2(tsevCards_s(1),tsevCards_v(1))
      cl ::= Tuple2(tsevCards_s(2),tsevCards_v(2))
      cl ::= Tuple2(tsevCards_s(3),tsevCards_v(3))
      cl ::= Tuple2(tsevCards_s(4) ,tsevCards_v(4))
      cl ::= Tuple2(tsevCards_s(5) ,tsevCards_v(5))


    }//6






      ObjectUtils.debugLog(TAG,"ndk ds:"+cl)

   def gethtableValue(clz:List[Tuple2[String,String]]): Array[Int] = {  //gcv

     val y:Array[Int]=Array.fill[Int](7)(0)  //var to val 28-01-14
     var z:Int=0

     for (cc <- clz) {
       cc match {
                 //HEARTS
                  case ("2","H") => y(z)  =98306    //2
                  case ("3","H") => y(z)  =164099    //3
                  case ("4","H") => y(z)  =295429    //4
                  case ("5","H") => y(z)  =557831    //5
                  case ("6","H") => y(z)  =1082379    //6
                  case ("7","H") => y(z)  =2131213    //7
                  case ("8","H") => y(z)  =4228625    //8
                  case ("9","H") => y(z)  =8423187    //9
                  case ("T","H") => y(z)  =16812055    //T
                  case ("J","H") => y(z)  =33589533    //J
                  case ("Q","H") => y(z)  =67144223   //Q
                  case ("K","H") => y(z)  =134253349  //K
                  case ("A","H") => y(z)  =268471337  //A

                  //DIAMONDS
                  case ("2","D") => y(z)  =81922
                  case ("3","D") => y(z)  =147715
                  case ("4","D") => y(z)  =279045
                  case ("5","D") => y(z)  =541447
                  case ("6","D") => y(z)  =1065995
                  case ("7","D") => y(z)  =2114829
                  case ("8","D") => y(z)  =4212241
                  case ("9","D") => y(z)  =8406803
                  case ("T","D") => y(z)  =16795671
                  case ("J","D") => y(z)  =33573149
                  case ("Q","D") => y(z)  =67127839
                  case ("K","D") => y(z)  =134236965
                  case ("A","D") => y(z)  =268454953

                  //SPADES
                  case ("2","S") => y(z)  =73730
                  case ("3","S") => y(z)  =139523
                  case ("4","S") => y(z)  =270853
                  case ("5","S") => y(z)  =533255
                  case ("6","S") => y(z)  =1057803
                  case ("7","S") => y(z)  =2106637
                  case ("8","S") => y(z)  =4204049
                  case ("9","S") => y(z)  =8398611
                  case ("T","S") => y(z)  =16787479
                  case ("J","S") => y(z)  =33564957
                  case ("Q","S") => y(z)  =67119647
                  case ("K","S") => y(z)  =134228773
                  case ("A","S") => y(z)  =268446761

                  //CLUBS
                  case ("2","C") => y(z)  =69634
                  case ("3","C") => y(z)  =135427
                  case ("4","C") => y(z)  =266757
                  case ("5","C") => y(z)  =529159
                  case ("6","C") => y(z)  =1053707
                  case ("7","C") => y(z)  =2102541
                  case ("8","C") => y(z)  =4199953
                  case ("9","C") => y(z)  =8394515
                  case ("T","C") => y(z)  =16783383
                  case ("J","C") => y(z)  =33560861
                  case ("Q","C") => y(z)  =67115551
                  case ("K","C") => y(z)  =134224677
                  case ("A","C") => y(z)  =268442665

                  case (_,_) => { y(z)=0 }    //not reachable I assume

                 } //match
                z+=1
                ObjectUtils.debugLog(TAG," ndk matching "+cc+" to ="+y(0)+"/"+y(1)+"/"+y(2)+"/"+y(3)+"/"+y(4)+"/"+y(5)+"/"+y(6))
                } //for
            y
            } //gcv

    gethtableValue(cl)



  } //htable

  //module: Determine the Sklansky_Chubukov Ranking

  //Sklanksy-Chubukov numbers are based on what you opponent would do **if you're hand was face up** (so they'd call with a perfect range against your hand).
  //hand is your hand, n_call, n_fold, the number of times your opponent would call/fold you if they knew your hand
  //P|Call is the probability you would win if you were called.


  //N_call and N_fold are the number of hands that will call/fold given that you move in with the maximum stack that you will do so with.
  //P|call is the probability of winning given that you are called (plus 1/2 the probability of tieing). The last number is the original question (max stack for ev>0)
  def sklansky(pfCards: Array[String]) {
  /*
  val sklansky_chubukov_Ranking = (
    ("AA",1,1224,0.500000,Inf),
    ("KK",7,1218,0.226177,953.995465),
    ("AKs",75,1150,0.457697,554.509992),
    ("QQ",13,1212,0.207007,478.008197),
    ("AKo",79,1146,0.433132,331.887184),
    ("JJ",19,1206,0.201104,319.213589),
    ("AQs",84,1141,0.424149,274.211191),
    ("TT",25,1200,0.198947,239.821017),
    ("AQo",93,1132,0.403144,192.670217),
    ("99",31,1194,0.197142,191.413933),
    ("AJs",96,1129,0.401528,183.221336),
    ("88",41,1184,0.226651,159.296894),
    ("ATs",108,1117,0.385544,138.913083),
    ("AJo",105,1120,0.379834,136.310470),
    ("77",61,1164,0.285621,134.847705),
    ("66",103,1122,0.355264,115.348532),
    ("ATo",117,1108,0.362908,106.264712),
    ("A9s",123,1102,0.367405,104.124788),
    ("55",153,1072,0.389493,98.629873),
    ("A8s",135,1090,0.361211,89.865649),
    ("KQs",256,969,0.429500,86.627695),
    ("44",275,950,0.431528,81.979590),
    ("A9o",129,1096,0.339884,81.716196),
    ("A7s",147,1078,0.356565,79.175905),
    ("KJs",265,960,0.419399,72.621257),
    ("A5s",171,1054,0.367031,72.292128),
    ("A8o",141,1084,0.332789,70.956513),
    ("A6s",159,1066,0.352858,70.744533),
    ("A4s",183,1042,0.366358,66.650529),
    ("33",455,770,0.454268,65.440821),
    ("KTs",277,948,0.411707,62.805558),
    ("A7o",155,1070,0.329722,62.747746),
    ("A3s",195,1030,0.366882,62.275315),
    ("KQo",265,960,0.400723,58.771664),
    ("A2s",207,1018,0.366815,58.141993),
    ("A5o",181,1044,0.340952,56.542087),
    ("A6o",171,1054,0.329477,56.151230),
    ("A4o",202,1023,0.347061,51.939490),
    ("KJo",277,948,0.391325,50.838788),
    ("QJs",418,807,0.432774,49.515440),
    ("A3o",220,1005,0.351305,48.445438),
    ("22",709,516,0.467553,48.054119),
    ("K9s",295,930,0.392879,47.812358),
    ("A2o",240,985,0.355839,45.172344),
    ("KTo",289,936,0.383383,44.946538),
    ("QTs",430,795,0.426952,43.809464),
    ("K8s",307,918,0.378141,39.910810),
    ("K7s",325,900,0.378587,37.330652),
    ("JTs",570,655,0.440073,36.106522),
    ("K9o",301,924,0.361114,35.754152),
    ("K6s",337,888,0.375940,34.890001),
    ("QJo",433,792,0.404082,32.816822),
    ("Q9s",457,768,0.409880,32.519706),
    ("K5s",349,876,0.371933,32.303331),
    ("K8o",324,901,0.351582,30.473887),
    ("K4s",367,858,0.371425,30.163283),
    ("QTo",445,780,0.398126,29.716401),
    ("K7o",344,881,0.353033,28.541184),
    ("K3s",379,846,0.369025,28.381805),
    ("K2s",394,831,0.367883,26.730843),
    ("Q8s",469,756,0.394731,26.718552),
    ("K6o",368,857,0.355714,26.675708),
    ("J9s",597,628,0.422213,25.712524),
    ("K5o",408,817,0.363569,24.680974),
    ("Q9o",459,766,0.377014,23.419539),
    ("JTo",585,640,0.411106,23.085252),
    ("K4o",458,767,0.373684,22.845021),
    ("Q7s",484,741,0.381931,22.685237),
    ("T9s",721,504,0.434081,22.491482),
    ("Q6s",499,726,0.382276,21.785164),
    ("K3o",508,717,0.383123,21.392219),
    ("J8s",609,616,0.406766,20.636243),
    ("Q5s",514,711,0.379236,20.321860),
    ("K2o",555,670,0.389958,19.999415),
    ("Q8o",479,746,0.363775,19.819326),
    ("Q4s",547,678,0.381543,18.916352),
    ("J9o",597,628,0.389470,17.799380),
    ("Q3s",568,657,0.380696,17.734011),
    ("T8s",733,492,0.418399,17.465705),
    ("J7s",624,601,0.393116,17.194521),
    ("Q7o",520,705,0.359844,17.077335),
    ("Q2s",591,634,0.380441,16.641032),
    ("Q6o",566,659,0.370110,16.295139),
    ("98s",841,384,0.427277,15.293343),
    ("Q5o",652,573,0.386607,15.034981),
    ("J8o",613,612,0.374112,14.867761),
    ("T9o",721,504,0.402190,14.832206),
    ("J6s",648,577,0.383218,14.718597),
    ("T7s",748,477,0.404171,14.199426),
    ("J5s",686,539,0.388455,14.048416),
    ("Q4o",748,477,0.400659,13.662167),
    ("J4s",751,474,0.396332,12.955471),
    ("J7o",657,568,0.368521,12.666038),
    ("Q3o",857,368,0.415272,12.503232),
    ("97s",853,372,0.412903,12.251417),
    ("T8o",733,492,0.385474,12.156984),
    ("J3s",792,433,0.398770,12.040344),
    ("T6s",767,458,0.391983,11.921088),
    ("Q2o",975,250,0.428097,11.302950),
    ("J2s",891,334,0.412488,11.138727),
    ("87s",945,280,0.422015,11.110552),
    ("J6o",755,470,0.378294,10.780675),
    ("98o",841,384,0.394874,10.271257),
    ("T7o",765,460,0.374878,10.204755),
    ("96s",878,347,0.401527,10.097673),
    ("J5o",855,370,0.395413,9.987293),
    ("T5s",886,339,0.401897,9.946900),
    ("T4s",949,276,0.408748,9.260066),
    ("86s",969,256,0.410324,8.994746),
    ("J4o",947,278,0.405076,8.906238),
    ("T6o",877,348,0.385581,8.571955),
    ("97o",873,352,0.384566,8.570963),
    ("T3s",1026,199,0.415998,8.415718),
    ("76s",1045,180,0.418616,8.318417),
    ("95s",970,255,0.403431,8.261043),
    ("J3o",1047,178,0.415307,7.914721),
    ("T2s",1123,102,0.425488,7.538836),
    ("87o",976,249,0.396225,7.505732),
    ("85s",1039,186,0.406723,7.239171),
    ("96o",987,238,0.393276,7.074151),
    ("T5o",1003,222,0.394962,6.920957),
    ("J2o",1129,96,0.420420,6.885765),
    ("75s",1115,110,0.414674,6.594160),
    ("94s",1063,162,0.403925,6.583641),
    ("T4o",1097,128,0.406874,6.248512),
    ("65s",1159,66,0.418775,6.207388),
    ("86o",1087,138,0.402754,6.099835),
    ("93s",1121,104,0.409454,6.058991),
    ("84s",1145,80,0.409633,5.692773),
    ("95o",1133,92,0.406508,5.650827),
    ("T3o",1145,80,0.406672,5.480421),
    ("76o",1164,61,0.410142,5.439126),
    ("92s",1153,72,0.406646,5.359298),
    ("74s",1198,27,0.412623,5.109201),
    ("54s",1225,0,0.414534,4.850294),
    ("T2o",1149,76,0.397258,4.832254),
    ("85o",1197,28,0.407938,4.812230),
    ("64s",1225,0,0.413333,4.769221),
    ("83s",1201,24,0.403003,4.463809),
    ("94o",1201,24,0.400861,4.345783),
    ("75o",1225,0,0.405120,4.269797),
    ("82s",1207,18,0.398164,4.129509),
    ("73s",1225,0,0.400359,4.018033),
    ("93o",1200,25,0.393756,4.000304),
    ("65o",1225,0,0.399443,3.972305),
    ("53s",1225,0,0.396930,3.851054),
    ("63s",1225,0,0.395336,3.777173),
    ("84o",1225,0,0.394468,3.737896),
    ("92o",1215,10,0.388261,3.585219),
    ("43s",1225,0,0.386419,3.402163),
    ("74o",1225,0,0.385498,3.366747),
    ("72s",1225,0,0.381559,3.221509),
    ("54o",1225,0,0.381553,3.221293),
    ("64o",1225,0,0.380105,3.170312),
    ("52s",1225,0,0.378493,3.114999),
    ("62s",1225,0,0.376690,3.054809),
    ("83o",1225,0,0.374838,2.994827),
    ("42s",1225,0,0.368290,2.796223),
    ("82o",1225,0,0.368277,2.795837),
    ("73o",1225,0,0.366023,2.731972),
    ("53o",1225,0,0.362648,2.640274),
    ("63o",1225,0,0.360776,2.591343),
    ("32s",1225,0,0.359844,2.567461),
    ("43o",1225,0,0.351459,2.366073),
    ("72o",1225,0,0.345836,2.243309),
    ("52o",1225,0,0.342846,2.181602),
    ("62o",1225,0,0.340751,2.139745),
    ("42o",1225,0,0.331998,1.976146),
    ("32o",1225,0,0.323032,1.825374) )
    */

    //extract cards and place in List format
    //do a search and extract n_call, n_fold, p_call






  }





  //module: Determine the strength of the PreFlop hand, this is returned non-normalised if the results do not fit into the conventional 0-100%
  def preFlopHandStrength(alg:Int, pfCards: Array[String]): Float = {
    //we pass an array with our cards, and determine the relative strength of the hand in the deck as a % of 100 (Royal Flush)
    //loop and break into values and suits

    //PF hand ranking follows classical straightforward techniques
    //For the initial two cards, there are {52 choose 2} = 1326 possible combinations, but only 169 distinct hand types.
    //For each one of the 169 possible hand types, a simulation of 1,000,000 poker games was done against nine random
    //hands. This produced a statistical measure of the approximate income rate (profit expectation) for each
    //starting hand. A pair of aces had the highest income rate; a 2 and 7 of different suits had the lowest. There is a strong
    //correlation between our simulation results and the pre-flop categorization given in Sklansky and Malmuth (1994)

    //Have Options #1 is PreFlop Sklansky/Malmuth Values
        //Our Primary routine is based upon Sklansky and Malmuth - HoldEm Poker for Advanced Players

        //These rankings reflect not only which group each starting hand belongs to, but its approximate order in that group as well.
        //Any hand not listed in the tables is ranked below Group 8.

        //Group 1: AA, KK, QQ, JJ, AKs
        //Group 2: TT, AQs, AJs, KQs, AK
        //Group 3: 99, JTs, QJs, KJs, ATs, AQ
        //Group 4: T9s, KQ, 88, QTs, 98s, J9s, AJ, KTs
        //Group 5: 77, 87s, Q9s, T8s, KJ, QJ, JT, 76s, 97s, Axs, 65s
        //Group 6: 66, AT, 55,86s, KT, QT, 54s, K9s, J8s, 75s
        //Group 7: 44, J9, 64s, T9, 53s, 33, 98,43s, 22, Kxs, T7s, Q8s 1
        //Group 8: 87, A9, Q9, 76,42s, 32s, 96s, 85s, 58, J7s, 65, 54, 74s,K9, T8

    // http://www.propokertools.com/oracle_help/range_of_hands#percent_of_hands
    val proPoker_10handed: Array[String] = Array[String] (
      "AA","KK","QQ","JJ","(AK)","TT","AK","(AQ)","99","(AJ)","AQ","88","(AT)","(KQ)","AJ","77","(KJ)","(QJ)","(KT)","KQ",
      "(A9)","AT","66","(A8)","(QT)","(JT)","KJ","(A7)","(A5)","(K9)","(A4)","(A6)","55","(Q9)","(A3)","(J9)","KT","QJ","A9","(T9)",
      "(K8)","(A2)","(K7)","44","A8","QT","(Q8)","JT","(J8)","(K6)","(98)","(T8)","(K5)","A7","(K4)","K9","A5","33","(K3)","A4",
      "Q9","(87)","(Q7)","(T7)","(Q6)","(K2)","(J7)","A6","(97)","(Q5)","A3","J9","T9","22","K8","A2","(Q4)","(76)","K7","(86)",
      "(96)","(J6)","(J5)","K6","(Q3)","(Q2)","(T6)","(65)","K5","(75)","Q8","(54)","J8","(J4)","T8","98","(85)","(95)","K4","(J3)",
      "(64)","(T4)","(T5)","87","Q7","K3","(J2)","(74)","97","J7","(53)","Q6","(T3)","K2","(94)","T7","(84)","(43)","(63)","Q5",
      "86","(T2)","(93)","76","Q4","(92)","96","(73)","J6","Q3","(52)","65","J5","T6","(82)","(42)","(83)","Q2","75","54",
      "J4","(62)","85","(32)","95","(72)","J3","T5","T4","64","J2","53","74","84","T3","43","94","T2","93","63",
      "92","73","52","42","83","82","62","32","72")
     //others are available at the above link to be added
     //val proPoker_6handed: Array[String]
     //....


    //A hand ordering for hold'em based on pre-flop equity vs. a random hand (2 million trials).
    //Hands are listed in descending order of profitability.
    //Cards of the same suit are listed in parentheses.

    val proPoker_vsRandom: Array[String] = Array[String] (
    "AA","KK", "QQ","JJ","TT","99","88","(AK)","77","(AQ)","(AJ)","AK","(AT)","AQ","AJ","(KQ)","66","(A9)","AT","(KJ)",
    "(A8)","(KT)","KQ","(A7)","A9","KJ","55","(QJ)","(K9)","(A5)","(A6)","A8","KT","(QT)","(A4)","A7","(K8)","(A3)","QJ","K9",
    "A5","A6","(Q9)","(K7)","(JT)","(A2)","QT","44","A4","(K6)","K8","(Q8)","A3","(K5)","(J9)","Q9","JT","K7","A2","(K4)",
    "(Q7)","K6","(K3)","(T9)","(J8)","33","(Q6)","Q8","K5","J9","(K2)","(Q5)","(T8)","K4","(J7)","(Q4)","Q7","T9","J8","K3",
    "Q6","(Q3)","(98)","(T7)","(J6)","K2","22","(Q2)","Q5","(J5)","T8","J7","Q4","(97)","(J4)","(T6)","(J3)","Q3","98","(87)",
    "T7","J6","(96)","(J2)","Q2","(T5)","J5","(T4)","97","(86)","J4","T6","(95)","(T3)","(76)","J3","87","(T2)","(85)","96",
    "J2","T5","(94)","(75)","T4","(93)","86","(65)","(84)","95","T3","(92)","76","(74)","T2","(54)","85","(64)","(83)","94",
    "75","(82)","(73)","93","65","(53)","(63)","84","92","(43)","74","(72)","54","64","(52)","(62)","83","(42)","82","73",
    "53","63","(32)","43","72","52","62","42","32")



    val proPoker_6handed: Array[String] = Array[String] (
    "AA","KK","QQ","JJ","TT","(AK)","AK","(AQ)","99","(AJ)","AQ","88","(AT)","AJ","(KQ)","77","(KJ)","AT","KQ","(A9)",
    "(KT)","66","(A8)","(QJ)","(A7)","KJ","(QT)","(A5)","A9","(A6)","(JT)","55","(K9)","(A4)","KT","(A3)","A8","QJ","(Q9)","(A2)",
    "(K8)","(J9)","44","(K7)","(T9)","QT","A7","(K6)","A5","JT","(Q8)","K9","A6","(K5)","(J8)","(T8)","A4","33","(98)","(K4)",
    "A3","(Q7)","(Q6)","Q9","(K3)","(K2)","A2","(J7)","K8","J9","(T7)","(87)","T9","(Q5)","(97)","K7","22","(Q4)","K6","(J6)",
    "Q8","(86)","(76)","(T6)","(Q3)","K5","(96)","J8","(J5)","T8","(Q2)","98","K4","(65)","(J4)","Q7","(75)","Q6","(J3)","K3",
    "J7","(95)","(85)","(T5)","T7","K2","87","(54)","(J2)","97","Q5","(T4)","(64)","(T3)","(74)","Q4","76","J6","(84)","(T2)",
    "(94)","T6","(53)","Q3","86","J5","(93)","96","Q2","(63)","(92)","65","(43)","J4","(73)","75","(83)","(52)","J3","85",
    "T5","(82)","95","54","J2","(42)","T4","(62)","64","(72)","(32)","T3","74","84","T2","53","94","93","43","63",
    "92","73","83","52","82","42","62","72","32")

    val proPoker_3handed: Array[String] = Array[String] (
    "AA","KK","QQ","JJ","TT","99","(AK)","(AQ)","88","AK","(AJ)","AQ","(AT)","77","AJ","(KQ)","AT","(A9)","(KJ)","66",
    "(A8)","(KT)","KQ","(A7)","A9","(QJ)","KJ","(A6)","(A5)","A8","55","(QT)","(K9)","KT","(A4)","A7","(JT)","QJ","(A3)","(A2)",
    "(K8)","(Q9)","A5","QT","A6","44","K9","(K7)","(J9)","A4","(K6)","JT","A3","(Q8)","(T9)","(K5)","K8","Q9","A2","(K4)","(J8)",
    "33","K7","(Q7)","(T8)","(K3)","J9","(Q6)","K6","Q8","(K2)","T9","(98)","(Q5)","(J7)",
    "K5","(Q4)","(T7)","J8","K4","(97)","22","(J6)","Q7","(J5)","(Q3)","(87)","T8","K3",
    "(T6)","Q6","(Q2)","98","(J4)","K2","J7","Q5","(96)","(J3)","(86)","(76)","T7","Q4","(T5)","(J2)","97","J6","(T4)","(95)","Q3","87",
    "(85)","J5","(T3)","(65)","(75)","T6","Q2","(T2)","J4","96","(54)","(94)","86","J3",
    "(84)","76","(74)","(64)","(93)","T5","(92)","J2","T4","95","(53)","85","75","T3","(73)",
    "65","(63)","(83)","(82)","(43)","T2","54","94","(52)","84","64","(72)","(62)","74","93","(42)","92","(32)","53","83",
    "63","73","82","43","52","62","72","42","32")


    //we compare our hand to the 169 types and determine our strength returned as a floating percentile, this can  be fed
    //directly into the first bar of the Relative Hand Strength Bar Graph Indicator

    //this is achieved by selecting a ranking mash (usually via droid interface switch) of 169 rankings, matching our cards
    //and then normalising to 100%

    //we split our pf hand, convert to the data structure string equivalent and simply do a quick match
    val c1 = pfCards(0)
    val c2 = pfCards(1)

    val suit_1 = c1.drop(1)
    val denom_1 = c1.dropRight(1)

    val suit_2 = c2.drop(1)
    val denom_2 = c2.dropRight(1)


    var idx,tc:Int = 0

    //ALG1 Weighted vs Random
    //check suited
    if (suit_1 == suit_2 && alg == Alg_Random) {
    //build our compare value
    val preFlop_pair = "("+denom_1.trim+denom_2.trim+")" //and trim (trailing) whitespace
    val preFlop_pair_r = "("+denom_2.trim+denom_1.trim+")" //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf suits match as "+preFlop_pair)

        //get its index
        for ( cd <-proPoker_vsRandom.reverse ) {
            idx +=1
            if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"suited "+cd+" found at position "+idx) }
        }

    } //suit
    else if (alg == Alg_Random)
    //off-suit
    {
      val preFlop_pair = denom_1.trim+denom_2.trim //and trim (trailing) whitespace
      val preFlop_pair_r = denom_2.trim+denom_1.trim //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf off-suit match as "+preFlop_pair)

        //get its index
        for ( cd <-proPoker_vsRandom.reverse ) {
          idx +=1
          if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"off-suited "+cd+" found at position "+idx) }
        }

  } //off-suit

    //ALG2 10handed
    //check suited
    if (suit_1 == suit_2 && alg == Alg_TenMax) {
      //build our compare value
      val preFlop_pair = "("+denom_1.trim+denom_2.trim+")" //and trim (trailing) whitespace
      val preFlop_pair_r = "("+denom_2.trim+denom_1.trim+")" //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf suits match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_10handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"suited "+cd+" found at position "+idx) }
      }

    } //suit
    else if (alg == Alg_TenMax)
    //off-suit
    {
      val preFlop_pair = denom_1.trim+denom_2.trim //and trim (trailing) whitespace
    val preFlop_pair_r = denom_2.trim+denom_1.trim //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf off-suit match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_10handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"off-suited "+cd+" found at position "+idx) }
      }

    } //off-suit


    //ALG3 6handed
    //check suited
    if (suit_1 == suit_2 && alg == Alg_SixMax) {
      //build our compare value
      val preFlop_pair = "("+denom_1.trim+denom_2.trim+")" //and trim (trailing) whitespace
      val preFlop_pair_r = "("+denom_2.trim+denom_1.trim+")" //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf suits match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_6handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"suited "+cd+" found at position "+idx) }
      }

    } //suit
    else if (alg == Alg_SixMax)
    //off-suit
    {
      val preFlop_pair = denom_1.trim+denom_2.trim //and trim (trailing) whitespace
    val preFlop_pair_r = denom_2.trim+denom_1.trim //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf off-suit match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_6handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"off-suited "+cd+" found at position "+idx) }
      }

    } //off-suit

    //ALG4 3handed
    //check suited
    if (suit_1 == suit_2 && alg == Alg_ThreeMax) {
      //build our compare value
      val preFlop_pair = "("+denom_1.trim+denom_2.trim+")" //and trim (trailing) whitespace
      val preFlop_pair_r = "("+denom_2.trim+denom_1.trim+")" //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf suits match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_6handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"suited "+cd+" found at position "+idx) }
      }

    } //suit
    else if (alg == Alg_ThreeMax)
    //off-suit
    {
      val preFlop_pair = denom_1.trim+denom_2.trim //and trim (trailing) whitespace
    val preFlop_pair_r = denom_2.trim+denom_1.trim //and trim (trailing) whitespace

      ObjectUtils.debugLog(TAG,"pf off-suit match as "+preFlop_pair)

      //get its index
      for ( cd <-proPoker_3handed.reverse ) {
        idx +=1
        if (preFlop_pair == cd || preFlop_pair_r == cd) { tc = idx; ObjectUtils.debugLog(TAG,"off-suited "+cd+" found at position "+idx) }
      }

    } //off-suit



  //return normalised 1..169
    ObjectUtils.debugLog(TAG,"PF TC: "+tc)
    ObjectUtils.debugLog(TAG,"PF RANK: "+(tc*100/169))
    (tc*100/169)
  }



  def chenPfRank (pfCards: Array[String]): Int = {

    //we split our pf hand, convert to the data structure string equivalent and simply do a quick match
    val c1 = pfCards(0)
    val c2 = pfCards(1)

    val suit_1 = c1.drop(1).trim
    val denom_1 = c1.dropRight(1).trim

    val suit_2 = c2.drop(1).trim
    val denom_2 = c2.dropRight(1).trim

    //chens points mappings
    val chenMultiplier = List({"2"-> 1.toFloat},{"3"->(1.5).toFloat}, {"4"->2.toFloat},{"5"->(2.5).toFloat},{"6"->3.toFloat},{"7"->(3.5).toFloat},{"8"->4.toFloat},{"9"->(4.5).toFloat},{"T"->5.toFloat},{"J"->6.toFloat},{"Q"->7.toFloat},{"K"->8.toFloat},{"A"->10.toFloat})

    val cM1 = chenMultiplier.find{e => e._1 == denom_1}
    val cM2 = chenMultiplier.find{e => e._1 == denom_2}

    var cMa:Float=0
    var cMb:Float=0

    //10-aug-14
    //exception on initial wheel up scroll but still redback and then a second wheel scroll
    //TODO wheel freeze ahead!
    try{
      cMa = cM1.last._2
      cMb = cM2.last._2
    }     catch {
      case e: Exception => {
        //e.printStackTrace
        cMa=10 //arbitrary mid-way is alpha'd anyways
        cMb=10
      }}


    var cM:Float=0

        //alg follows strict ordering so is not factored
        if (cMa > cMb) cM = cMa else cM = cMb //Score your highest card only.
        ObjectUtils.debugLog(TAG,"chen: Score your highest card only giving "+cM)

        if (denom_1 == denom_2) {//Multiply pairs by 2 of one card's value. However, minimum score for a pair is 5.
          cM = cM*2
          if (cM<5) cM=5
          ObjectUtils.debugLog(TAG,"chen: Multiply pairs by 2 of one card's value. However, minimum score for a pair is 5 giving "+cM)
        }

        if (suit_1 == suit_2) { cM = cM+2
            ObjectUtils.debugLog(TAG,"chen: Add 2 points if cards are suited giving "+cM)
        } //Add 2 points if cards are suited.

        //Subtract points if their is a gap between the two cards. Aces are high.
        //No gap = -0 points.
        //1 card gap = -1 points.
        //2 card gap = -2 points.
        //3 card gap = -4 points.
        //4 card gap or more = -5 points.

        var cIndex:Int = 0
        val cIndex1 = chenMultiplier.indexWhere({e => e._1 == denom_1})
        val cIndex2 = chenMultiplier.indexWhere({e => e._1 == denom_2})

        //ObjectUtils.debugLog(TAG,"Chen Index "+denom_1+" is "+cIndex1)
        //ObjectUtils.debugLog(TAG,"Chen Index "+denom_2+" is "+cIndex2)

        if (cMa > cMb) {
            cIndex = cIndex1 - cIndex2

            } else {
            cIndex = cIndex2 - cIndex1

        }

        val gap:Int = cIndex-1

        if (gap == 1) { cM = cM-1;   ObjectUtils.debugLog(TAG,"chen: Subtract points 1 if their is a 1 gap between the two cards giving "+cM) }
        if (gap == 2) { cM = cM-2;   ObjectUtils.debugLog(TAG,"chen: Subtract points 2 if their is a 2 gap between the two cards giving "+cM) }
        if (gap == 3) { cM = cM-4;   ObjectUtils.debugLog(TAG,"chen: Subtract points 4 if their is a 3 gap between the two cards giving "+cM) }
        if (gap == 4) { cM = cM-5;   ObjectUtils.debugLog(TAG,"chen: Subtract points 4 if their is a 4 gap between the two cards giving "+cM) }
        if (gap >  4) { cM = cM-5;   ObjectUtils.debugLog(TAG,"chen: Subtract points 5 if their is a >4 gap between the two cards giving "+cM) }


        //Add 1 point if there is a 0 or 1 card gap and both cards are lower than a Q but not pairs
        if (denom_1 != denom_2) {
          if (cIndex <=2 & ((cIndex1< 10) & (cIndex2 < 10) ))  {
          cM=cM+1
          ObjectUtils.debugLog(TAG,"chen: Add 1 point if there is a 0 or 1 card gap and both cards are lower than a Q giving "+cM)
          }
        }

        //ObjectUtils.debugLog(TAG,"Chen Index difference is "+cIndex)


        //Always round up
        cM = Math.ceil(cM).asInstanceOf[Float]


        ObjectUtils.debugLog(TAG,"chen: Chen Index is "+cM)

   cM.toInt
  }

  //module: numberOfOuts returns the number (and details) of available outs for a given hand,this is displayed in the large status box beneath the current best hand
  /*    no      card
        cards   odds-1
  outs #1 #2  #1  #2      type
  ------------------------------------------------------------------------
  1	  2%	4%	46	23	    Backdoor Straight or Flush (Requires two cards)
  2	  4%	8%	22	12	    Pocket Pair to Set
  3	  7%	13%	14	7	      One Overcard
  4	  9%	17%	10	5	      Inside Straight / Two Pair to Full House
  5	  11%	20%	8	4	        One Pair to Two Pair or Set
  6	  13%	24%	6.7	3.2	    No Pair to Pair / Two Overcards
  7	  15%	28%	5.6	2.6	    Set to Full House or Quads
  8	  17%	32%	4.7	2.2	    Open Straight
  9	  19%	35%	4.1	1.9	    Flush
  10	22%	38%	3.6	1.6	    Inside Straight & Two Overcards
  11	24%	42%	3.2	1.4	    Open Straight & One Overcard
  12	26%	45%	2.8	1.2	    Flush & Inside Straight / Flush & One Overcard
  13	28%	48%	2.5	1.1
  14	30%	51%	2.3	0.95
  15	33%	54%	2.1	0.85	  Flush & Open Straight / Flush & Two Overcards
  16	34%	57%	1.9	0.75
  17	37%	60%	1.7	0.66
  */



  //TODO:Current 1-Feb-14
  def pfSomething(pCards:Array[String]): String = {


    //we split our pf hand, convert to the data structure string equivalent and simply do a quick match
    val c1 = pCards(0)
    val c2 = pCards(1)


    val suit_1 = c1.drop(1).trim
    val denom_1 = c1.dropRight(1).trim

    val suit_2 = c2.drop(1).trim
    val denom_2 = c2.dropRight(1).trim

    var suited:Boolean=false
    if (suit_1 == suit_2) suited=true

    val gappedCards = List({"2"-> 2},{"3"->3}, {"4"->4},{"5"->5},{"6"->6},{"7"->7},{"8"->8},{"9"->9},{"T"->10},{"J"->11},{"Q"->12},{"K"->13},{"A"->14})

    val g1 = gappedCards.find{e => e._1 == denom_1}
    val g2 = gappedCards.find{e => e._1 == denom_2}

    ObjectUtils.debugLog(TAG,"s1:"+suit_1+" s2:"+suit_2+" d1:"+denom_1+" d2:"+denom_2+" suited:"+suited)

    "devel"
  }




  //TODO:Current 26-Jan-14
  def numberOfOuts(cr:Int) {
  //we pass in htable rating

    //we pull the current cards as we need to construct the out cards (such as the card needed to make three of a kind, what value are the other two?
    val theCards = ObjectUtils.currentPlayerCardsArrayDesc


    cr match {                   //this is what we currently have at flop

      case cr if (cr > 6185) => {
                                //pair at T = //(52-2pf-3f=47 / 4-1=3) 47/3 = approx 15-1
                                //pair at R = 47/3 + 46/3

                                }// 1277 high card

      case cr if (cr > 3325) => {
                                //trip

                                }// 2860 one pair

      case cr if (cr > 2467) => {

                                }//  858 two pair

      case cr if (cr > 1609) => {

                                }//  858 three-kind

      case cr if (cr > 1599) => {

                                }//   10 straights

      case cr if (cr > 322)  => {

                                }// 1277 flushes

      case cr if (cr > 166)  => {

                                }//  156 full house

      case cr if (cr > 10)   => {

                                }//  156 four-kind

      case cr if (cr>1)      => {

                                }//   10 straight-flushes

      case cr if (cr== 1)      => {

                                  } //4? rf
      case cr if (cr ==0)      => {

                                  } //ERR rf?  //TODO not sure if this error ever raised

    } //numberOfOuts



  }

  def getCardValue(crd:Int): Tuple2[String,String] = {  //int->cac

      crd match {
        //HEARTS
        case 98306   => ("2","H")
        case 164099  => ("3","H")
        case 295429   => ("4","H")
        case 557831    => ("5","H")
        case 1082379   => ("6","H")
        case 2131213    => ("7","H")
        case 4228625    => ("8","H")
        case 8423187    => ("9","H")
        case 16812055   => ("T","H")
        case 33589533   => ("J","H")
        case 67144223   => ("Q","H")
        case 134253349  => ("K","H")
        case 268471337  => ("A","H")

        //DIAMONDS
        case 81922 => ("2","D")
        case 147715 => ("3","D")
        case 279045 => ("4","D")
        case 541447 => ("5","D")
        case 1065995 => ("6","D")
        case 2114829 => ("7","D")
        case 4212241 => ("8","D")
        case 8406803 => ("9","D")
        case 16795671 => ("T","D")
        case 33573149 => ("J","D")
        case 67127839 => ("Q","D")
        case 134236965 => ("K","D")
        case 268454953 => ("A","D")

        //SPADES
        case 73730  => ("2","S")
        case 139523 => ("3","S")
        case 270853 => ("4","S")
        case 533255 => ("5","S")
        case 1057803 => ("6","S")
        case 2106637 => ("7","S")
        case 4204049 => ("8","S")
        case 8398611 => ("9","S")
        case 16787479 => ("T","S")
        case 33564957 => ("J","S")
        case 67119647 => ("Q","S")
        case 134228773 => ("Q","S")
        case 268446761 => ("A","S")

        //CLUBS
        case 69634 => ("2","C")
        case 135427 => ("3","C")
        case 266757 => ("4","C")
        case 529159 => ("5","C")
        case 1053707 => ("6","C")
        case 2102541 => ("7","C")
        case 4199953 => ("8","C")
        case 8394515 => ("9","C")
        case 16783383 => ("T","C")
        case 33560861 => ("J","C")
        case 67115551 => ("Q","C")
        case 134224677 => ("K","C")
        case 268442665 => ("A","C")

        case _ => { (""+crd,""+crd) }    //not reachable I assume

      } //match

} //cac


  //8-aug-14

  def getCardValueIndex(crd:Int): Int = {  //int->cac

    crd match {
      //HEARTS
      case 98306   => 0
      case 164099  => 1
      case 295429   => 2
      case 557831    => 3
      case 1082379   => 4
      case 2131213    => 5
      case 4228625    => 6
      case 8423187    => 7
      case 16812055   => 8
      case 33589533   => 9
      case 67144223   => 10
      case 134253349  => 11
      case 268471337  => 12

      //DIAMONDS
      case 81922 => 13
      case 147715 => 14
      case 279045 => 15
      case 541447 => 16
      case 1065995 => 17
      case 2114829 => 18
      case 4212241 => 19
      case 8406803 => 20
      case 16795671 => 21
      case 33573149 => 22
      case 67127839 => 23
      case 134236965 => 24
      case 268454953 => 25

      //SPADES
      case 73730  => 26
      case 139523 => 27
      case 270853 => 28
      case 533255 => 29
      case 1057803 => 30
      case 2106637 => 31
      case 4204049 => 32
      case 8398611 => 33
      case 16787479 => 34
      case 33564957 => 35
      case 67119647 => 36
      case 134228773 => 37
      case 268446761 => 38

      //CLUBS
      case 69634 => 39
      case 135427 => 40
      case 266757 => 41
      case 529159 => 42
      case 1053707 => 43
      case 2102541 => 44
      case 4199953 => 45
      case 8394515 => 46
      case 16783383 => 47
      case 33560861 => 48
      case 67115551 => 49
      case 134224677 => 50
      case 268442665 => 51

      case _ => { 0 }    //not reachable I assume

    } //match

  } //cac

//4-Aug-14


    def isValidHero(cards:Int, cpcda:Array[String], cry:Crypto_htable): Boolean = {
    //input: some hand
    //output: is the hand a valid out for hero

      //return hand
      val hand = cry.htableTable_3(cards-1).replaceAll(" ","").trim()
      //get type of hand
      //val toh = ObjectUtils.returnHand(c)
      //Log.d(TAG,"0. isValidHero full potential out (at turn) ="+hand)

      //preflop
      val card1 = cpcda(0).dropRight(1).trim
      val card2 = cpcda(1).dropRight(1).trim
      //Log.d(TAG,"1. isValidHero actual preflop="+card1+card2)

      //remove pre-flop from our hand
      var bestturnhand = hand.replaceFirst(card1,"")
      bestturnhand = bestturnhand.replaceFirst(card2,"")
      //Log.d(TAG,"2. isValidHero turn remaining hand after preflop delete="+bestturnhand)

      //high card we don't need this so discard
      //Log.d(TAG,"2. isValidHero discarding high card="+bestturnhand)
      if (cards > 6186) return false


      //if any two c3-c6 match then a pair
      if (cards> 3325 && cards < 6186) //it's a pair
      {
        //Log.d(TAG,"2.  Its a Pair...")
        //quick check may be hero and we can eliminate now
        if (card1 == card2) { //Log.d(TAG, "3.   =preflops match");
          return true}
        //if our flop contains any preflop card then a pair
        if ((bestturnhand contains card1) || (bestturnhand contains card2)) { //Log.d(TAG,"3.   =one preflop matches an out");
          return true }

        //Log.d(TAG,"2.  false pair!")
        return false // we can't match this pair satisfactorily
      }

      if (cards > 1609 && cards < 2468 )
      {
       // Log.d(TAG,"3.  Its Three of a Kind...")
        //make sure all three don't exist within bestturnhand(i.e. -preflop) but out of preflop
        if (!(bestturnhand contains card1) && (!(bestturnhand contains card2))) { //Log.d(TAG,"3.   =crazy three of a kind");
          return false }

        //Log.d(TAG,"3. isValidHero returning valid 3ofakind="+bestturnhand)
        return true//default

      }

      if (cards> 2468 && cards < 3326 ){
        // the y's may not count if post flop
        //Log.d(TAG,"4.  x AND y's...")
        //if below must be two pair in preflop
        if ((bestturnhand contains card1) || ((bestturnhand contains card2))) { //Log.d(TAG,"3.   =ok 2 pairs, at least one decent");
          return true }
        //Log.d(TAG,"5.  Thrown away")
        return false//one pair must be dodgy
      }


      //if any three c3-c6 match then trips here
      //if all four match then 4oaK
      //if returned hand


    //Log.d(TAG,"4. returning true - valid hand by default!"+bestturnhand)
    //default
    true
    }


//5-Aug-14






} //pa
