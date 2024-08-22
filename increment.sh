#!/bin/bash

version=$(cat gradle.properties | grep version= | awk -F '=' '{print $2}')
td_version=$(cat gradle.properties | grep tdVersion= | awk -F '=' '{print $2}')
starter_version=$(cat gradle.properties | grep starterVersion= | awk -F '=' '{print $2}')

release=$(echo $version | awk -F '.' '{print $1}')
major=$(echo $version | awk -F '.' '{print $2}')
minor=$(printf $((10#$(echo $version | awk -F '.' '{print $3}') + 1)))

newVersion="$(echo $release).$(echo $major).$(echo $minor)"

gsed -i -e "s/version=.*/version=$newVersion/g" gradle.properties
gsed -i -e "s/^version:.*$/version: \"$newVersion\"/g" client/src/main/resources/application.yml
gsed -i -E "s|(.*)tags: chsergeig/tg-autoreply:(.*?),(.*)|\1tags: chsergeig/tg-autoreply:$newVersion-$td_version,\3|g" .github/workflows/docker-publish.yml

gsed -i -E "s|(^RUN.+? --branch \").+(\".+)$|\1$starter_version\2|g" ./DockerFile
