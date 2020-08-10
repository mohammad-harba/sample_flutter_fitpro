package com.example.sample_flutter_fitpro;

public interface ISDKWrapper {

    ISDKWrapper setMacAddress(String macAddress);

    ISDKWrapper setName(String name);

    ISDKWrapper setDateForData(String date);

    void connectDevice();

    void connect();

    void startHeart();
}
