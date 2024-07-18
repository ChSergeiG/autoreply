[![Publish Docker image](https://github.com/ChSergeiG/autoreply/actions/workflows/docker-publish.yml/badge.svg)](https://github.com/ChSergeiG/autoreply/actions/workflows/docker-publish.yml)

# Based on

https://habr.com/ru/articles/738022/

https://github.com/p-vorobyev/spring-boot-starter-telegram/tree/master

# GitHub

https://github.com/ChSergeiG/autoreply

# Stack

- Spring Boot
- p-vorobyev Spring Boot Telegram Starter
- tomcat web server
- thymeleaf template processor

# How to run

- You may build jar yourself and use prebuilt libs
  from [here](https://github.com/p-vorobyev/spring-boot-starter-telegram/tree/master/libs) with command
  like `java -jar -Djava.library.path=/path/to/library/libtdjni.so ./my-builded.jar`
- Or use docker image, as described in `docker-compose.yml` file. Don forget
  update `API_ID`, `API_HASH`, `USER_PHONE`, `USER_LOGIN` and `USER_PASSWORD` according to your account
