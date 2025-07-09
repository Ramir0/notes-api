# Getting Started

### Prerequisites

- [Java 21+](https://adoptium.net/)
- [Gradle](https://gradle.org/)
- [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/)

---

### Running MongoDB with Docker Compose

To start a local MongoDB instance for development and testing, use the provided `docker-compose.yml`:

```sh
docker compose up -d
```

This will start MongoDB on port `27017` with a replica set (`rs0`).
You can stop it with
```sh
docker compose down
```

---

### Running the Application

Start the Spring Boot application (ensure MongoDB is running):

```sh
gradle bootRun
```
or
```sh
gradle build
java -jar build/libs/notes-1.0.jar
```

Running Tests
To run all tests:
```sh
gradle test
```


### Reference Documentation
For further reference, please consider the following sections:

* [Official Gradle documentation](https://docs.gradle.org)
* [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/3.5.3/gradle-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.5.3/gradle-plugin/packaging-oci-image.html)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/3.5.3/reference/using/devtools.html)
* [Spring Reactive Web](https://docs.spring.io/spring-boot/3.5.3/reference/web/reactive.html)
* [Spring Data Reactive MongoDB](https://docs.spring.io/spring-boot/3.5.3/reference/data/nosql.html#data.nosql.mongodb)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a Reactive RESTful Web Service](https://spring.io/guides/gs/reactive-rest-service/)
* [Accessing Data with MongoDB](https://spring.io/guides/gs/accessing-data-mongodb/)

### Additional Links
These additional references should also help you:

* [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

