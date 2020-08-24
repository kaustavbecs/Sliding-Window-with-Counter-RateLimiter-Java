## **Features**
This SpringBoot service serves as an API rate limiter for your other Microservices.

## **Configurations**

Change the following parameters in application.properties as per your need:

1. **request.time.limit.inseconds**: Overall time window (in seconds) to apply rate limit.
2. **request.count.limit**: Overall request count allowed in the time window.
3. **time.window.count**: Number of smaller time window buckets that the program will use. This parameter will determine the granularity of the checks. Higher value will yield more accurate results but with higher memory footprint.
4. **header.userid.attribute.name**: UserID attribute name in the HTTP header. This application uses this attribute name to uniquely identify a user and then applies rate limit on incoming requests based on the configured parameters.

## **Deployment steps**

To build and run the application you need to have Maven and JDK8+ OR Docker.
1. To build it, run the command: **mvn clean package** (It may take up to two minutes for all the test cases to run)
2. Go to the target directory and run the command: **java -jar ratelimiter-1.0.0.jar**
3. Instead of running the jar directly, you may also run using Docker: 
  a. **docker build -t myratelimiter .**
  b. **docker run -p 8080:8080 -t myratelimiter** 
4. Send a POST request to http://localhost:8080/isallowed with the request header containing "userid" key and corresponding value. The API should return true.

**Note**: 
If you are configuring auto-scaling for this Rate limiter service, the load balancer should make sure that requests for the same user should always hit the same instance
