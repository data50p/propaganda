FROM openjdk:16-alpine3.13

WORKDIR /app

COPY target/propaganda-0.1.7.1-jexec.jar /app

CMD java -cp /app/propaganda-0.1.7.1-jexec.jar com.femtioprocent.propaganda.appl.ServerAppl
