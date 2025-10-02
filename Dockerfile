FROM docker-registry.dreads.bnb/eclipse-temurin:21-jdk-alpine as builder

COPY s533-trilha-auditoria-servico/target/*.jar s533-trilha-auditoria-servico/application.jar
RUN java -Djarmode=layertools -jar s533-trilha-auditoria-servico/application.jar extract

FROM registry.access.redhat.com/ubi9/openjdk-21:latest
USER 0
 
RUN curl -o /etc/pki/ca-trust/source/anchors/ca-bnb.pem -fsSLk https://repo.dreads.bnb/repository/raw-tools-hosted/bnb/ca/ca-bnb.pem && \
    update-ca-trust
USER 185
 
COPY --from=builder dependencies/ ./
COPY --from=builder snapshot-dependencies/ ./
COPY --from=builder spring-boot-loader/ ./
COPY --from=builder application/ ./

ENTRYPOINT ["java", "-XX:+UseG1GC", "-Xmx500m", "-Xms256m", "-XX:MaxGCPauseMillis=200", "-XX:MetaspaceSize=64M", "-XX:MaxMetaspaceSize=160M", "org.springframework.boot.loader.launch.JarLauncher"]