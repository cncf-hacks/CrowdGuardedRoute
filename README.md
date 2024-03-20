# CrowdGuardedRoute

## Proposal

> Google Maps is the most popular navigation service. By tuning it, we look for ways to get to different places.  
> On the one hand, we know of some apps that parents install to protect their children. (iSharing, for example) They are effective apps that show the location of children or their families, but they lack the ability to find the right routes. They are also paid services.
>
> We want to combine the advantages of these two kinds of apps to create a new service. It is an app that finds safe routes. To solve this problem, we have some ideas.
>
> First, we will develop an app that collects people's routes and their speed. It is one of the most important indicators for predicting safe routes. We will measure safety by measuring the average speed of travel over time.  
> We also know various open sites that collect crime status. (ex: crimemapping.com ) It can be used as one of the indicators of travel routes. In addition, we will check streetlights, cctv (earthcam.com ), fire stations, and police stations on the route and use it as a safe route.  
> Besides this, we are considering various indicators and have the best algorithms for scoring them. We can also check the safety of the route through interactions with people who are using the app. We can collect and score these indicators and provide a route at a specific time through time series analysis.
>
> And my route will be displayed on my mobile device as a family member or acquaintance, and even if I do not start the app function, I will support to receive an alarm in case of an abnormal situation.  
> By creating these apps, we are confident that support is possible to provide a safe route.

## Project Overview

CrowdGuardedRoute recommends safe travel routes to users. It serves via mobile app. The project evaluates safety through machine learning models based on public data and user feedback, and provides optimal routes.

## How to create a safe travel route

Let's check the route we're moving. If you press the recommended route on Google Maps, you can see that numerous points are connected. These points can be expressed as values of specific latitude and longitude and can be converted into geohash values to define the id for geographic information.

We want to evaluate the safety of each coordinate and recommend a safe route through the total safety index of the travel route where the coordinates are gathered.

The geohash-based safety evaluation model has many advantages, and the most important of them is the structure that is easy to expand.
Safety is provided based on machine learning, and we collected various factors to evaluate it. This includes the location of police stations, fire stations and traffic light, brightness(illuminance) and moving speed information. By learning these factor, the safety score can be calculated, and if a new factor is added or the weight for each factor is adjusted, the result can be naturally mapped to geohash and provided to the user.

Additionally, there are factors that can be obtained depending on the region. These include things like the locations of CCTVs in Korea (which are not provided in France). We are considering a configuration that creates a sub-machine learning model with these region-specific factors and concatenates it with the main model.

On the one hand, it is easy for users to get feedback on their experiences using the route and add it as a factor in our safety evaluation.

If this structure is maintained, we can expect not only to provide safe routes through mobile apps, but also to dataize our safety evaluation results, reproduce them into public APIs, and provide them in a more scalable way.

## Structure

We have three types of sub-projects in the project. The description of each project is described in the sub-projects, where we introduce how each project is structured for a single goal.

```bash
root
|- safeexpo  # Cross-platform app using reactnative. Collecting user experience.
|- backend   # Server based on spring boot. Collecting apps and public data, organizing data.
|- saferoute # Python machine learning based server. I recommend safe routes with continuous learning.
```

## Feature Introduction

The project provides the following functions.

- **Recommends a safe route from your current location to where you want to go** (/safeexpo)
- **SOS function to alert dangerous situations while on the move** (/safeexpo)
- Additional collection of public data. (/backend)
- Data structure that makes it easy to add data for safety assessment (/backend)
- Push (/backend) to notify connected users (family) when SOS notifications come
- Machine learning-based safety assessment (/saferoute)

## Installation and Use

The app was developed as a cross-platform, but so far, the test was conducted only with the Android version.

If you run the app after installation, the map will be displayed on the screen.
You can set the destination by selecting a specific location on the map or entering the search conditions at the top
When the destination is determined, click the work button. At this time, three walking routes are recommended.

The recommended path has three colors, and the ranking for the color is displayed at the top left.

## How to configure the development environment

### safeexpo

The above project is constructed based on expo. Since many configurations of the service are executed through Google API, they must be carried out in an environment where the Internet is enabled.

#### Preparation in advance

- Latest nodejs
- npm or yarn
- javascript IDE(vs code)
- Android device with expo go installed

#### Configuring Environment Variables (.env.local)

| environmental variables | value (example)                | use                   |
| ----------------------- | ------------------------------ | --------------------- |
| EXPO_PUBLIC_API_KEY     | Google API Key                 | Google Map API Key    |
| GOOGLE_SERVICES_JSON    | ./google/google-services.json' | Google Place API Key  |
| BACKEND_DOMAIN          | http://192.168.223.251:8080    | Backend Server domain |

#### Running

Expo runs with the following command, and if you scan the qr code with the Android phone's expo app after execution, the development environment is set.

```javascript
yarn install
yarn start
```

### backend

#### Preparation in advance

- jdk 17
- spring boot IDE(intellij)
- redis 3.2.0 version or later (requires geospatial functionality)

#### onfiguring Environment Variables(src/main/resources/application-local.yml)

| environmental variables | value (example)            | use                    |
| ----------------------- | -------------------------- | ---------------------- |
| spring.data.redis.host  | localhost                  | geo-based data storage |
| spring.data.redis.port  | 6379                       | geo-based data storage |
| spring.data.ml.domain   | ${ML_DOMAIN}               | exporoute domain       |


The environment variable keys are as follows:
> key : ML_DOMAIN 
>
> value : Declare the machine learning domain as an environment variable and read it.

#### Running

The project is gradle roll set and starts the spring boot app after performing the build on the side.

### exporoute

### preparation in advance

- python 3.9 or higher
- python IDE
- tensorflow 2.15.0
- flask 3.0.2

### Running

Exporoute runs with follwing command, If a Serverport is not explicitly specified, the server defaults to operating on port 8080.

```python
python main.py {SevicePort}
```

## Restrictions

This repository has the purpose of submitting to cloudnativehacks 2024, and for this purpose, we conducted the underlying data based on public data in Paris.
It is recommended if you conduct a route search in Paris to confirm the evaluation, but other regions are not yet ready, and to solve this problem, data that can be collected in each region must be registered directly in the 'backend'.
