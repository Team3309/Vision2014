Vision2014
==========

Team 3309's Vision code for 2014. This is designed to run on some kind of co-processor (in our case a Raspberry Pi) and runs an HTTP server for the robot to be able to retrieve target info

Setup
-----

Clone repository in IntelliJ IDEA. OpenCV libraries for OS X, Linux are included in this repository (Windows coming soon)

Running
-------

Can be run from within IntelliJ by clicking the Run button.
If exported to a JAR, just use
> java -Djava.library.path=. -jar Vision2014.jar

Server API
----------

This program provides an HTTP server using Jetty that will provide targeting info to a client.

To calibrate, open "http://<ip>:8080/"

Results in JSON format available at "http://<ip>:8080/result"

JSON format
```
{
  time: <time in ms>
  targets: [
    {
      side: <left/right>,
      left: <true/false>,
      right: <true/false>,
      hot: <true/false>,
      distance: <distance in inches>,
      azimuth: <azimuth (horizontal angle) in degrees>
    }, <other targets>
  ]
}
```

The 'targets' field may be just an empty array if no targets are detected
