FROM --platform=linux/amd64 bellsoft/liberica-openjre-alpine:21 as jar_build

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
RUN ./config && make > /dev/null

FROM --platform=linux/amd64 bellsoft/liberica-openjdk-debian:21 as ld_build

COPY ./prepare_ld_build.sh /
RUN sh /prepare_ld_build.sh

RUN apt update
RUN apt install -y make git zlib1g-dev libssl-dev gperf php-cli cmake clang-18 libc++-18-dev libc++abi-18-dev
RUN git clone https://github.com/tdlib/td.git /td
RUN git clone --branch "1.13.0" --depth 1 https://github.com/p-vorobyev/spring-boot-starter-telegram.git /sbst

RUN rm -rf /td/build && mkdir /td/build
WORKDIR /td/build

RUN CXXFLAGS="-stdlib=libc++" CC=/usr/bin/clang-18 CXX=/usr/bin/clang++-18 cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td -DTD_ENABLE_JNI=ON ..
RUN cmake --build . --target install

RUN rm /td/example/java/CMakeLists.txt && cp /sbst/libs/build/CMakeLists.txt /td/example/java && cp -R /sbst/libs/build/dev /td/example/java
RUN rm -rf /td/example/java/build && mkdir /td/example/java/build
WORKDIR /td/example/java/build

RUN CXXFLAGS="-stdlib=libc++" CC=/usr/bin/clang-18 CXX=/usr/bin/clang++-18 cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../../../tdlib -DTd_DIR:PATH=$(readlink -e ../td/lib/cmake/Td) ..
RUN cmake --build . --target install

FROM --platform=linux/amd64 bellsoft/liberica-openjre-debian:21

COPY ./prepare_ld_build.sh /
RUN sh /prepare_ld_build.sh
RUN rm /prepare_ld_build.sh

RUN apt update
RUN apt install -y libc++-18-dev musl-dev
RUN apt autoremove

RUN ln -s "/usr/lib/x86_64-linux-musl/libc.so" "/lib/libc.musl-x86_64.so.1"

WORKDIR /app
COPY --from=jar_build "/build/client/build/libs/*.jar" "/app/app.jar"
COPY --from=ssl_build "/openssl-1.1.1o/libssl.so.1.1" "/app/libs/libssl.so.1.1"
COPY --from=ssl_build "/openssl-1.1.1o/libcrypto.so.1.1" "/app/libs/libcrypto.so.1.1"
COPY --from=ld_build "/td/tdlib/bin/libtdjni.so" "/app/libs/libtdjni.so"

ENV LD_LIBRARY_PATH=/app/libs/

EXPOSE 5005
EXPOSE 8080

ENTRYPOINT exec java -Dspring.profiles.active=prod -Djava.library.path=/app/libs -jar /app/app.jar
