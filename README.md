<div align="center">
  <h1>
    Java-REST-API-Scrapper
  </h1>
</div>

The [REST API](https://gb.ru/blog/rest-api/) Scrapper collects data from 3 different APIs and parses this data into [json](https://en.wikipedia.org/wiki/JSON) and [csv](https://learn.openwaterfoundation.org/owf-learn-csv/csv-syntax/) format.

<details open>
  <summary><h2>🎯 The task:</h2></summary>
It was necessary to select at least three open, periodically updated REST APIs, for example, open sources of news, articles, weather observations, or APIs of various services such as social networks, online cinemas, etc. From the list of open APIs.

The next step was to develop an application that polls the API of data sources in several streams and saves new records to a CSV or JSON file (at the user's choice).

## 📋 Requirements:
1) When launching the application, it is passed as an argument:

    • The number of allowed simultaneously active threads is n;

    • Time timeout t in seconds, which sets the interval between service polling iterations;

    • A list of names of services that will be surveyed;

    • File format for saving results.

2) A separate stream is created for each data source, in which the API is polled, namely, the execution of an HTTP request to the selected endpoints returning the data. No more than n such threads can run simultaneously. If the number of polled services is more than n, then the threads for the remaining services wait in line until one of the other threads completes updating the data. In this case, the polling thread starts over again after a set time t after completion and also waits for the queue if the number of active polling threads is n.
3) All threads write data to the same file.
4) To create streams, use the util.concurrent library.
5) The code must be covered by unit tests by at least 70%.
6) To connect third-party libraries and frameworks during development, use the [Maven](https://maven.apache.org/what-is-maven.html) or [Gradle](https://gradle.org) project builder.
</details>



## 🧑🏻‍💻 Screenshots of work examples:

|Input string|Output in logger|Output in file|
|---|---|---|
|```2 10 currentsapi,newsapi,openweathermap JSON```|<img width="1361" height="158" alt="Снимок экрана 2025-08-15 в 02 52 53" src="https://github.com/user-attachments/assets/47c38d18-6967-440f-942a-3c865025059e" />|<img width="1379" height="797" alt="Снимок экрана 2025-08-15 в 02 54 57" src="https://github.com/user-attachments/assets/79879ab6-c976-4774-b2dd-6ff1f0d529d2" />|
|```2 10 currentsapi,newsapi,openweathermap CSV```|<img width="1364" height="166" alt="Снимок экрана 2025-08-15 в 02 59 53" src="https://github.com/user-attachments/assets/e19bcd6f-3835-4c9d-95e4-00486c8ee1eb" />|<img width="1319" height="607" alt="Снимок экрана 2025-08-15 в 03 00 37" src="https://github.com/user-attachments/assets/588f9ea7-6b02-4dba-9aea-b39d6b2d76a9" /> P.S. The data is the same here, since I have limited the list of topics for which I receive news (the weather data is different), this can be done in [this file](ApiClient.java), where you insert your API key.
| ```2 10 fooapi,newsapi,pooapi CSV```|<img width="1305" height="197" alt="Снимок экрана 2025-08-15 в 02 39 32" src="https://github.com/user-attachments/assets/affd1336-7632-47b1-93a3-4420e93944fa" />|<img width="1282" height="97" alt="Снимок экрана 2025-08-15 в 02 45 34" src="https://github.com/user-attachments/assets/4758a6a9-24fa-4b76-a849-7273a2c3f5d5" />|
|```2 10 newsapi JSON```|<img width="1254" height="167" alt="Снимок экрана 2025-08-15 в 03 07 54" src="https://github.com/user-attachments/assets/32da3ef8-6a87-4c80-b55d-c05d1d986a6f" />|<img width="1440" height="900" alt="Снимок экрана 2025-08-15 в 03 08 36" src="https://github.com/user-attachments/assets/5fa9b32c-1058-4770-aa20-a40ede8f6285" />|


etc.



<details>
  <summary><h2>List of topic sources used in the project</h2></summary>

  1. About the [REST API](https://yandex.cloud/ru/docs/glossary/rest-api)

2. About the [JSON format](https://tproger.ru/articles/chto-takoe-json-vvedenie)

3. The [Jackson library](https://www.baeldung.com/jackson-object-mapper-tutorial) for working with JSON
   
4. [Apache HttpClient](https://www.baeldung.com/apache-httpclient-cookbook) [HTTP client](https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5), which can be used to generate requests to [endpoints](https://habr.com/ru/companies/umbrellaitcom/articles/423591/).
</details>
