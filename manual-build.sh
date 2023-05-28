#!/usr/bin/bash

mvn clean package -DskipTests=true -f UserSevice
mvn clean package -DskipTests=true -f RentService

docker-compose up -f docker