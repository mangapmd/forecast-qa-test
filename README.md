## Intro 
Imagine you have a commandline app to show tomorrow's forecast using public API: https://www.metaweather.com/api/

Sample output:
```
$ forecast dubai

Tomorrow (2019/05/01) in Dubai:
Clear
Temp: 26.5 °C
Wind: 7.6 mph
Humidity: 61%
```

## Task - Updated with comments
* How will you test the app? Write 1-2 automated tests to prove the correct work of application.  
__MP__  : AppTest.java has 2 tests.  
First test, to verfiy the city name prompt message is displayed when no argument is sent.  
Second test, to verify weather information displayed when city parameter is sent.  
Tests Can be run as JUnit test within IDE, or via mvn test command

* Ideally, tests should not touch the real service and work without the Internet.  
__MP__ : 
1) Used WireMock to create stubs
2) Updated the App.main method to point to baseUrl as localhost to get response from the Wiremock stub, instead of the real service  
  

* Bonus task. Create CI pipeline with GitHub Actions or any alternative.  
__MP__ :  
Using GitHub Actions:
1) Created from the template workflow (Java CI with Maven)
2) Tests will be run on every push to the master branch
