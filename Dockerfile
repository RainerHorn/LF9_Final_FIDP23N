FROM openjdk:8-jre-alpine

WORKDIR /app

COPY ./target/todo-todo-jar-with-dependencies.jar /app/todo.jar
COPY ./*.db /app/

EXPOSE 8080

CMD ["java", "-jar", "todo.jar"]
