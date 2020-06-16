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

#Сборка проекта

#В папке проекта выполняем команду
#dicker build .
#либо эту команду с заданием имени
#docker build -t freewind6/freewind-chat-server . (даем имя)

#Затем смотрим все наши образы и находим IMAGE ID нужного
#docker images

#Запускаем с перенаправлением портов
#docker run -p 8189:8189 3f5c06630610 (3f5c06630610 - IMAGE ID)
#или по имени
#docker run -p 8189:8189 freewind6/freewind-chat-server

#Загрузаем на сервер
#docker push freewind6/freewind-chat-server (сделать docker login если проблемы авторизации)

#После того как сделали push можно запусть на любой машине
#docker run -p 8189:8189 freewind6/freewind-chat-server

#Останавливаем
#docker stop 3f5c06630610

#Удаляем, но сначала нужно удалить все контеинеры
#docker rmi 3f5c06630610

#Смотрим все контейнеры
#docker ps
#docker ps -a

#Удаляем нужный
#docker rm [Container-id]

#Подсказки:
#https://habr.com/ru/company/flant/blog/336654/
#https://java-master.com/docker-%D0%BE%D1%81%D0%BD%D0%BE%D0%B2%D1%8B/

#Подключиться к контейнеру:
#Сначала смотрим NAMES нужно контейнера
#docker container ls

#Затем подкляемся к нему
#docker container attach [NAMES]