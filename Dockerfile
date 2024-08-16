FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:21 as jar_build

ARG GH_LOGIN
ENV GH_LOGIN ${GH_LOGIN}
ARG GH_TOKEN
ENV GH_TOKEN ${GH_TOKEN}

WORKDIR /build
COPY . .
RUN ./gradlew bootJar

FROM --platform=linux/amd64 alpine as ssl_build

RUN wget https://www.openssl.org/source/openssl-1.1.1o.tar.gz && \
    tar -zxvf openssl-1.1.1o.tar.gz > /dev/null

RUN apk add perl make gcc libgcc g++ zlib zlib-dev ldc linux-headers

WORKDIR /openssl-1.1.1o
RUN ./config && make > /dev/null

FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:21 as ld_build

ENV CXXFLAGS -stdlib=libc++
ENV CC /usr/bin/clang-18
ENV CXX /usr/bin/clang++-18

RUN apk add make git zlib-dev gperf php-cli cmake compiler-rt openssl openssl-dev
RUN apk add clang18 clang18-headers clang18-extra-tools clang18-libclang clang18-libs
RUN apk add libc++-dev libatomic linux-headers

RUN mkdir -p /usr/lib/llvm18/lib/clang/18/lib/linux/
RUN cp -r /usr/lib/llvm17/lib/clang/17/lib/linux/libclang_rt.ubsan_standalone-x86_64.a /usr/lib/llvm18/lib/clang/18/lib/linux/libclang_rt.ubsan_standalone-x86_64.a
RUN cp -r /usr/lib/llvm17/lib/clang/17/lib/linux/libclang_rt.ubsan_standalone_cxx-x86_64.a /usr/lib/llvm18/lib/clang/18/lib/linux/libclang_rt.ubsan_standalone_cxx-x86_64.a

RUN git clone https://github.com/tdlib/td.git && cd td && git checkout 8d08b34e22a08e58db8341839c4e18ee06c516c5
RUN git clone --branch "1.13.0" --depth 1 https://github.com/p-vorobyev/spring-boot-starter-telegram.git /sbst

RUN rm -rf /td/build && mkdir /td/build
WORKDIR /td/build
RUN cmake -DCMAKE_C_STANDARD_LIBRARIES="-latomic" -DCMAKE_BUILD_TYPE=Release -DCMAKE_TRY_COMPILE_TARGET_TYPE=STATIC_LIBRARY -DCMAKE_INSTALL_PREFIX:PATH=../example/java/td -DTD_ENABLE_JNI=ON ..
RUN cmake --build . --target install

WORKDIR /td
RUN rm example/java/CMakeLists.txt && cp /sbst/libs/build/CMakeLists.txt example/java
RUN cp -R /sbst/libs/build/dev example/java

RUN rm -rf /td/example/java/build && mkdir /td/example/java/build
WORKDIR /td/example/java/build
RUN cmake -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX:PATH=../../../tdlib -DTd_DIR:PATH=$(readlink -f ../td/lib/cmake/Td) ..
RUN cmake --build . --target install

FROM --platform=linux/amd64 bellsoft/liberica-openjdk-alpine:21

WORKDIR /app
COPY --from=jar_build /build/client/build/libs/*.jar /app/app.jar
COPY --from=ssl_build /openssl-1.1.1o/libssl.so.1.1 ./libs/libssl.so.1.1
COPY --from=ssl_build /openssl-1.1.1o/libcrypto.so.1.1 ./libs/libcrypto.so.1.1
COPY --from=ld_build /td/example/java/build/libtdjni.so ./libs/libtdjni.so

ENV LD_LIBRARY_PATH=/app/libs/

EXPOSE 5005
EXPOSE 8080

ENTRYPOINT exec java -Djava.library.path=/app/libs -jar /app/app.jar
