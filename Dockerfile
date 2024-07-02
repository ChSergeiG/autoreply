FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:21 as build

ARG GH_LOGIN
ENV GH_LOGIN ${GH_LOGIN}
ARG GH_TOKEN
ENV GH_TOKEN ${GH_TOKEN}

WORKDIR /build
COPY . .
RUN ./gradlew bootJar

FROM --platform=linux/amd64 alpine as ssl_build

RUN wget https://www.openssl.org/source/openssl-1.1.1o.tar.gz
RUN tar -zxvf openssl-1.1.1o.tar.gz > /dev/null

RUN apk add perl make gcc libgcc g++ zlib zlib-dev ldc linux-headers

WORKDIR /openssl-1.1.1o
RUN ./config
RUN make > /dev/null
RUN find / -name libssl.so.1.1

FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:21

WORKDIR /app
COPY --from=build /build/client/build/libs/*.jar /app/app.jar

RUN wget https://github.com/p-vorobyev/spring-boot-starter-telegram/raw/master/libs/linux_x64/libtdjni.so
RUN mkdir ./libs
RUN mv ./libtdjni.so ./libs/
COPY --from=ssl_build /openssl-1.1.1o/libssl.so.1.1 ./libs/libssl.so.1.1
COPY --from=ssl_build /openssl-1.1.1o/libcrypto.so.1.1 ./libs/libcrypto.so.1.1

ENV LD_LIBRARY_PATH=/app/libs/

EXPOSE 5005
EXPOSE 8080

ENTRYPOINT exec java -Djava.library.path=/app/libs -jar /app/app.jar
