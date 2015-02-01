package com.droidroid.PM2;

import android.content.Context;
import android.os.AsyncTask;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.*;
import java.security.Security;


/**
Decrypts and reads our raw resources using a symmetric cipher  - using 128-bit AES encrypted using OpenSSL on CentOS 5.7

linux command line: openssl enc -aes-128-cbc -in lookup.csv -out mashenc



 */
public class Crypto_htable {

    final String TAG="Crypto_htable";
    //padding issues
    //01-22 03:34:23.448: DEBUG/Crypto_htable(21279): decrypt error=javax.crypto.BadPaddingException: pad block corrupted    = when using AES secretkeyspec  + correct key
    //01-22 03:36:06.218: DEBUG/Crypto_htable(21405): decrypt error=javax.crypto.BadPaddingException: error:06065064:digital envelope routines:EVP_DecryptFinal_ex:bad decrypt   = using AES/CBC/PKCS5Padding + correct key
    //01-22 03:38:15.098: DEBUG/Crypto_htable(21507): decrypt error=java.security.InvalidKeyException: Unsupported key size: xx bytes  = using invalid or valid key less than 16 bits (chars) with AES/CBC/PKCS5Padding
    //moved to Bouncy Castle..... and used salt (for openssl encrypted file)


    public int[] htableTable_1 = new int[7462];
    String[] htableTable_2 = new String[7462];
    String[] htableTable_3 = new String[7462];


    void decrypt(Context ctx, Integer rid, String ek) throws Exception {

    Security.addProvider(new BouncyCastleProvider());

    InputStream rsc = ctx.getResources().openRawResource(rid);

    byte[] encrypted = org.apache.commons.io.IOUtils.toByteArray(rsc);

        ObjectUtils.debugLog(TAG,"Crypto_htable: length is "+encrypted.length );

    Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");

    // Openssl puts SALTED__ then the 8 byte salt at the start of the file.  We simply copy it out.
    byte[] salt = new byte[8];
    System.arraycopy(encrypted, 8, salt, 0, 8);
    SecretKeyFactory fact = SecretKeyFactory.getInstance("PBEWITHMD5AND128BITAES-CBC-OPENSSL", "BC");
    c.init(Cipher.DECRYPT_MODE, fact.generateSecret(new PBEKeySpec((ek).toCharArray(), salt, 100)));

    // Decrypt the rest of the byte array (after stripping off the salt)
    byte[] data = c.doFinal(encrypted, 16, encrypted.length-16);
        //ObjectUtils.debugLog(TAG,"Crypto_htable: "+data); //now place csv into array/list
        writePlaintext(data);

    }



   void writePlaintext(byte[] plaintext) {

       int ct=0;

       InputStream is = null;
       BufferedReader bfReader = null;
       try {
           is = new ByteArrayInputStream(plaintext);
           bfReader = new BufferedReader(new InputStreamReader(is));
           String temp = null;
           while((temp = bfReader.readLine()) != null)
           {
               //ObjectUtils.debugLog(TAG,temp); //now place csv into array/list

               String[] dataLine = temp.split(",");

                       //htableTable_1  [ct] =  Integer.parseInt(dataLine [0].trim());  //same as index
                       htableTable_2  [ct]=   dataLine [7].trim();   //descriptor
                       htableTable_3  [ct]=   dataLine [5];          //card-layout

                       ct++;

           }
       } catch (IOException e) {
           e.printStackTrace();
       } finally {
           try{
               if(is != null) is.close();
           } catch (Exception ex){
               ex.printStackTrace();
           }
       }

    } //decrypt





} //crypto
