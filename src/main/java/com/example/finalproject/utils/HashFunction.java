package com.example.finalproject.utils;

import javafx.scene.control.DatePicker;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFunction {
    public HashFunction() {
    }

    public String getHash(String array, String algorithm){
        String hashValue = null;
        try{
            MessageDigest messageDigest = MessageDigest.getInstance(algorithm); //MD5
            if(array != null) {
                messageDigest.update(array.getBytes());
                byte[] digesttedBytes = messageDigest.digest();
                StringBuilder sb = new StringBuilder();

                for (byte b : digesttedBytes) {
                    sb.append(String.format("%02x", b));
                }
                hashValue = sb.toString();
            }
            else{
                return hashValue;
            }
        }
        catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
        return hashValue;
    }
}
