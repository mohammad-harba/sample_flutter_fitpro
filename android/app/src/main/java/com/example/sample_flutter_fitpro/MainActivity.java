package com.example.sample_flutter_fitpro;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.nio.channels.Channel;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private String CHANNEL = "samples.flutter.dev/fitpro";
    ISDKWrapper lfw;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("onCreate Started -----------------------");
        lfw = new FitProWrapper(this);
    }


    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            _methodExecute(call, result);
                        }
                );
    }


    private void _methodExecute(MethodCall call, MethodChannel.Result result) {

        System.out.println(lfw);


            System.out.println("we are here ------------------------------------------");
            if(lfw == null) {
                //lfw = SDKWrapper.getCorrectSdk(call.argument("name"), this);
                //lfw.setMacAddress(call.argument("mac")).setName(call.argument("name"));
            }
        System.out.println("METHOD CALLED: " + call.method);
        System.out.println("calling get correct SDK --- ");

        System.out.println("Device name: " + call.argument("name"));
        System.out.println("Device mac: " + call.argument("mac"));


        switch (call.method) {
            case "connectDevice":
                lfw.setMacAddress(call.argument("mac")).setName(call.argument("name"));
                System.out.println("connect device called------------");
                lfw.connectDevice();
                break;
            case "startMeasure":
                System.out.println("start heart called --------------");
                lfw.startHeart();
                break;
        }
    }
}
