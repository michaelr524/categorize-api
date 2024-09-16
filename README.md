# Web Page Content Categorization API

## Overview
This project is a Java 8-compatible web service that categorizes web page content. It retrieves and cleans HTML content from multiple URLs, then applies categorization strategies to classify the content based on predefined categories. 

Notes:

- Uses Spring Boot 2.7.18, the last/latest version that supports Java 8 (Spring Boot 3.x requires minimum Java 11)
- Uses Jsoup for HTML parsing - see section below.
- Implements the API from the bonus task instead of the Runner class, providing a more flexible and extensible solution
- The solution focuses on delivering a production ready code (as much as possible within the constraints). Special attention is given to performance. A performance testing framework and two algorithmically simple strategy implementations (as per the bonus task). Thus stressing the measure-then-optimize approach.
- The processing of a single request is parallelized with an unbounded cached thread pool. The workload is CPU bound, and this metric should be used to load balance and scale the service. 
- Categories with their keywords are loaded from categories.json
- Configurable categorization strategy through application.properties
- Integration tests demonstrate the functionality of the API endpoints
- Experimented with Trie-based implementations (such as [Aho-Corasick](https://github.com/robert-bor/aho-corasick)) which have better complexity parameters (O(n + m + z), where n is the length of the text, m is the total length of all keywords, and z is the number of matches), however, they showed worse performance than regex in practice and were subsequently removed
- Deployment considerations: While I don't have extensive experience with Kubernetes to confidently propose a comprehensive design document, I can highlight several general deployment concerns that are worth recognizing and addressing:
  1. Monitoring, centralized logs and metrics collection and analytics: 
     a. Metrics collection (e.g., Prometheus)
     b. Logs collection (e.g., Loki)
     c. Analysis tool (e.g., Grafana)
  2. Scaling should be based on metrics such as CPU utilization of the nodes, response times
  3. Placement: It makes sense to deploy pods that contain a collection of services that serve all the categories. By placing them together, this will allow efficiently matching all categories via parallel requests while keeping it as a unit of scalability that can be instantiated more to handle increased load

1. Naive Categorization Strategy:
    - Time complexity: O(N * M * K), where N is the text length, M is the number of categories, and K is the maximum keyword length.

2. Regex Categorization Strategy:
    - Time complexity: O(N * M), where N is the text length and M is the number of categories.


## Choice of Jsoup for HTML Parsing

Parsing HTML is known as a notoriously hard task because of all the edge cases and quirks. I chose Jsoup as our HTML parsing library because it provides an efficient StreamParser for single-pass processing of HTML content. Jsoup is robust, capable of handling almost any web page on the internet, and comes with built-in functionality to retrieve only the text from HTML while cleaning tags but preserving the text between them.

## Getting Started

### Prerequisites
- Java 8 or higher
- Gradle (if not using the Gradle wrapper)

### Building the Project

2. Build the project using Gradle:
   ```
   ./gradlew build
   ```

   If you want to build without running tests, use:
   ```
   ./gradlew build -x test
   ```

### Running the Application
After building the project, you can run it using the following command:
```
./gradlew bootRun
```

The application will start, and you can access it at `http://localhost:8080`.

### Running Tests
To run the tests, use the following command:
```
./gradlew test
```

## Usage Example

You can use the categorization service by sending a POST request to the `/categorize` endpoint. Here's an example using curl:

```bash
curl -X POST --location "http://localhost:8080/categorize" \
    -H "Content-Type: application/json" \
    -d '{
          "categories" : ["Star Wars", "Basketball"],
          "urls" : ["https://edition.cnn.com/sport", "http://www.starwars.com/"]
        }'
```

This request will categorize the given URLs based on the provided categories.


## Performance Test Results

I conducted performance tests on different categorization strategies using the `CategorizationStrategyPerformanceTest`. Here are the results:

### NaiveCategorizationStrategy Performance

- **Average duration:** 33 ms over 2000 runs (after 1000 warm-up runs)
- **Test parameters:**
  - Number of categories: 20
  - Number of web pages: 20
  - Max keywords per category: 1000
  - Max words per keyword: 6
  - Max words per web page: 3000
- **Total number of category matches:** 800,000.00

### RegexCategorizationStrategy Performance

- **Average duration:** 2 ms over 2000 runs (after 1000 warm-up runs)
- **Test parameters:**
  - Number of categories: 20
  - Number of web pages: 20
  - Max keywords per category: 1000
  - Max words per keyword: 6
  - Max words per web page: 3000
- **Total number of category matches:** 800,000.00

The Regex-based strategy demonstrated significantly better performance compared to the Naive approach in this particular test scenario.

**Note:** Performance may vary depending on the specific hardware and test data used. These results represent a specific test run and may not be indicative of performance in all scenarios.
## Performance Testing

The project includes a robust performance testing framework that allows for easy addition and comparison of different categorization algorithms. The `CategorizationStrategyPerformanceTest` class is designed to:

1. Warm up the JIT compiler with a configurable number of initial runs.
2. Measure the performance of each algorithm over a set number of runs.
3. Provide detailed output on the performance of each algorithm, including average duration and test parameters.

This setup ensures that the performance measurements are as accurate as possible and that new algorithms can be easily added and compared against existing ones.

To run the performance test, use the following command:

```
./gradlew test --tests com.categorize.service.CategorizationStrategyPerformanceTest --rerun-tasks --info
```

This will execute the performance test and provide detailed output on the performance of each categorization strategy.



## Dependencies
- Spring Boot 2.7.18
- Jsoup 1.18.1
- Java 8

## Configuration
The categorization strategy is selected from the `src/main/resources/application.properties` file. Currently, it is set to use the regex strategy:

```
categorization.strategy=regex
```

You can change this value to switch between different categorization strategies.
