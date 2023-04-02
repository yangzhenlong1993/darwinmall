# Darwin Mall

![](https://img.shields.io/badge/building-passing-green.svg)![GitHub](https://img.shields.io/badge/license-MIT-yellow.svg)![jdk](https://img.shields.io/static/v1?label=oraclejdk&message=8&color=blue)

The Darwin Mall project is committed to building a complete large-scale distributed architecture e-commerce platform,
which is realized by the current popular technology and written separately from the front and back ends.

## Project Description

The Darwin Mall project is a set of e-commerce projects, including the front-end commercial system and the back-end
management system, based on SpringCloud, SpringCloud Alibaba, and MyBatis Plus. The front-end comm system includes: user
login, registration, product search, product details, shopping cart, order, and other modules. The background management
system includes seven modules: system management, commodity system, preferential marketing, inventory system, order
system, user system, and content management.

## Project Demonstration (Cause we used the template from a Chinese website, so some parts of page include Chinese characteristics)

### Main page of Client
![Screenshot 2023-04-02 161149](https://user-images.githubusercontent.com/54578367/229337831-8ef6c719-bc32-4b89-8f9f-a494b3eddc24.png)



## Microservices Structure

```
darwinmall
├── darwinmall-common -- utils and common modules
├── renren-generator -- renren open source project code generator
├── darwinmall-auth-server -- login through social platform
├── darwinmall-cart -- cart microservice
├── darwinmall-coupon -- coupon microservice
├── darwinmall-gateway -- gateway configuration
├── darwinmall-order -- order microservice
├── darwinmall-product -- product microservice
├── darwinmall-search -- search microservice
├── darwinmall-third-party -- third party service (object storage service, email verification code)
├── darwinmall-ware -- warehouse microservice
└── darwinmall-member -- membership microservice
```

## technology stack

### back end

|     Technology     |            Description            |                Official Website                 |
|:------------------:|:---------------------------------:|:-----------------------------------------------:|
|     SpringBoot     |      Container+MVC framework      |     https://spring.io/projects/spring-boot      |
|    SpringCloud     |           microservice            |     https://spring.io/projects/spring-cloud     |
| SpringCloudAlibaba |            components             | https://spring.io/projects/spring-cloud-alibaba |
|    MyBatis-Plus    |           ORM framework           |             https://mp.baomidou.com             |
|  renren-generator  | renren open source code generator |   https://gitee.com/renrenio/renren-generator   |
|   Elasticsearch    |           search engine           |    https://github.com/elastic/elasticsearch     |
|      RabbitMQ      |           message queue           |            https://www.rabbitmq.com             |
|   Springsession    |     distributed system cache      |    https://projects.spring.io/spring-session    |
|      Redisson      |      distributed system lock      |      https://github.com/redisson/redisson       |
|       Docker       |   application container engine    |             https://www.docker.com              |
|        OSS         |      object storage service       |  https://github.com/aliyun/aliyun-oss-java-sdk  |

### front end

| technology |      description       |     official website      |
|:----------:|:----------------------:|:-------------------------:|
|    Vue     |  front end framework   |     https://vuejs.org     |
|  Element   | front end UI framework | https://element.eleme.io  |
| thymeleaf  |    template engine     | https://www.thymeleaf.org |
|  node.js   |     client script      |   https://nodejs.org/en   |

## architect diagram

### system architect diagram
![20201125202655970](https://user-images.githubusercontent.com/54578367/229336798-db77459e-418d-4635-b434-b7dc9561ca69.png)

# service architect diagram
##![20201125202611963](https://user-images.githubusercontent.com/54578367/229336811-d0e6d8e7-bd34-4bb5-b6b9-e36fed9a5d03.png)

## development environment set up

### development tools

|     tool      |         description          |                official website                 |
|:-------------:|:----------------------------:|:-----------------------------------------------:|
|     IDEA      |   develop JAVA application   |     https://www.jetbrains.com/idea/download     |
| RedisDesktop  | redis client connection tool |        https://redisdesktop.com/download        |
|  SwitchHosts  |    local host management     |       https://oldj.github.io/SwitchHosts        |
|    X-shell    | Linux remote connection tool | http://www.netsarang.com/download/software.html |
|    Navicat    |  data base connection tool   |       http://www.formysql.com/xiazai.html       |
| PowerDesigner |     database design tool     |             http://powerdesigner.de             |
|    Postman    |       API testing tool       |             https://www.postman.com             |
|    Jmeter     | performance pressure testing |            https://jmeter.apache.org            |
|    Typora     |       Markdown editor        |                https://typora.io                |

### development environment

|     tool      | version |                               download link                                |
|:-------------:|:-------:|:--------------------------------------------------------------------------:|
|      JDK      |   1.8   | https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html |
|     Mysql     |   5.7   |                           https://www.mysql.com                            |
|     Redis     |  Redis  |                         https://redis.io/download                          |
| Elasticsearch |  7.6.2  |                      https://www.elastic.co/downloads                      |
|    Kibana     |  7.6.2  |                      https://www.elastic.co/cn/kibana                      |
|   RabbitMQ    |  3.8.5  |                   http://www.rabbitmq.com/download.html                    |
|     Nginx     |  1.1.6  |                     http://nginx.org/en/download.html                      |
