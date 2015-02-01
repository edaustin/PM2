package com{
package droidroid {
package PM2 {

import _root_.android.content.{Intent, Context}
import _root_.android.R.array
import _root_.android.os.Build
import _root_.android.util.Log
import _root_.android.widget.{FrameLayout, Toast}
import scala.Array
import java.util


//TODO: Card_s and Card_v need to be transposed in name

object ObjectUtils {

  final val TAG: String = "ObjectUtils"

  final val REMOVE_FROM_WHEEL =1

  var bP,vP=0

  val referenceDeck: Array[String] = Array[String]("2 C", "2 D", "2 H", "2 S", "3 C", "3 D", "3 H", "3 S", "4 C", "4 D", "4 H", "4 S", "5 C", "5 D", "5 H", "5 S", "6 C", "6 D", "6 H", "6 S", "7 C", "7 D", "7 H", "7 S", "8 C", "8 D", "8 H", "8 S", "9 C", "9 D", "9 H", "9 S", "T C", "T D", "T H", "T S", "J C", "J D", "J H", "J S", "Q C", "Q D", "Q H", "Q S", "K C", "K D", "K H", "K S", "A C", "A D", "A H", "A S")
  val refDeckImages: Array[Int] = Array[Int](R.drawable.c2_of_clubs, R.drawable.c2_of_diamonds, R.drawable.c2_of_hearts, R.drawable.c2_of_spades, R.drawable.c3_of_clubs, R.drawable.c3_of_diamonds, R.drawable.c3_of_hearts, R.drawable.c3_of_spades, R.drawable.c4_of_clubs, R.drawable.c4_of_diamonds, R.drawable.c4_of_hearts, R.drawable.c4_of_spades, R.drawable.c5_of_clubs, R.drawable.c5_of_diamonds, R.drawable.c5_of_hearts, R.drawable.c5_of_spades, R.drawable.c6_of_clubs, R.drawable.c6_of_diamonds, R.drawable.c6_of_hearts, R.drawable.c6_of_spades, R.drawable.c7_of_clubs, R.drawable.c7_of_diamonds, R.drawable.c7_of_hearts, R.drawable.c7_of_spades, R.drawable.c8_of_clubs, R.drawable.c8_of_diamonds, R.drawable.c8_of_hearts, R.drawable.c8_of_spades, R.drawable.c9_of_clubs, R.drawable.c9_of_diamonds, R.drawable.c9_of_hearts, R.drawable.c9_of_spades, R.drawable.c10_of_clubs, R.drawable.c10_of_diamonds, R.drawable.c10_of_hearts, R.drawable.c10_of_spades, R.drawable.jack_of_clubs, R.drawable.jack_of_diamonds, R.drawable.jack_of_hearts, R.drawable.jack_of_spades, R.drawable.queen_of_clubs, R.drawable.queen_of_diamonds, R.drawable.queen_of_hearts, R.drawable.queen_of_spades, R.drawable.king_of_clubs, R.drawable.king_of_diamonds, R.drawable.king_of_hearts, R.drawable.king_of_spades, R.drawable.ace_of_clubs, R.drawable.ace_of_diamonds, R.drawable.ace_of_hearts, R.drawable.ace_of_spades,R.drawable.red_back)


  //we use Arrays as these are passed to th Java CardAdapter class for usage
  var currentDeck: Array[String] = Array[String]("2 C", "2 D", "2 H", "2 S", "3 C", "3 D", "3 H", "3 S", "4 C", "4 D", "4 H", "4 S", "5 C", "5 D", "5 H", "5 S", "6 C", "6 D", "6 H", "6 S", "7 C", "7 D", "7 H", "7 S", "8 C", "8 D", "8 H", "8 S", "9 C", "9 D", "9 H", "9 S", "T C", "T D", "T H", "T S", "J C", "J D", "J H", "J S", "Q C", "Q D", "Q H", "Q S", "K C", "K D", "K H", "K S", "A C", "A D", "A H", "A S","-")
  var currDeckImages: Array[Int] = Array[Int](R.drawable.c2_of_clubs, R.drawable.c2_of_diamonds, R.drawable.c2_of_hearts, R.drawable.c2_of_spades, R.drawable.c3_of_clubs, R.drawable.c3_of_diamonds, R.drawable.c3_of_hearts, R.drawable.c3_of_spades, R.drawable.c4_of_clubs, R.drawable.c4_of_diamonds, R.drawable.c4_of_hearts, R.drawable.c4_of_spades, R.drawable.c5_of_clubs, R.drawable.c5_of_diamonds, R.drawable.c5_of_hearts, R.drawable.c5_of_spades, R.drawable.c6_of_clubs, R.drawable.c6_of_diamonds, R.drawable.c6_of_hearts, R.drawable.c6_of_spades, R.drawable.c7_of_clubs, R.drawable.c7_of_diamonds, R.drawable.c7_of_hearts, R.drawable.c7_of_spades, R.drawable.c8_of_clubs, R.drawable.c8_of_diamonds, R.drawable.c8_of_hearts, R.drawable.c8_of_spades, R.drawable.c9_of_clubs, R.drawable.c9_of_diamonds, R.drawable.c9_of_hearts, R.drawable.c9_of_spades, R.drawable.c10_of_clubs, R.drawable.c10_of_diamonds, R.drawable.c10_of_hearts, R.drawable.c10_of_spades, R.drawable.jack_of_clubs, R.drawable.jack_of_diamonds, R.drawable.jack_of_hearts, R.drawable.jack_of_spades, R.drawable.queen_of_clubs, R.drawable.queen_of_diamonds, R.drawable.queen_of_hearts, R.drawable.queen_of_spades, R.drawable.king_of_clubs, R.drawable.king_of_diamonds, R.drawable.king_of_hearts, R.drawable.king_of_spades, R.drawable.ace_of_clubs, R.drawable.ace_of_diamonds, R.drawable.ace_of_hearts, R.drawable.ace_of_spades,R.drawable.red_back)



  var currentPlayerCardsArrayDesc: Array[String] = Array.fill[String](7)("")
  var currentPlayerCardsArrayRsc: Array[Int] = Array.fill[Int](7)(0)
                                              //we use an array as it is FAR simpler to maintain state through an index
                                              //this is later constructed into a list for analytics

  var currentBlockIndex: Array[Int] = Array.fill[Int](referenceDeck.length)(0)


  def removeL[T](i: Int, li: List[T]) = {
    val (left, right) = li.span(_ != i)
    left ::: right.drop(1)
  }

  def resetCardData() {
  currentDeck  = Array[String]("2 C", "2 D", "2 H", "2 S", "3 C", "3 D", "3 H", "3 S", "4 C", "4 D", "4 H", "4 S", "5 C", "5 D", "5 H", "5 S", "6 C", "6 D", "6 H", "6 S", "7 C", "7 D", "7 H", "7 S", "8 C", "8 D", "8 H", "8 S", "9 C", "9 D", "9 H", "9 S", "T C", "T D", "T H", "T S", "J C", "J D", "J H", "J S", "Q C", "Q D", "Q H", "Q S", "K C", "K D", "K H", "K S", "A C", "A D", "A H", "A S","-")
  currDeckImages = Array[Int](R.drawable.c2_of_clubs, R.drawable.c2_of_diamonds, R.drawable.c2_of_hearts, R.drawable.c2_of_spades, R.drawable.c3_of_clubs, R.drawable.c3_of_diamonds, R.drawable.c3_of_hearts, R.drawable.c3_of_spades, R.drawable.c4_of_clubs, R.drawable.c4_of_diamonds, R.drawable.c4_of_hearts, R.drawable.c4_of_spades, R.drawable.c5_of_clubs, R.drawable.c5_of_diamonds, R.drawable.c5_of_hearts, R.drawable.c5_of_spades, R.drawable.c6_of_clubs, R.drawable.c6_of_diamonds, R.drawable.c6_of_hearts, R.drawable.c6_of_spades, R.drawable.c7_of_clubs, R.drawable.c7_of_diamonds, R.drawable.c7_of_hearts, R.drawable.c7_of_spades, R.drawable.c8_of_clubs, R.drawable.c8_of_diamonds, R.drawable.c8_of_hearts, R.drawable.c8_of_spades, R.drawable.c9_of_clubs, R.drawable.c9_of_diamonds, R.drawable.c9_of_hearts, R.drawable.c9_of_spades, R.drawable.c10_of_clubs, R.drawable.c10_of_diamonds, R.drawable.c10_of_hearts, R.drawable.c10_of_spades, R.drawable.jack_of_clubs, R.drawable.jack_of_diamonds, R.drawable.jack_of_hearts, R.drawable.jack_of_spades, R.drawable.queen_of_clubs, R.drawable.queen_of_diamonds, R.drawable.queen_of_hearts, R.drawable.queen_of_spades, R.drawable.king_of_clubs, R.drawable.king_of_diamonds, R.drawable.king_of_hearts, R.drawable.king_of_spades, R.drawable.ace_of_clubs, R.drawable.ace_of_diamonds, R.drawable.ace_of_hearts, R.drawable.ace_of_spades,R.drawable.red_back)

  currentPlayerCardsArrayDesc = Array.fill[String](7)("")
  currentPlayerCardsArrayRsc = Array.fill[Int](7)(0)

  }


  //module:Maintains wheel state

  def GlobalWheelBuilder(wheelNo:Int,wheelSelectedItemSingle:Int) {



      val wheelItem = wheelSelectedItemSingle
      val wheelNum = wheelNo -2  //array arg offset needs changing in callers, starts at wheel 2

      debugLog(TAG,"----------------GlobalWheelBuilder------------------")


        //ignore excessive wheel gravity
        if (wheelItem > referenceDeck.size-1) {
        debugLog(TAG,"ignore Gravity and final Red Back "+wheelItem)
        }
        else //run the show
        {
        //we arrive here after the wheel is scrolled and an item selected
        debugLog(TAG,"Wheel="+wheelNo+" Item="+wheelItem+" translates to '"+referenceDeck(wheelItem)+ "' with rsc "+refDeckImages(wheelItem))

        //SAVE CURRENT CARD STATE ARRAY
        //save our card (overwrites previous cards at this position)
        currentPlayerCardsArrayDesc(wheelNum) = referenceDeck(wheelItem)
        currentPlayerCardsArrayRsc(wheelNum) = refDeckImages(wheelItem)


        //UPDATE GLOBAL WHEEL STATE BLOCKING OUT EXISTING CARDS
        //our wheel used currentDeck() and currDeckImages(), adding/removing here affects the global wheel
        currentBlockIndex(wheelNum) = wheelSelectedItemSingle


          //clear old version and write out fresh version with only blocks above
          val freshDeckImages = refDeckImages.clone()

          for (i <- 0 to wheelNum){ //iterate through our block list
            //debugLog(TAG,"Wheel "+i+" blocked "+referenceDeck(currentBlockIndex(i))+" card no "+currentBlockIndex(i))
            debugLog(TAG,"Wheel "+i+" blocked "+referenceDeck(currentBlockIndex(i))+" card no "+currentBlockIndex(i)+" Desc/RSc="+currentPlayerCardsArrayDesc(i)+"/"+currentPlayerCardsArrayRsc(i))
            val rdi = currentBlockIndex(i)
            //todo - fixthis
            debugLog(TAG,"rdi="+rdi)
            freshDeckImages(rdi) = R.drawable.red_back

          }

          //we now need to write out the fresh version
          currDeckImages = freshDeckImages

        } //else


  }
  //-----------


  //module:set our board position
  //sb=1, bb=2, ep=3, mp=4, lp/co=5, bn=6
  def setBoardPosition(positionBoard:Int) {
  debugLog(TAG,"Setting Hero at Position:"+positionBoard)
  bP = positionBoard

  }

  //module:get our board position
  def getBoardPosition(): Int=  {
    debugLog(TAG,"getting Hero at Position:"+bP)
  bP
  }


  //module:set our villain position
  //cc=1,r=2, rr=3, f=4
  def setVillainPosition(positionBoard:Int) {
    debugLog(TAG,"Setting Villain Position:"+positionBoard)
    vP = positionBoard

  }

  //module:get our villain position
  def getVillainPosition(): Int=  {
    debugLog(TAG,"getting Villain Position:"+vP)
    vP
  }


  //module: get the image id's of hero/villain (flop/preflop) for display as best hand
  def cardImageLookup(theResultsCards:String): Array[Int] = {

    //break up the status line into individual cards
    //use the calg string
    //pull the currentPlayersCardsArrayDesc to get suits
    //write out as array in format same as above
    ObjectUtils.debugLog(TAG,"cardx writing the mini ResultsCards="+theResultsCards)


    val y:Array[Int]=Array.fill[Int](7)(0) //returned

    val Cards_v: Array[String] = Array.fill[String](7)("")
    val Cards_s: Array[String] = Array.fill[String](7)("")
    var cl = List[Tuple2[String,String]]()

    //convert theResultsCards into an array of pairs similar to currentPlayerCardsArrayDesc called theResultsCardArray
    //this requires us to pull suit information from currentPlayerCardsArrayDesc, but we let this run first as it gives
    //us the cards, and we then reorder according to theResultsCards and place in theResultsCardArray

    val theCards = currentPlayerCardsArrayDesc

    //convert the array to a list
    val c1 = theCards(0)
    val c2 = theCards(1)
    //f
    val c3 = theCards(2)
    val c4 = theCards(3)
    val c5 = theCards(4)
    //tr
    val c6 = theCards(5)
    val c7 = theCards(6)

    Cards_v(0) = c1.drop(1).trim
    Cards_s(0)= c1.dropRight(1).trim

    Cards_v(1) = c2.drop(1).trim
    Cards_s(1) = c2.dropRight(1).trim

    Cards_v(2) = c3.drop(1).trim
    Cards_s(2) = c3.dropRight(1).trim

    Cards_v(3) = c4.drop(1).trim
    Cards_s(3) = c4.dropRight(1).trim

    Cards_v(4) = c5.drop(1).trim
    Cards_s(4) = c5.dropRight(1).trim

    Cards_v(5) = c6.drop(1).trim
    Cards_s(5) = c6.dropRight(1).trim

    Cards_v(6) = c7.drop(1).trim       //23-jan-14 corrected
    Cards_s(6) = c7.dropRight(1).trim

    cl ::= Tuple2(Cards_s(0),Cards_v(0))
    cl ::= Tuple2(Cards_s(1),Cards_v(1))
    cl ::= Tuple2(Cards_s(2),Cards_v(2))
    cl ::= Tuple2(Cards_s(3),Cards_v(3))
    cl ::= Tuple2(Cards_s(4) ,Cards_v(4))
    cl ::= Tuple2(Cards_s(5) ,Cards_v(5))
    cl ::= Tuple2(Cards_s(6) ,Cards_v(6))

    var z:Int=0

    for (cc <- cl) {
      cc match {
        //HEARTS
        case ("2","H") => y(z)  =  R.drawable.c2_of_hearts
        case ("3","H") => y(z)  =  R.drawable.c3_of_hearts
        case ("4","H") => y(z)  =  R.drawable.c4_of_hearts
        case ("5","H") => y(z)  =  R.drawable.c5_of_hearts
        case ("6","H") => y(z)  =  R.drawable.c6_of_hearts
        case ("7","H") => y(z)  =  R.drawable.c7_of_hearts
        case ("8","H") => y(z)  =  R.drawable.c8_of_hearts
        case ("9","H") => y(z)  =  R.drawable.c9_of_hearts
        case ("T","H") => y(z)  =  R.drawable.c10_of_hearts
        case ("J","H") => y(z)  =  R.drawable.jack_of_hearts
        case ("Q","H") => y(z)  =  R.drawable.queen_of_hearts
        case ("K","H") => y(z)  =  R.drawable.king_of_hearts
        case ("A","H") => y(z)  =  R.drawable.ace_of_hearts

        //DIAMONDS
        case ("2","D") => y(z)  =   R.drawable.c2_of_diamonds
        case ("3","D") => y(z)  =   R.drawable.c3_of_diamonds
        case ("4","D") => y(z)  =   R.drawable.c4_of_diamonds
        case ("5","D") => y(z)  =   R.drawable.c5_of_diamonds
        case ("6","D") => y(z)  =   R.drawable.c6_of_diamonds
        case ("7","D") => y(z)  =   R.drawable.c7_of_diamonds
        case ("8","D") => y(z)  =   R.drawable.c8_of_diamonds
        case ("9","D") => y(z)  =   R.drawable.c9_of_diamonds
        case ("T","D") => y(z)  =   R.drawable.c10_of_diamonds
        case ("J","D") => y(z)  =   R.drawable.jack_of_diamonds
        case ("Q","D") => y(z)  =   R.drawable.queen_of_diamonds
        case ("K","D") => y(z)  =   R.drawable.king_of_diamonds
        case ("A","D") => y(z)  =   R.drawable.ace_of_diamonds

        //SPADES
        case ("2","S") => y(z)  =  R.drawable.c2_of_spades
        case ("3","S") => y(z)  =  R.drawable.c3_of_spades
        case ("4","S") => y(z)  =  R.drawable.c4_of_spades
        case ("5","S") => y(z)  =  R.drawable.c5_of_spades
        case ("6","S") => y(z)  =  R.drawable.c6_of_spades
        case ("7","S") => y(z)  =  R.drawable.c7_of_spades
        case ("8","S") => y(z)  =  R.drawable.c8_of_spades
        case ("9","S") => y(z)  =  R.drawable.c9_of_spades
        case ("T","S") => y(z)  =  R.drawable.c10_of_spades
        case ("J","S") => y(z)  =  R.drawable.jack_of_spades
        case ("Q","S") => y(z)  =  R.drawable.queen_of_spades
        case ("K","S") => y(z)  =  R.drawable.king_of_spades
        case ("A","S") => y(z)  =  R.drawable.ace_of_spades

        //CLUBS
        case ("2","C") => y(z)  =  R.drawable.c2_of_clubs
        case ("3","C") => y(z)  =  R.drawable.c3_of_clubs
        case ("4","C") => y(z)  =  R.drawable.c4_of_clubs
        case ("5","C") => y(z)  =  R.drawable.c5_of_clubs
        case ("6","C") => y(z)  =  R.drawable.c6_of_clubs
        case ("7","C") => y(z)  =  R.drawable.c7_of_clubs
        case ("8","C") => y(z)  =  R.drawable.c8_of_clubs
        case ("9","C") => y(z)  =  R.drawable.c9_of_clubs
        case ("T","C") => y(z)  =  R.drawable.c10_of_clubs
        case ("J","C") => y(z)  =  R.drawable.jack_of_clubs
        case ("Q","C") => y(z)  =  R.drawable.queen_of_clubs
        case ("K","C") => y(z)  =  R.drawable.king_of_clubs
        case ("A","C") => y(z)  =  R.drawable.ace_of_clubs

        case (_,_) => { y(z)=0 }    //not reachable I assume

      } //match
      z+=1
      ObjectUtils.debugLog(TAG," resource matching "+cc+" to ="+y(0)+"/"+y(1)+"/"+y(2)+"/"+y(3)+"/"+y(4)+"/"+y(5)+"/"+y(6))
    } //for



     //theResultsCards V V V V V (V V)
     //y() holds the resourced id's of currentPlayerCardsArrayDesc (mirrored), also mirrors cl
      //cl list of tuple2's with (V,S)

    val theResultsCardResourcesArray:Array[Int] = Array.fill[Int](7)(0)
    var rai=0
    var idx=0

    val trc:Array[String] = theResultsCards.split("\\s+") // reg ex to split on all whitespace our string from lookup with format V V V V V (V V)
    for (ourCardValue <- trc) {  // now we have V (ordered)
        ObjectUtils.debugLog(TAG,"the original cards are:"+ourCardValue)
        //remove from y and add to theResultsCardResourcesArray, doesn't matter how we pull suits, but card must go


                             //find ourCardValue in cl (including it's index) / or from Cards_v/Cards_s
                             if (Cards_s contains ourCardValue) {
                                         ObjectUtils.debugLog(TAG,"Cards_s contains:"+ourCardValue)
                                         //get its index
                                         idx = Cards_s.indexOf(ourCardValue)
                                         //remove from cl / or from Cards_v/Cards_s
                                         Cards_s(idx)=""
                                         //add the resources via index lookup of y() at index to theResultsCardResourcesArray at index
                                         theResultsCardResourcesArray(rai)=currentPlayerCardsArrayRsc(idx)

                             }
                             ObjectUtils.debugLog(TAG,"our a_rai="+rai)
                             rai+=1


      } //trc


                            //River only returns 5 cards, the others are kickers (not returned), we need to add these cards and alpharize them slightly
                            //these cards are left in Cards_s, and we have the resources simply add them back, get len of river and alpharize the remaining resources
                            //rai holds the strength length


                            for (leftS <- Cards_s) {  //iterate through the cards array looking for remaining cards
                              if (!(leftS.equals(""))) {
                                ObjectUtils.debugLog(TAG,"we found kicker:"+leftS+" adding to resource list")


                                //TODO correct kicker suit

                                /* TODO FIX: turn/river issue
                                b_rai i.e. rai increments to 7 so out of bounds
                                02-19 00:42:22.287: ERROR/AndroidRuntime(5639): FATAL EXCEPTION: main
                                Process: com.droidroid.PM2, PID: 5639
                                java.lang.ArrayIndexOutOfBoundsException: length=7; index=7
                                at com.droidroid.PM2.ObjectUtils$$anonfun$cardImageLookup$3.apply(ObjectUtils.scala:313)
                                at com.droidroid.PM2.ObjectUtils$$anonfun$cardImageLookup$3.apply(ObjectUtils.scala:309)
                                 */
                                //added conditional
                                if (rai<7)  theResultsCardResourcesArray(rai) = currentPlayerCardsArrayRsc(Cards_s.indexOf(leftS))  //error because after 6, no card to left!
                                ObjectUtils.debugLog(TAG,"our b_rai="+rai)
                                rai+=1
                              }//leftS
                            } //scan array


    //return resources
    theResultsCardResourcesArray
  } //cil


  def allinNmax(aa:Array[String],pfc:Array[String]): Int = {


    val theCards = currentPlayerCardsArrayDesc
    val c1 = theCards(0)
    val c2 = theCards(1)

    val Cards_v0 = c1.drop(1).trim
    val Cards_s0= c1.dropRight(1).trim

    val Cards_v1 = c2.drop(1).trim
    val Cards_s1 = c2.dropRight(1).trim

    var isS:Boolean=false
    var aac=0
    var aacf=0
    var aacft=0
    var aacftx:Int=0

    if (Cards_v0 == Cards_v1) isS=true


   for (aaCard <- aa) {

     aac+=1

     ObjectUtils.debugLog(TAG,"aaCard="+aaCard)
     //if length>2 then suited
     //we need our preflop cards and we return a %
     ObjectUtils.debugLog(TAG,"aa/pg="+aaCard+" "+Cards_s0+Cards_s1 )

     if (aaCard.length < 3 && isS==false) {  //not suited
        val pfs = Cards_s0+Cards_s1
        if (pfs == aaCard) { ObjectUtils.debugLog(TAG,"not suited aa Match "+aaCard); aacf=aac}
        }

        else if (aaCard.length > 3 && isS==true)   //suited

        {
        //remove the parenthesis
        val aaCardStripped = aaCard.replaceAll ("[()]", "") //regex using java func
        val pfs = Cards_s0+Cards_s1
        if (pfs == aaCardStripped) { ObjectUtils.debugLog(TAG,"suited aa Match "+aaCard); aacf=aac}

     }


    //run transposed
    if (aaCard.length < 3 && isS==false) {  //not suited
    val pfs = Cards_s1+Cards_s0
      if (pfs == aaCard) { ObjectUtils.debugLog(TAG,"not suited aa Match "+aaCard); aacft=aac}
    }

    else if (aaCard.length > 3 && isS==true)   //suited

    {
      //remove the parenthesis
      val aaCardStripped = aaCard.replaceAll ("[()]", "") //regex using java func
    val pfs = Cards_s1+Cards_s0
      if (pfs == aaCardStripped) { ObjectUtils.debugLog(TAG,"suited aa Match "+aaCard); aacft=aac}

    }

  } //aaCard

    aacftx = aacft //default if both values are equal
    if (aacf > aacft) aacftx=aacf else if(aacf < aacft) aacftx=aacft  //differ so choose highest


    //aac is length, aacf is position
    //normalize
    val rex = ((((aacftx-1)-aac).abs)*100/aac)

    ObjectUtils.debugLog(TAG,"AA data: %="+rex+" aacftx "+aacftx)

  rex
  }




  //23-7-14 refactored
  def returnHand(hand:Int): String = {


    hand match {

      case hand if (hand > 6185) => "10. High Card"      // 1277 high card
      case hand if (hand > 3325) => "9. One Pair"       // 2860 one pair
      case hand if (hand > 2467) => "8. Two Pair"       //  858 two pair
      case hand if (hand > 1609) => "7. Three of a Kind"//  858 three-kind
      case hand if (hand > 1599) => "6. Straight"       //   10 straights
      case hand if (hand > 322)  => "5. Flush"           // 1277 flushes
      case hand if (hand > 166)  => "4. Full House 3+2"      //  156 full house
      case hand if (hand > 10)   => "3. Four of a Kind"   //  156 four-kind
      case hand if (hand>1)      => "2. Straight Flush"      //   10 straight-flushes
      case hand if (hand== 1)      => "1. Royal Flush"
      case hand if (hand==0)      =>  "V-LO-ER (Royal) Flush?" //TODO fix for ndkalg return 0 on rf

    }
  }


  def getDeviceName():String = {
    val manufacturer = Build.MANUFACTURER
    val model = Build.MODEL
      if (manufacturer.length >0) manufacturer.substring(0, 1).toUpperCase() + manufacturer.substring(1)+" "+model
       else if (model.length >0) model
        else "generic"
  }


  def debugLog(tag: String, str: String): Unit = {
    //Log.d(tag, str)
  }

  def showToast(toastText:String,ctx:Context): Unit = {
    //Toast.makeText(ctx, toastText, Toast.LENGTH_SHORT).show()
  }


  def nowt{}

}
}}}