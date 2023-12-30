FROM mongo:4.4.26

ENV MONGO_INITDB_ROOT_USERNAME=root
ENV MONGO_INITDB_ROOT_PASSWORD=root

WORKDIR /docker-entrypoint-initdb.d/

COPY ./container-init-scripts/ .

CMD ["mongod"]
