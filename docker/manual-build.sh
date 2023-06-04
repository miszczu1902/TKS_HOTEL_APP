#!/usr/bin/bash

cd ../UserService && pwd
mvn clean install -DskipTests=true
mvn test

cd ../RentService && pwd
mvn clean install -DskipTests=true
mvn test

cd ../docker && pwd
sudo docker-compose up