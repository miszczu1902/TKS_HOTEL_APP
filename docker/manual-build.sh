#!/usr/bin/bash

cd ../UserService && pwd
mvn clean install -DskipTests=true

cd ../RentService && pwd
mvn clean install -DskipTests=true

cd ../docker && pwd
sudo docker-compose up