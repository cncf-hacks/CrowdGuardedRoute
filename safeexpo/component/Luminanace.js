import React, { useState, useEffect } from 'react';
import { LightSensor } from 'expo-sensors';
import { updateUserLightInfo } from './CallBackend';
import * as Location from 'expo-location';

export default function Luminance() {
  const [{ illuminance }, setData] = useState({ illuminance: 0 });

  useEffect(() => {
    _toggle();

    return () => {
      _unsubscribe();
    };
  }, []);

  const _toggle = () => {
    if (this._subscription) {
      _unsubscribe();
    } else {
      _subscribe();
    }
  };

  const _subscribe = () => {
    this._subscription = LightSensor.addListener(makeLightAndSend);
    LightSensor.setUpdateInterval(1000);
  };

  const _unsubscribe = () => {
    this._subscription && this._subscription.remove();
    this._subscription = null;
  };
}

async function makeLightAndSend({ illuminance }) {
  let location = await Location.getCurrentPositionAsync({});
  const update = await updateUserLightInfo(location, illuminance);
}
