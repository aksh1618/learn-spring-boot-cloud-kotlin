# Notes

Created during the [Master Microservices with Spring Boot and Spring Cloud](https://www.udemy.com/course/microservices-with-spring-boot-and-spring-cloud) Udemy course. The course was in Java but this was done in Kotlin because. Tried [Kofu](https://github.com/spring-projects-experimental/spring-fu) (code in [kofu branch](https://github.com/aksh1618/learn-spring-boot-cloud-kotlin/tree/kofu/currency-exchange-service)) but it didn't support JPA so settled for [Beans](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#kotlin-bean-definition-dsl) & [Router(WebMVC.fn)](https://docs.spring.io/spring/docs/current/spring-framework-reference/languages.html#router-dsl) DSLs and [other sugars](https://docs.spring.io/spring-framework/docs/current/kdoc-api/spring-framework/)

<!-- vim-markdown-toc GFM -->

* [Creating a RESTful resource](#creating-a-restful-resource)
* [Challenges in Microservices Architecture](#challenges-in-microservices-architecture)
* [Spring Cloud Config](#spring-cloud-config)
* [Feign](#feign)
* [Ribbon](#ribbon)
* [Eureka](#eureka)
    * [Summary of Feign,Ribbon,Eureka](#summary-of-feignribboneureka)
* [Zuul](#zuul)
* [Sleuth/Zipkin](#sleuthzipkin)
* [Spring Cloud Bus](#spring-cloud-bus)
* [Hystrix](#hystrix)

<!-- vim-markdown-toc -->

## Creating a RESTful resource

- Define entity, Dao and controller
- Define findAll, findById, create, deleteById
- Define custom exceptions
- Define validations for entity and corresponding handler
- Enable actuator endpoints for monitoring
- Misc. things like HATEOAS, Locales, Filtering etc.
- Decide versioning scheme based on factors such as
    - Caching requirement (URI/param based)
    - Ease of Use & Documenting/ Browser friendly (URI/param based)
    - URI Pollution (Header/mime type based)
- Configure basic auth (?)


## Challenges in Microservices Architecture

- Bounded context (Separaction of concern, figuring out boundaries)
- Configuration management
- Dynamic scaling
- Visibility / Fault detection
- Domino effect / Fault tolerance


## Spring Cloud Config

- Problems Solved:
    - Easier config management for multiple environments
    - Ability to change properties without application restart
- Steps:
    - Server:
        - Create service with spring cloud server dependency
        - Add config git repo uri in server application.properties
        - Commit configs in the added repo as `<client-service-id>-<env>.properties`
        - Available at `<server-host>:<server-port>/<client-service-id>/<env/default>`
    - Client:
        - Rename application.properties to bootstrap.properties
        - Add config server uri in bootstrap.properties
        - Specify env as active profile if required
        - Enable actuator refresh endpoint to be able to reload without service restart
- Notes:
    - Config changes *must* be committed in order to be picked up by the config server

## Feign

- Problems solved:
    - Invoking other services
- Steps:
    - Add `openfeign` dependency
    - Add `@EnableFeignClients(<Feign clients package>)`
    - Define proxy interface using `@FeignClient(name = "service-id", url="url:port")`
    - In the interface copy the controllers of service and adjust return type if required
    - Autowire and call method

## Ribbon

- Problems solved:
    - Client side load balancing
    - DNS
- Steps:
    - Add `netflix-ribbon` dependency
    - Add `@RibbonClient(name = "<destination-service-id>")` (url can now be removed from `FeignClient`)
    - Add nodes in property file:
    ```
    <detination-service-id>.ribbon.listOfServers=http://localhost:777,http://localhost:888
    ```

## Eureka

- Problems solved:
    - Service discovery and registration
- Server:
    - Add eureka server dependency
    - Enable server using `@EnableEurekaServer`
    - Disable register with eureka & fetch registry in properties (?)
- Clients:
    - Add eureka client dependency
    - Enable discovery using `@EnableDiscoveryClient`
    - Add service url in properties
    - Restart to register with Eureka
- Integration with Feign & Ribbon:
    - Service id in feign and ribbon
    - Remove ribbon list of servers property

### Summary of Feign,Ribbon,Eureka

- Feign: Easy to call with URL
- Ribbon: Calling with multiple URLs
- Eureka: Calling with name instead of URL, adding and removing nodes dynamically

## Zuul

- Problems solved:
    - Authentication
    - Rate limit
    - Fault Tolerance (fallback)
    - Service Aggregation (1 call instead of 15 calls)
- Steps:
    - Create server project with zuul dependency
    - Add `@EnableZuulProxy`
    - Filter:
        - Extend `ZuulFilter` and override methods
        - Use URI <zuul-server-url:port>/<destination-service-id>/<path-and-params> (Destination Service as well as Zuul should be registered with Eureka)

## Sleuth/Zipkin

- Problems solved:
    - Identification of muliple internal requests belonging to same request
    - Centralization and visualization for easy debugging
- Steps:
    - Sleuth:
        - Add dependency in service
        - Add `Sampler` bean returning `Sampler.ALWAYS_SAMPLE`
    - Zipkin:
        - Add zipkin-starter and rabbitmq dependencies
        - Start rabbitmq and zipkin servers
        - Start applications

## Spring Cloud Bus

- Problems solved:
    - Intercommunication between services (For ex. to refresh config, we'll have to hit 100 endoints for 100 instances)
- Steps:
    - Add dependency in config server and clients
    - Enable bus-refresh endpoint in clients
    - Send a POST request at any client node's `/actuator/bus-refresh` endpoint

## Hystrix

- Problems solved:
    - Prevention of cascading failures (fault tolerance)
- Steps:
    - Add dependency
    - Add `@EnableHystrix`
    - Fallback:
        - Add `@HystrixCommand(fallback=<fallback-method>)` on controller method

