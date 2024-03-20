# CrowdGuardedRoute

## safeexpo

### Project Overview

CrowdGuardedRoute is a mobile application that safely guides users on their travel routes. The project collects users' travel speed and illumination data, and recommends safe routes.

### why geolocation ?

The data we want to get from users is the average moving speed and brightness (illuminance) of users at a specific location. We basically judge that if people's moving speed exceeds a certain speed, they are aware of the dangers of the region and their walking has accelerated.
In addition, it is intended to measure the brightness of the area and use it as an evaluation index for safety. In particular, information on the brightness of the travel route collected by people in the evening is required.
In order to utilize the above information, location information (latitude/longitude) along with movement speed and brightness is essential.

### How was the geolocation collected?

Expo acquires information from androio and ios, respectively, through a module called [location](https://docs.expo.dev/versions/latest/sdk/location/). Since location contains privileges that are not provided by default, users must agree to use this app.

### Basic Considerations and Approaches for Route Recommendations

The most open data we generally look at is provided by Google. Starting with Google Maps, Google offers surrounding information (such as business names) and latitude/longitude details for locations. When we think about the common usage patterns for navigating to a destination, it typically involves the following steps:

1. Open Google Maps.
2. Search for the location we want to travel to on Google Maps.
3. Set the searched location as our destination.
4. Set our current location as the starting point.
5. Request a route for the journey.
6. Follow the recommended route as we travel.

The route we take is the optimal path suggested by Google. Google’s ‘directions’ feature recommends travel routes, offering up to five options if public transportation is involved, or three walking routes if not. These routes are composed of the shortest and most optimized paths. We aim to combine these with our own safety evaluations to recommend travel routes.  
Expo (React Native) provides a feature in the MapView component that integrates with Google directions to draw the travel route. However, this integration only allows for one recommended direction, which is a limitation.

Our solution is to directly call the ‘directions’ API to receive a list of possible routes, which we then intend to send to our `backend`. This allows us to offer travel routes that are not only optimized but also take into account our safety assessments, providing a more personalized recommendation for travelers.

```shell
curl https://maps.googleapis.com/maps/api/directions/json?origin=${origin}&destination=${destination}&mode=walking&alternatives=true&key=${apiKey}
```

### SOS

How would you call for help if you had a dangerous situation while on the move?

Basically, when a specific situation occurs, it is possible to predict a situation in which it is difficult for the user to do many activities. Therefore, it is intended to recognize the minimum motion, but to add functions that do not occur by accident.

In order to be able to inform acquaintances of my risks, we need a function to connect each other. We have arranged to link acquaintances to the registration function

Once the sos function is called, a push alarm is provided to the user connected to me, and an extension function that provides location information to a government office (police station) may be considered in the future.
