package com.reactnativejapaneseocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
  public void ocrFromURL(String path, Promise promise) {
    URL url = null;

    try {
      url = new URL(path);
    } catch (MalformedURLException e) {
      promise.reject(e);
      e.printStackTrace();
    }

    Bitmap bitmap = null;
    try {
      bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
      ocrFromImage(InputImage.fromBitmap(bitmap, 0), promise);
    } catch (IOException e) {
      promise.reject(e);
      e.printStackTrace();
    }
  }

  @ReactMethod
  public void ocr(String uriString, Promise promise) {
    Uri uri = Uri.parse(Uri.decode(uriString));
    InputImage image;

    try {
      image = InputImage.fromFilePath(getReactApplicationContext(), uri);
      ocrFromImage(image, promise);
    } catch (IOException e) {
      promise.reject(e);
    }
  }

  public void ocrFromImage(InputImage image, Promise promise) {
    recognizer.process(image)
      .addOnSuccessListener(result -> {
        promise.resolve(convertResult(result));
      })
      .addOnFailureListener(
        e -> {
          promise.reject(e);
        });
  }

  private WritableMap getCoordinates(Rect boundingBox) {
    WritableMap coordinates = new WritableNativeMap();
    if (boundingBox == null) {
      coordinates.putNull("top");
      coordinates.putNull("left");
      coordinates.putNull("width");
      coordinates.putNull("height");
    } else {
      coordinates.putDouble("top", boundingBox.top);
      coordinates.putDouble("left", boundingBox.left);
      coordinates.putDouble("width", boundingBox.width());
      coordinates.putDouble("height", boundingBox.height());
    }
    return coordinates;
  }

  private WritableArray getCornerPoints(Point[] pointsList) {
    WritableArray p = new WritableNativeArray();
    if (pointsList == null) {
      return p;
    }

    for (Point point : pointsList) {
      WritableMap i = new WritableNativeMap();
      i.putDouble("x", point.x);
      i.putDouble("y", point.y);
      p.pushMap(i);
    }

    return p;
  }


  private WritableArray convertResult(Text visionText) {
    WritableArray data = new WritableNativeArray();

    for (Text.TextBlock block : visionText.getTextBlocks()) {
      WritableArray blockElements = new WritableNativeArray();
      for (Text.Line line : block.getLines()) {
        WritableArray lineElements = new WritableNativeArray();
        for (Text.Element element : line.getElements()) {
          WritableMap e = new WritableNativeMap();
          e.putString("text", element.getText());
          e.putMap("bounding", getCoordinates(element.getBoundingBox()));
          e.putArray("cornerPoints", getCornerPoints(element.getCornerPoints()));
          lineElements.pushMap(e);
        }
        WritableMap l = new WritableNativeMap();
        WritableMap lCoordinates = getCoordinates(line.getBoundingBox());
        l.putString("text", line.getText());
        l.putMap("bounding", lCoordinates);
        l.putArray("elements", lineElements);
        l.putArray("cornerPoints", getCornerPoints(line.getCornerPoints()));
        blockElements.pushMap(l);
      }
      WritableMap b = new WritableNativeMap();
      b.putMap("bounding", getCoordinates(block.getBoundingBox()));
      b.putString("text", block.getText());
      b.putArray("lines", blockElements);
      b.putArray("cornerPoints", getCornerPoints(block.getCornerPoints()));
      data.pushMap(b);
    }
    return data;
  }
}
