package com.reactnativejapaneseocr;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = JapaneseOcrModule.NAME)
public class JapaneseOcrModule extends ReactContextBaseJavaModule {
    public static final String NAME = "JapaneseOcr";

    public final TextRecognizer recognizer;


    public JapaneseOcrModule(ReactApplicationContext reactContext) {
        super(reactContext);
        recognizer = TextRecognition.getClient(new JapaneseTextRecognizerOptions.Builder().build());
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }


    @ReactMethod
    public void ocr(int a, int b, Promise promise) {
        InputImage image;

        try {
            image = InputImage.fromFilePath(reactContext, uri);
        } catch (IOException ignored) {}

        promise.resolve(result);

}
