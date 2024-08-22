#!/bin/bash

apt update
apt install -y gpg

rm /etc/apt/sources.list.d/archive_uri-http_apt_llvm_org*.list | true
rm /etc/apt/trusted.gpg.d/apt.llvm.org.asc | true
curl https://apt.llvm.org/llvm-snapshot.gpg.key -o apt.llvm.org.asc
gpg --output apt.llvm.org.gpg --dearmor apt.llvm.org.asc
mkdir -p /etc/apt/keyrings/
mv apt.llvm.org.gpg /etc/apt/keyrings/

mkdir -p /etc/apt/sources.list.d/
echo "Types: deb" > /etc/apt/sources.list.d/apt.llvm.org.sources
echo "URIs: https://apt.llvm.org/bullseye/" >> /etc/apt/sources.list.d/apt.llvm.org.sources
echo "Suites: llvm-toolchain-bullseye llvm-toolchain-bullseye-18" >> /etc/apt/sources.list.d/apt.llvm.org.sources
echo "Components: main" >> /etc/apt/sources.list.d/apt.llvm.org.sources
echo "Signed-By: /etc/apt/keyrings/apt.llvm.org.gpg" >> /etc/apt/sources.list.d/apt.llvm.org.sources

