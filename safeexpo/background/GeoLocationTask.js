import * as BackgroundFetch from 'expo-background-fetch';
import * as TaskManager from 'expo-task-manager';
import * as Location from 'expo-location';
import { createUserGeoInfo } from '../component/CallBackend';

const LOCATION_TASK_NAME = 'background-location-task';
const FETCH_INTERVAL = 5000;

const RequestPermissions = async () => {
  const { status: foregroundStatus } =
    await Location.requestForegroundPermissionsAsync();
  if (foregroundStatus === 'granted') {
    const { status: backgroundStatus } =
      await Location.requestBackgroundPermissionsAsync();
    if (backgroundStatus === 'granted') {
      await Location.startLocationUpdatesAsync(LOCATION_TASK_NAME, {
        accuracy: Location.Accuracy.BestForNavigation,
        timeInterval: FETCH_INTERVAL,
      });
    }
  }
};

TaskManager.defineTask(LOCATION_TASK_NAME, async ({ data, error }) => {
  if (error) {
    console.log(error);
    return;
  }
  if (data) {
    try {
      const { locations } = data;
      createUserGeoInfo(locations);

      return data
        ? BackgroundFetch.BackgroundFetchResult.NewData
        : BackgroundFetch.BackgroundFetchResult.NoData;
    } catch (err) {
      console.log(err);
      return BackgroundFetch.BackgroundFetchResult.Failed;
    }
  } else {
    console.log('something strange');
  }
});

export default RequestPermissions;
