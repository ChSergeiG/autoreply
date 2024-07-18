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
- Or use docker image, as described in `compose/docker-compose.*.yml` file. Don forget
  update `API_ID`, `API_HASH`, `USER_PHONE`, `USER_LOGIN` and `USER_PASSWORD` according to your account
  - To deploy only image without SSL you may use `compose/docker-compose.http.yml` compose
  - To deploy SSL protected image you need to use `compose/docker-compose.https.yml` compose
    - [Here](https://phoenixnap.com/kb/letsencrypt-docker) is additional information about certbot
    - First of all you need to replace all `[domain.name]` in configs with your server domain name. `google.com` for example
    - Then you need to deploy https compose with `nginx.80.conf` mount, but without `nginx.443.conf` mounted in
    - Then - after stack will be ready you need to generate certificates with certbot utility in certbot container using command `docker-compose run --rm certbot certonly --webroot --webroot-path /var/www/certbot/ -d [domain-name]` - don forget replace `[domain-name]` in command
    - And at last redeploy stack with `nginx.443.conf` mounted in.
    - After it you will be able to connect to your application on `https://[domain.name]` url address


