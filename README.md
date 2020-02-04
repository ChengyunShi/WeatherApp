# WeatherApp
This is an Android Application which can forecast the weather all over the world.

The server is written by Node.js and uses DarkSky API to retrieve weather forecast data. Google API is used to locate the place 
and Google search engine is used to obtain pictures. The server will process the response data and send a json data to the mobile app.
The server is deployed on the AWS beanstalk.

The mobile app has a splash screen and the home screen shows the weather of current location (which is acquired by the ip). 
The user can click on the first card to see the detailed weather information and the picture of the city.

There is a searchview the user can use to search weather of any place on the earth. It has an autocomplete function which used 
the Place Autocomplete service provided by Google. Users can add the search result in your collections, so that it's convenient 
to check weather on the home screen.
