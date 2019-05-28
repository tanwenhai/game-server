FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /app
ENTRYPOINT [ "sh", "-c", "/app.jar" ]