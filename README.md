## A step into Java
This program marks my first attempt at programming in Java, comming in from a background of C/C++ and Python

## Weather Tracking
This program was written primarially to satisfy a need I had of both diving into Java, and generating lots of time series data. Using Wunderground's API, the program queries the database for weather data at a given date and place, receives the data in the form of a JSON, and saves it to the hard drive as a JSON, stripping unessesary keys first. The program was compiled and tested under OpenJDK-7, but should work under any other Java Platform. 

## Dependancies
* [jackson-core-2.2](https://github.com/FasterXML/jackson)
* [jackson-annotations-2.2](https://github.com/FasterXML/jackson)
* [jackson-databind-2.2](https://github.com/FasterXML/jackson)
* [Apache Commons-CLi 1.2](http://commons.apache.org/proper/commons-cli/download_cli.cgi)
