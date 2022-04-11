package com.reactnativejapaneseocr;

import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;

import java.io.IOException;

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
  public void ocr(String uriString, Promise promise) {
    Uri uri = Uri.parse(Uri.decode(uriString));
    InputImage image;

    try {
      image = InputImage.fromFilePath(getReactApplicationContext(), uri);

      recognizer.process(image)
        .addOnSuccessListener(result -> {
          promise.resolve(convertResult(result));
        })
        .addOnFailureListener(
          e -> {
            promise.resolve(null);
          });
    } catch (IOException ignored) {
      promise.resolve(null);
    }
  }


  private WritableMap getCoordinates(Rect boundingBox) {
    WritableMap coordinates = Arguments.createMap();
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
    WritableArray p = Arguments.createArray();
    if (pointsList == null) {
      return p;
    }
    for (Point point : pointsList) {
      WritableMap i = Arguments.createMap();
      i.putDouble("x", point.x);
      i.putDouble("y", point.y);
      p.pushMap(i);
    }

    return p;
  }


  private WritableArray convertResult(Text visionText) {
    WritableArray data = Arguments.createArray();

    for (Text.TextBlock block : visionText.getTextBlocks()) {
      WritableArray blockElements = Arguments.createArray();
      for (Text.Line line : block.getLines()) {
        WritableArray lineElements = Arguments.createArray();
        for (Text.Element element : line.getElements()) {
          WritableMap e = Arguments.createMap();
          e.putString("text", element.getText());
          e.putMap("bounding", getCoordinates(element.getBoundingBox()));
          e.putArray("cornerPoints", getCornerPoints(element.getCornerPoints()));
          lineElements.pushMap(e);
          WritableMap l = Arguments.createMap();
          WritableMap lCoordinates = getCoordinates(line.getBoundingBox());
          l.putString("text", line.getText());
          l.putMap("bounding", lCoordinates);
          l.putArray("elements", lineElements);
          l.putArray("cornerPoints", getCornerPoints(line.getCornerPoints()));

          blockElements.pushMap(l);

          WritableMap b = Arguments.createMap();
          b.putMap("bounding", getCoordinates(block.getBoundingBox()));
          b.putString("text", block.getText());
          b.putArray("lines", blockElements);
          b.putArray("cornerPoints", getCornerPoints(block.getCornerPoints()));
          data.pushMap(b);
        }
      }
    }
    return data;
  }
}
