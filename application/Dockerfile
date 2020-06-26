FROM openjdk:11-jre-slim

RUN \
      addgroup --system configableauth \
      && adduser --system --disabled-login --disabled-password --home /var/lib/configableauth --shell /sbin/nologin --ingroup configableauth configableauth

COPY ./target/scala-2.12/application-assembly.jar /var/lib/configableauth/

USER configableauth
WORKDIR /var/lib/configableauth

ENTRYPOINT ["java", "-jar", "application-assembly.jar"]