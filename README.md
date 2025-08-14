<div align="center">
  <h1>
    Java-REST-API-Scrapper
  </h1>
</div>

The [REST API](https://gb.ru/blog/rest-api/) Scrapper collects data from 3 different APIs and parses this data into [json](https://en.wikipedia.org/wiki/JSON) and [csv](https://learn.openwaterfoundation.org/owf-learn-csv/csv-syntax/) format.

## 🎯 The task:
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

## 🧑🏻‍💻 Screenshots of work examples:


<details>
  <summary><h2>List of topic sources used in the project</h2></summary>

  1. About the [REST API](https://yandex.cloud/ru/docs/glossary/rest-api)

2. About the [JSON format](https://tproger.ru/articles/chto-takoe-json-vvedenie)

3. The [Jackson library](https://www.baeldung.com/jackson-object-mapper-tutorial) for working with JSON
   
4. [Apache HttpClient](https://www.baeldung.com/apache-httpclient-cookbook) [HTTP client](https://mvnrepository.com/artifact/org.apache.httpcomponents.client5/httpclient5), which can be used to generate requests to [endpoints](https://habr.com/ru/companies/umbrellaitcom/articles/423591/).
</details>
