package com.example.sample_flutter_fitpro;

import android.content.SharedPreferences;

import java.util.Arrays;

import com.example.sample_flutter_fitpro.FitProWrapper;


abstract public class SDKWrapper {
    public static String[] devicesNames;

    public static boolean isSdkWrapperCorrect(String deviceName, String []devicesNames) {
        SDKWrapper.devicesNames = devicesNames;
        System.out.println("DEVICE: " + devicesNames);
        return deviceName != null && Arrays.asList(SDKWrapper.devicesNames).contains(deviceName);
    }

    public static ISDKWrapper getCorrectSdk(String deviceName, MainActivity mainActivity) {
        if(deviceName == null || deviceName.equals("")) {
            SharedPreferences sharedPreferences = mainActivity.getPreferences(mainActivity.MODE_PRIVATE);
            deviceName = "LH716";
        }

         if(FitProWrapper.isSdkWrapperCorrect(deviceName, new String[]{"LH716"})) {
            System.out.println("fit pro wrapper called");
         }
        return new FitProWrapper(mainActivity);

    }
}



