import React from 'react';
import { useState, useEffect, useRef, useCallback } from 'react';
import { View, Platform, StyleSheet, Animated } from 'react-native';
import * as Device from 'expo-device';
import * as Notifications from 'expo-notifications';
import Constants from 'expo-constants';
import {
  withTheme,
  Button,
  Card,
  Title,
  Paragraph,
  Avatar,
  TextInput,
} from 'react-native-paper';
import { registerUserInfo } from '../component/CallBackend';
import {
  Gesture,
  LongPressGestureHandler,
  State,
} from 'react-native-gesture-handler';
import { Text } from 'react-native-paper';
import { sendPush } from '../component/CallBackend';

async function registerForPushNotificationsAsync() {
  let token;

  if (Platform.OS === 'android') {
    Notifications.setNotificationChannelAsync('default', {
      name: 'default',
      importance: Notifications.AndroidImportance.MAX,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: '#FF231F7C',
    });
  }

  if (Device.isDevice) {
    const { status: existingStatus } =
      await Notifications.getPermissionsAsync();
    let finalStatus = existingStatus;
    if (existingStatus !== 'granted') {
      const { status } = await Notifications.requestPermissionsAsync();
      finalStatus = status;
    }
    if (finalStatus !== 'granted') {
      alert('Failed to get push token for push notification!');
      return;
    }
    token = await Notifications.getExpoPushTokenAsync({
      projectId: Constants.expoConfig.extra.eas.projectId,
    });
    console.log(token);
  } else {
    alert('Must use physical device for Push Notifications');
  }

  return token.data;
}

function Settings() {
  const [expoPushToken, setExpoPushToken] = useState('');
  const [relationUserKey, setRelationUserKey] = React.useState('');
  const LeftContent = (props) => (
    <Avatar.Image {...props} source={require('../assets/safeexpo.png')} />
  );

  useEffect(() => {
    registerForPushNotificationsAsync().then((token) =>
      setExpoPushToken(token),
    );
  }, []);

  // gesture add
  const pressDuration = 5000; // 5초를 밀리초로
  const colorAnim = useRef(new Animated.Value(0)).current; // 애니메이션 값
  const [message, setMessage] = useState('Press and hold for 5 seconds');

  // 색상 변화 애니메이션
  const onColorChange = Animated.timing(colorAnim, {
    toValue: 1,
    duration: pressDuration,
    useNativeDriver: false,
  });

  // 길게 누르기 핸들러
  const longHandlerStateChange = ({ nativeEvent }) => {
    if (nativeEvent.state === State.ACTIVE) {
      onColorChange.start(); // 애니메이션 시작
    } else if (nativeEvent.state === State.END && nativeEvent.duration > 5000) {
      setMessage('alert sendded');
      sendPush();
    } else {
      colorAnim.setValue(0); // 초기화
      onColorChange.stop(); // 애니메이션 중지
    }
  };

  const resetHandlerParam = () => {
    setMessage('Press and hold for 5 seconds');
    colorAnim.setValue(0); // 초기화
    onColorChange.stop(); // 애니메이션 중지
  };

  // 색상 인터폴레이션
  const backgroundColor = colorAnim.interpolate({
    inputRange: [0, 1],
    outputRange: ['rgb(255, 255, 255)', 'rgb(255, 0, 0)'], // 흰색에서 붉은색으로
  });

  return (
    <View style={{ flex: 1, justifyContent: 'flex-end', padding: 20 }}>
      <Card>
        <Card.Title title="Settings" left={LeftContent} />
        <Card.Content>
          <Title>Your expo push token:</Title>
          <Paragraph>{expoPushToken}</Paragraph>
          <TextInput
            label="Relation User Key"
            value={relationUserKey}
            onChangeText={(relationUserKey) =>
              setRelationUserKey(relationUserKey)
            }
          />
        </Card.Content>
        <Card.Actions>
          <Button
            mode="contained"
            onPress={() => registerUserInfo(expoPushToken, relationUserKey)}
          >
            Registration
          </Button>
        </Card.Actions>
      </Card>
      <LongPressGestureHandler onHandlerStateChange={longHandlerStateChange}>
        <Animated.View
          style={[styles.box, { backgroundColor: backgroundColor }]}
        >
          <Text>{message}</Text>
          <Button mode="contained" onPress={() => resetHandlerParam()}>
            Reset
          </Button>
        </Animated.View>
      </LongPressGestureHandler>
    </View>
  );
}

const styles = StyleSheet.create({
  box: {
    height: '50%', // 화면의 절반 높이
    backgroundColor: '#b58df1',
    borderRadius: 20,
    marginBottom: 30,
    alignContents: 'center',
    justifyContent: 'center',
    alignItems: 'center',
  },
});
export default withTheme(Settings);
