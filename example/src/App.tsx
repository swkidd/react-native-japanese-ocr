import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { ocrFromBase64 } from 'react-native-japanese-ocr';

const url = '';

export default function App() {
  const [result, setResult] = React.useState();

  React.useEffect(() => {
    ocrFromBase64(url)
      .then((r) => {
        let text = '';
        r.forEach((b) => (text += b.text));
        setResult(text);
      })
      .catch((e) => console.log(e));
  }, []);

  return (
    <View style={styles.container}>
      <Text selectable={true}>Result: {result}</Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  box: {
    width: 60,
    height: 60,
    marginVertical: 20,
  },
});
