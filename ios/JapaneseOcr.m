#import "JapaneseOcr.h"

@import MLKitVision;
@import MLKitTextRecognitionCommon;
@import MLKitTextRecognitionJapanese;

@implementation JapaneseOcr

+ (MLKTextRecognizer *)recognizer {
  static MLKTextRecognizer *recognizer = nil;
  if (recognizer == nil) {
    MLKJapaneseTextRecognizerOptions *japaneseOptions = [[MLKJapaneseTextRecognizerOptions alloc] init];
    recognizer = [MLKTextRecognizer textRecognizerWithOptions:japaneseOptions];
  }
  return recognizer;
}

RCT_EXPORT_MODULE()

RCT_REMAP_METHOD(ocr,
                 path:(NSString *)path
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    NSFileManager *fileManager = [NSFileManager defaultManager];
    BOOL isFileExist = [fileManager fileExistsAtPath:path];
    UIImage *image;
    if (isFileExist) {
        image = [[UIImage alloc] initWithContentsOfFile:path];
        MLKVisionImage *visionImage = [[MLKVisionImage alloc] initWithImage:image];

        void (^ocrCompletionBlock)(MLKText *_Nullable, NSError *_Nullable) = ^(MLKText *_Nullable result, NSError *_Nullable error) {
          if (error != nil || result == nil) {
              reject(@"error", @"ocr-error", error);
          }
          resolve(prepareOutput(result));
        };

        [[JapaneseOcr recognizer] processImage:visionImage completion:ocrCompletionBlock];

    }
    else {
       reject(@"error", @"file-not-found", nil);
    }
}

RCT_REMAP_METHOD(ocrFromURL,
                 url:(NSString *)url
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    NSData * imageData = [[NSData alloc] initWithContentsOfURL: [NSURL URLWithString:url]];
    UIImage* image = [UIImage imageWithData: imageData];
    MLKVisionImage *visionImage = [[MLKVisionImage alloc] initWithImage:image];

    void (^ocrCompletionBlock)(MLKText *_Nullable, NSError *_Nullable) = ^(MLKText *_Nullable result, NSError *_Nullable error) {
      if (error != nil || result == nil) {
          reject(@"error", @"ocr-error", error);
      }
      resolve(prepareOutput(result));
    };

    [[JapaneseOcr recognizer] processImage:visionImage completion:ocrCompletionBlock];
}

RCT_REMAP_METHOD(ocrFromBase64,
                 base64String:(NSString *)base64String
                 withResolver:(RCTPromiseResolveBlock)resolve
                 withRejecter:(RCTPromiseRejectBlock)reject)
{
    NSArray *components = [base64String componentsSeparatedByString:@","];
    NSString *dataString = [components lastObject];
    NSData *data = [[NSData alloc] initWithBase64EncodedString:dataString options:NSDataBase64DecodingIgnoreUnknownCharacters];
    UIImage *image = [UIImage imageWithData:data];
    MLKVisionImage *visionImage = [[MLKVisionImage alloc] initWithImage:image];

    void (^ocrCompletionBlock)(MLKText *_Nullable, NSError *_Nullable) = ^(MLKText *_Nullable result, NSError *_Nullable error) {
      if (error != nil || result == nil) {
          reject(@"error", @"ocr-error", error);
      }
      resolve(prepareOutput(result));
    };

    [[JapaneseOcr recognizer] processImage:visionImage completion:ocrCompletionBlock];
}

NSMutableArray* getCornerPoints(NSArray *cornerPoints) {
    NSMutableArray *result = [NSMutableArray array];
    
    if (cornerPoints == nil) {
        return result;
    }
    for (NSValue  *point in cornerPoints) {
        NSMutableDictionary *resultPoint = [NSMutableDictionary dictionary];
        // values altered to rotate clockwise 90 degrees
        [resultPoint setObject:[NSNumber numberWithFloat:point.CGPointValue.y] forKey:@"x"];
        [resultPoint setObject:[NSNumber numberWithFloat:point.CGPointValue.x] forKey:@"y"];
        [result addObject:resultPoint];
    }
    return result;
}

NSDictionary* getBounding(CGRect frame) {
    // values altered to rotate clockwise 90 degrees
    return @{
       @"width": @(frame.size.height),
       @"height": @(frame.size.width),
       @"origin": @{
        @"y": @(frame.origin.x),
        @"x": @(frame.origin.y)
       }
   };
}

NSMutableArray* prepareOutput(MLKText *result) {
    NSMutableArray *output = [NSMutableArray array];
    for (MLKTextBlock *block in result.blocks) {
        
        NSMutableArray *blockElements = [NSMutableArray array];
        for (MLKTextLine *line in block.lines) {
            NSMutableArray *lineElements = [NSMutableArray array];
            for (MLKTextElement *element in line.elements) {
                NSMutableDictionary *e = [NSMutableDictionary dictionary];
                e[@"text"] = element.text;
                e[@"cornerPoints"] = getCornerPoints(element.cornerPoints);
                e[@"bounding"] = getBounding(element.frame);
                [lineElements addObject:e];
            }
            
            NSMutableDictionary *l = [NSMutableDictionary dictionary];
            l[@"text"] = line.text;
            l[@"cornerPoints"] = getCornerPoints(line.cornerPoints);
            l[@"elements"] = lineElements;
            l[@"bounding"] = getBounding(line.frame);
            [blockElements addObject:l];
        }
        
        NSMutableDictionary *b = [NSMutableDictionary dictionary];
        b[@"text"] = block.text;
        b[@"cornerPoints"] = getCornerPoints(block.cornerPoints);
        b[@"bounding"] = getBounding(block.frame);
        b[@"lines"] = blockElements;
        [output addObject:b];
    }
    return output;
}

@end
