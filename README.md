# react-native-japanese-ocr

A React-Native package for preforming OCR on Japanese text using Google MLKit TextRecognition v2

## Installation

```sh
npm install react-native-japanese-ocr
```

## Usage

```js
import { ocr } from "react-native-japanese-ocr";

// ...

const result = await ocr(path).then(response => ...);
```

```js
import { ocrFromURL } from "react-native-japanese-ocr";

// ...

const result = await ocrFromURL(url).then(response => ...);
```

### Example Response
```json
  [
    {
      "bounding": {
        "height": 0.0,
        "width": 0.0,
        "origin": {
          "x": 0.0,
          "y": 0.0
        }
      },
      "text": "",
      "lines": [
        {
          "text": "",
          "bounding": {
            "height": 0.0,
            "width": 0.0,
            "origin": {
              "x": 0.0,
              "y": 0.0
            }
          },
          "elements": [
            {
              "text": "",
              "bounding": {
                "height": 0.0,
                "width": 0.0,
                "origin": {
                  "x": 0.0,
                  "y": 0.0
                }
              },
              "cornerPoints": [
                {
                  "x": 0.0,
                  "y": 0.0
                },
                {
                  "x": 0.0,
                  "y": 0.0
                },
                {
                  "x": 0.0,
                  "y": 0.0
                },
                {
                  "x": 0.0,
                  "y": 0.0
                }
              ]
            }
          ],
          "cornerPoints": [
            {
              "x": 0.0,
              "y": 0.0
            },
            {
              "x": 0.0,
              "y": 0.0
            },
            {
              "x": 0.0,
              "y": 0.0
            },
            {
              "x": 0.0,
              "y": 0.0
            }
          ]
        }
      ],
      "cornerPoints": [
        {
          "x": 0.0,
          "y": 0.0
        },
        {
          "x": 0.0,
          "y": 0.0
        },
        {
          "x": 0.0,
          "y": 0.0
        },
        {
          "x": 0.0,
          "y": 0.0
        }
      ]
    }
  ]
```
### Uses Google MLKit TextRecognition v2
[documentation](https://developers.google.com/ml-kit/vision/text-recognition/v2)

## License

MIT
