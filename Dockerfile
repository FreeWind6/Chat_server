#за основу взята java 8
FROM openjdk:8-jdk-alpine

#Прочие основы
#FROM ubuntu:18.04
#FROM ubuntu:trusty

#Берем jar файл из папку target, задаем ему имя JAR_FILE и копируем его в образ под названием app.jar
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

#Копируем базу данных
ARG DB_FILE=mydb.db
COPY ${DB_FILE} mydb.db

#Команда аналогична java -jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]