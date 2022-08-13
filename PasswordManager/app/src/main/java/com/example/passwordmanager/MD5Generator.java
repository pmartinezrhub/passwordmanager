package com.example.passwordmanager;

import java.security.MessageDigest;

public class MD5Generator {
    private String stringToHash;
    private String generatedString = null;
    public MD5Generator(String aString){
        try {
            stringToHash = aString;
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(stringToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            generatedString = sb.toString();
        }
        catch(Exception e){
            System.out.println(e);
        }
        //System.out.println(generatedPassword);
    }
    public String getGenerated_password(){
        return generatedString;
    }
}
