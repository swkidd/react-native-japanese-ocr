import * as React from 'react';

import { StyleSheet, View, Text } from 'react-native';
import { ocrFromURL } from 'react-native-japanese-ocr';

const url = 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRjU3vG_TIsinFxVHu8mfO9q2KhnkVyUfFmuQ&usqp=CAU';

export default function App() {
  const [result, setResult] = React.useState();

  React.useEffect(() => {
    ocrFromURL(url)
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
