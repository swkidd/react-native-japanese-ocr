import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-japanese-ocr' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo managed workflow\n';

const JapaneseOcr = NativeModules.JapaneseOcr
  ? NativeModules.JapaneseOcr
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function ocr(path: string): Promise<any> {
  return JapaneseOcr.ocr(path);
}

export function ocrFromURL(path: string): Promise<any> {
  return JapaneseOcr.ocrFromURL(path);
}

export function ocrFromBase64(base64String: string): Promise<any> {
  return JapaneseOcr.ocrFromBase64(base64String);
}
