import axios from 'axios';
import Constants from 'expo-constants';

const uuid = Constants.installationId;
const uniqueKey = uuid.slice(-6);

export async function CallBackend(uri, payload) {
  try {
    const response = await axios.post(
      Constants.expoConfig.extra.backend + uri,
      { payload: payload },
    );
    if (response.status === 201 || response.status === 200) {
      return response.data;
    } else {
      console.log('not created', response.status, response.data);
      return null; // 또는 적절한 에러 객체 반환
    }
  } catch (error) {
    console.error('CallBackend error', error);
    throw error; // 에러를 상위로 전파
  }
}

export function registerUserInfo(expoPushToken, relationUserKey) {
  const data = {
    uniqueKey: uniqueKey,
    uuid: uuid,
    pushKey: expoPushToken,
    relationKey: relationUserKey,
  };
  console.log(data);
  CallBackend('/location/collect/v1/createUserDeviceInfo', data);
}

export function createUserGeoInfo(location) {
  const data = {
    uniqueKey: uniqueKey,
    latitude: location[0].coords.latitude,
    longitude: location[0].coords.latitude,
    speed: location[0].coords.speed,
    light: 0,
    uuid: uuid,
  };
  const rtn = CallBackend('/location/collect/v1/createUserGeoInfo', data);
  console.log(rtn);
}

export function updateUserLightInfo(location, light) {
  const data = {
    uniqueKey: uniqueKey,
    latitude: location.coords.latitude,
    longitude: location.coords.latitude,
    speed: location.coords.speed,
    light: light,
    uuid: uuid,
  };
  const rtn = CallBackend('/location/collect/v1/updateUserLightInfo', data);
}

const transformedDataAsync = async (direction) => {
  return direction.map((route) => {
    return route.legs.flatMap((leg) =>
      leg.steps.map((step) => ({
        uniqueKey: uniqueKey,
        latitude: step.end_location.lat,
        longitude: step.end_location.lng,
      })),
    );
  });
};

export async function getRecommend(direction) {
  try {
    const transformedData = await transformedDataAsync(direction);
    // console.log('transformedData is: ', transformedData);
    const response = await CallBackend(
      '/location/route/v1/recommend',
      transformedData,
    );
    return response.payload;
  } catch (error) {
    console.error('getRecommend error', error);
    return null; // 또는 적절한 에러 객체 반환
  }
}

export function sendPush() {
  const data = {
    uniqueKey: uniqueKey,
    title: 'Alert',
    body: 'your friend need help',
  };
  CallBackend('/location/push', data);
}
