import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { ocrFromURL } from 'react-native-japanese-ocr';

const url =
  'https://upload.wikimedia.org/wikipedia/commons/thumb/4/42/Road_traffic_sign_with_Russian%2C_Nemuro%2C_Hokkaido%2C_Japan.jpg/360px-Road_traffic_sign_with_Russian%2C_Nemuro%2C_Hokkaido%2C_Japan.jpg';

export default function App() {
  const [result, setResult] = React.useState<number | undefined>();

  React.useEffect(() => {
    ocrFromURL(url)
      .then((r) => setResult(JSON.stringify(r)))
      .catch((e) => console.log(e));
  }, []);

  return (
    <View style={styles.container}>
      <Text>Result: {result}</Text>
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
