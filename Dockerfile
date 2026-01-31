FROM gradle:8.14-jdk21

WORKDIR /app

COPY . .

EXPOSE 8081

ENTRYPOINT ["gradle", "bootRun"]
