FROM openjdk:11-jdk

MAINTAINER Mirror jobinbai@foxmail.com

ENV TZ=Asia/Shanghai

ADD target/flora-tools.jar /opt/app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/opt/app.jar"]