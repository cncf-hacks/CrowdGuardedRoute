import React, { useState, useEffect, useRef } from 'react';
import { StyleSheet, View, Text, Dimensions } from 'react-native';
import MapView, { Marker, PROVIDER_GOOGLE, Polyline } from 'react-native-maps';
import { GooglePlacesAutocomplete } from 'react-native-google-places-autocomplete';
import {
  withTheme,
  IconButton,
  MD3Colors,
  Modal,
  Button,
} from 'react-native-paper';
import * as Location from 'expo-location';
import Geocoder from 'react-native-geocoding';
import Constants from 'expo-constants';
import { getRecommend } from '../component/CallBackend';

function App() {
  const [location, setLocation] = useState(null); //current location
  const mapRef = useRef(null);
  const [coordinatesA, setCoordinatesA] = useState([]);
  const [coordinatesB, setCoordinatesB] = useState([]);
  const [coordinatesC, setCoordinatesC] = useState([]);

  // current location setting
  useEffect(() => {
    (async () => {
      let { status } = await Location.requestForegroundPermissionsAsync();
      if (status !== 'granted') {
        console.error('Permission to access location was denied');
        return;
      }

      let currentLocation = await Location.getCurrentPositionAsync({});
      setLocation({
        latitude: currentLocation.coords.latitude,
        longitude: currentLocation.coords.longitude,
        latitudeDelta: 0.0922,
        longitudeDelta: 0.0421,
      });
    })();
  }, []);

  // Google Maps Directions API 키를 변수로 저장
  // const apiKey = process.env.EXPO_PUBLIC_API_KEY;
  const apiKey = Constants.expoConfig.android.config.googleMaps.apiKey;

  // direction api 호출
  const getDirections = async () => {
    const destination =
      `${selectedPlace.latitude}` + ',' + `${selectedPlace.longitude}`;
    let currentLocation = await Location.getCurrentPositionAsync({});
    const origin =
      currentLocation.coords.latitude + ',' + currentLocation.coords.longitude;
    const mode = 'walking';
    try {
      const response = await fetch(
        `https://maps.googleapis.com/maps/api/directions/json?origin=${origin}&destination=${destination}&mode=${mode}&alternatives=true&key=${apiKey}`,
      );
      const json = await response.json().catch((error) => console.log(error));
      if (json.routes && json.routes.length > 0) {
        // 경로가 하나 이상 있는 경우, 경로 정보를 처리합니다.
        getRecommend(json.routes).then((res) => {
          setCoordinatesA(
            res[0].score.map((point) => ({
              latitude: point.lat,
              longitude: point.lon,
            })),
          );
          setCoordinatesB(
            res[1].score.map((point) => ({
              latitude: point.lat,
              longitude: point.lon,
            })),
          );
          setCoordinatesC(
            res[2].score.map((point) => ({
              latitude: point.lat,
              longitude: point.lon,
            })),
          );
        });
      }
    } catch (error) {
      console.error(error);
    }
  };

  // search location
  const [modalVisible, setModalVisible] = React.useState(false);

  const showModal = () => setModalVisible(true);
  const hideModal = () => setModalVisible(false);
  const [selectedPlace, setSelectedPlace] = useState({});
  const handleSelectPlace = (data, details) => {
    // 'details' is provided when fetchDetails = true
    setSelectedPlace({
      name: data.structured_formatting.main_text,
      address: details.formatted_address,
      latitude: details.geometry.location.lat,
      longitude: details.geometry.location.lng,
      description: data.description,
      latitudeDelta: 0.0522,
      longitudeDelta: 0.0221,
    });
  };

  // marker handler
  const handleMapPress = (e) => {
    Geocoder.init(apiKey);

    // 좌표로부터 주소 정보 검색
    Geocoder.from(e.nativeEvent.coordinate)
      .then((json) => {
        var addressComponent = json.results[0].address_components[0];
        setSelectedPlace({
          name: addressComponent.long_name,
          address: json.results[0].formatted_address,
          latitude: json.results[0].geometry.location.lat,
          longitude: json.results[0].geometry.location.lng,
          // description: data.description,
          latitudeDelta: 0.0522,
          longitudeDelta: 0.0221,
        });
      })
      .catch((error) => console.warn(error));
  };

  useEffect(() => {
    if (selectedPlace.latitude) {
      showModal();
    }
    mapRef.current.animateToRegion(selectedPlace, 500);
  }, [selectedPlace]);

  return (
    <View style={styles.container}>
      <MapView
        ref={mapRef}
        style={{ flex: 1 }}
        initialRegion={
          location
            ? location
            : {
                latitude: 48.82688,
                longitude: 2.289502,
                latitudeDelta: 0.0922,
                longitudeDelta: 0.0421,
              }
        }
        onMapReady={() => {
          if (location) {
            mapRef.current.animateToRegion(location, 500);
          }
        }}
        provider={PROVIDER_GOOGLE} // Google Maps를 사용하기 위해 provider 설정
        showsUserLocation={true}
        region={location}
        onRegionChangeComplete={(newRegion) => setLocation(newRegion)}
        onPress={handleMapPress} // 지도를 눌렀을 때 handleMapPress 함수 호출
      >
        {selectedPlace.latitude && (
          <Marker
            coordinate={{
              latitude: selectedPlace.latitude,
              longitude: selectedPlace.longitude,
            }}
            title={selectedPlace.name}
            description={selectedPlace.address}
          />
        )}
        <Polyline
          coordinates={coordinatesC}
          strokeColor="#FF0000"
          lineDashPhase={'1'}
          lineDashPattern={[10, 10]}
          strokeWidth={5}
        />
        <Polyline
          coordinates={coordinatesB}
          strokeColor="#00FF00"
          lineDashPhase={'1'}
          lineDashPattern={[10, 10]}
          strokeWidth={5}
        />
        <Polyline
          coordinates={coordinatesA}
          lineDashPhase={'1'}
          lineDashPattern={[10, 10]}
          strokeColor="#0000FF"
          strokeWidth={5}
        />
      </MapView>
      <View style={styles.searchContainer}>
        <GooglePlacesAutocomplete
          ref={(instance) => {
            this.googlePlacesRef = instance;
          }}
          placeholder="검색"
          fetchDetails={true}
          onPress={(data, details = null) => {
            // 'details'는 사용자가 선택한 장소의 상세 정보입니다.
            handleSelectPlace(data, details);
          }}
          query={{
            key: apiKey,
            language: 'ko',
          }}
        />
        <IconButton
          icon="google-maps"
          mode="contained"
          animated={true}
          iconColor={MD3Colors.primary10}
          containerColor={MD3Colors.neutral100}
          onPress={() => console.log('Search for:', searchTerm)}
        />
      </View>
      <Modal
        animationType="slide"
        transparent={false}
        visible={modalVisible}
        onDismiss={hideModal}
        contentContainerStyle={styles.containerStyle}
      >
        <View style={styles.modalContent}>
          <View style={styles.modalText}>
            <Text variant="titleMedium">Name: {selectedPlace.name}</Text>
            <Text variant="titleMedium">Address: {selectedPlace.address}</Text>
          </View>
          <View>
            <Button icon="directions" mode="contained" onPress={getDirections}>
              walk!
            </Button>
          </View>
        </View>
      </Modal>
    </View>
  );
}
const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
  },
  map: {
    ...StyleSheet.absoluteFillObject,
  },
  searchContainer: {
    position: 'absolute',
    top: 40,
    left: 10,
    right: 10,
    backgroundColor: 'white',
    flexDirection: 'row',
    alignItems: 'center',
    padding: 0,
    borderRadius: 10,
    borderStartStartRadius: 10,
    borderEndStartRadius: 10,
  },
  searchBox: {
    flex: 1,
    backgroundColor: 'white',
  },
  modalContent: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'stretch', // 왼쪽 정렬을 위해
    padding: 20, // 텍스트 주변에 패딩을 추가
  },
  modalText: {
    flexDirection: 'column',
    justifyContent: 'space-around',
    width: '75%',
  },
  containerStyle: {
    position: 'absolute',
    bottom: 0,
    width: '100%',
    height: modalHeight,
    backgroundColor: 'white',
  },
});
const screenHeight = Dimensions.get('window').height;
const modalHeight = screenHeight * 0.3; // 화면의 30%

export default withTheme(App);
