# Chat Server
![Java CI with Maven](https://github.com/FreeWind6/Chat_server/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
[![GitHub release (latest by date)](https://img.shields.io/github/v/release/freewind6/Chat_server)](https://github.com/FreeWind6/Chat_server/releases)
[![license](https://img.shields.io/github/license/freeWind6/Chat_server)](https://github.com/FreeWind6/Chat_server/blob/master/LICENSE)
[![Docker Pulls](https://img.shields.io/docker/pulls/freewind6/freewind-chat-server)](https://hub.docker.com/r/freewind6/freewind-chat-server)

Сервер чата подключаемого по адресу localhost:8189

Для запуска приложения нужно перекинуть в ту же папку что и jar, файл базы данных. Пример базы находится в корне проекта "mydb.db".

Компиляция:
    
    mvn clean install
    
Запуск:

    java -jar ServerChar-1.0-SNAPSHOT-jar-with-dependencies.jar
    
Или запуск через Docker:

    docker run -p 8189:8189 freewind6/freewind-chat-server
