import React, { useEffect, useState } from 'react';
import { Tabs } from 'expo-router';
import RequestPermissions from '../background/GeoLocationTask';
import { MaterialCommunityIcons } from 'react-native-vector-icons';
import { PaperProvider } from 'react-native-paper';
import Luminance from '../component/Luminanace';
import EmergencyAlert from '../component/EmergencyCall';
import * as Notifications from 'expo-notifications';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

// 알림 처리 핸들러 설정
Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: true,
  }),
});

export default function Layout() {
  const [alertVisible, setAlertVisible] = useState(false);
  const [alertMessage, setAlertMessage] = useState('');
  RequestPermissions();
  Luminance();
  // notification listener
  useEffect(() => {
    const subscription = Notifications.addNotificationReceivedListener(
      (notification) => {
        setAlertMessage(notification.request.content);
        setAlertVisible(true);
      },
    );

    return () => {
      Notifications.removeNotificationSubscription(subscription);
    };
  }, []);

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <PaperProvider>
        <Tabs screenOptions={{ tabBarActiveTintColor: 'black' }}>
          <Tabs.Screen
            name="index"
            options={{
              title: 'route',
              headerShown: false,
              tabBarIcon: ({ focused, size }) => {
                // foo 탭에 대한 아이콘 설정
                let iconName = focused ? 'map-search' : 'map-search-outline';
                return <MaterialCommunityIcons name={iconName} size={size} />;
              },
            }}
          />
          <Tabs.Screen
            name="settings"
            options={{
              headerShown: false,
              tabBarIcon: ({ focused, size }) => {
                // foo 탭에 대한 아이콘 설정
                let iconName = focused
                  ? 'content-save-settings'
                  : 'content-save-settings-outline';
                return <MaterialCommunityIcons name={iconName} size={size} />;
              },
            }}
          />
        </Tabs>
        <EmergencyAlert
          visible={alertVisible}
          message={alertMessage}
          onDismiss={() => setAlertVisible(false)}
        />
      </PaperProvider>
    </GestureHandlerRootView>
  );
}
