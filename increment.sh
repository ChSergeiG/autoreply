#!/bin/bash

version=$(cat gradle.properties | grep version= | awk -F '=' '{print $2}')

release=$(echo $version | awk -F '.' '{print $1}')
major=$(echo $version | awk -F '.' '{print $2}')
minor=$(printf $((10#$(echo $version | awk -F '.' '{print $3}') + 1)))

newVersion="$(echo $release).$(echo $major).$(echo $minor)"

gsed -i -e "s/version=.*/version=$newVersion/g" gradle.properties
gsed -i -E "s|(.*)tags: chsergeig/tg-autoreply:(.*?),(.*)|\1tags: chsergeig/tg-autoreply:$newVersion,\3|g" .github/workflows/docker-publish.yml
