## CrowdGuardedRoute


### How do you use location information?

factors : (Traffic, Light, Police-Station, Fire-Station etc)

While developing the CrowdGuardedRoute app, I had to create a function to retrieve the factors closest to my coordinates among the numerous data containing latitude and longitude.

If we were to create the above function, how could we create it? The easiest way to retrieve it seems to be to search all factors, compare my position with the positions of the factors, and retrieve the n nearest factors.

However, if you use the method above, if the number of factors is small, the problem of slowdown will occur as the number of factors increases.

So, in order to find a better way, we learned about geohash and used it in this app, and would like to introduce it to you.

<br>

### What is GeoHash?

![GeoHash](https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbmieYZ%2FbtqCRyUP4MI%2Fk27fNcYWYKuyqvWz1F6pUK%2Fimg.jpg, "GeoHash")

[Geohash](https://en.wikipedia.org/wiki/Geohash) can be summarized in the picture above, but to explain it one by one, “geo” means geography, and “hash” is a function that converts random data into a fixed-sized value. I mean.

As shown in the photo above, the Earth is divided into zones of a certain size and each zone is encoded with a unique string. This is a geohash. This geohash is an algorithm that allows for fast and efficient searches.

Geohashes convert location information into a binary string of certain length to represent an exact location. At this time, the shorter the length of the string, the lower the accuracy of the target area, but the longer the string, the higher the accuracy of the target area. Therefore, geohashes can produce strings with varying degrees of accuracy.


<br>

### How was the data collected?

Relevant information was retrieved through the Paris Public Data Portal API or location information was scraped from the map provided on the government office website.

<br>

### How was the data purification process carried out?

First, the imported data was first parsed and made into a mart through [H2 Database](https://www.h2database.com/html/main.html).

Second, the unstructured numerical information required for actual services was refined into Json format and served where numerical analysis was needed.

<br>

### Description of numerical interpretation of data

The numerical analysis is as follows:

> Example) Assuming that the area of Paris is 105.4km², the number of police stations is 120, and each police station is uniformly distributed in a square grid pattern, the length of one side can be calculated as follows:

> $$\sqrt{\frac{105.4}{\sqrt{120}}}$$

If you calculate this equation, the length of one side becomes approximately 3.101km (3.1018km to be exact). This is the length of one side of each square grid, which allows us to estimate the distance to the furthest police station.

> The diagonal length of a square grid (distance to the furthest police station) is the side length multiplied by the square root of 2, so the approximate maximum distance is calculated as:

> $$ 3.101Km \times \sqrt{2} \approx 4.385Km $$

Therefore, under ideal conditions, the furthest possible police station from a given location would be approximately 4.385 km.

The same calculation gives a maximum distance of 3.297 km for 94 fire stations.

<br>

> Article 3 of Seoul Metropolitan Government Regulations on Installation and Management of Road Electrical Equipment

> “Streetlights” refer to lighting facilities installed on roads with a width of 12 m or more, including pedestrian lights, overpass lights, and high mast lights.

> In other related laws, The spacing between security lights installed on the road must be less than 50 meters.

A tentative numerical interpretation based on Seoul's legal basis is as follows:

#### Police Station 4.385km
#### Fire Station 3.297km
#### Traffic lights 1m to 50m
#### Street lights 1m to 50m

<br>


### data source

1. [Traffic-Light source](https://opendata.paris.fr/explore/dataset/signalisation-tricolore/information/?disjunctive.lib_domain&disjunctive.lib_ouvrag&disjunctive.lib_regime&disjunctive.lib_voiedo&disjunctive.lib_region&disjunctive.lib_regi_1&disjunctive.lib_lumi_1&disjunctive.lib_lampef)

2. [Lamp](https://opendata.paris.fr/pages/catalogue/?q=lampadaire&disjunctive.theme&disjunctive.publisher&sort=modified)

3. [Police-Station](https://www.prefecturedepolice.interieur.gouv.fr/)

4. [Fire-Station](https://www.prefecturedepolice.interieur.gouv.fr/)

### How to  Data martization

 First, Grant DB user permission to enable DDL

```yml
  jpa:
    # Change to the dialect of your DB
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create  # Data schema create       
```

 Second, DB Schema create by JPA in your Database

 Third, Collect data from Open Data Resource(ex Public API ..)
 
 Fourth, Clean and store data according to schema
