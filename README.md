# ReplyKafka

start kafka :
```
docker-compose -f zk-single-kafka-single.yml up -d
docker exec -it zookeeper-kafka_kafka1_1 sh
cd /opt/bitnami/kafka/bin/
kafka-topics.sh --create --zookeeper zoo1:2181 --topic test-reply-topic-req --partitions 3 --replication-factor 1 --config delete.retention.ms=86400000
kafka-topics.sh --create --zookeeper zoo1:2181 --topic test-reply-topic-resp --partitions 3 --replication-factor 1 --config delete.retention.ms=86400000
kafka-topics.sh --list  --zookeeper zoo1:2181
```

Spring boot Application :
* 2 Profile : auto, manaual

* JVM ARGS ReplyKafkaReq
```
-Dserver.port=9000 -Dspring.profiles.active=manaual -DSERVER_PORT=localhost:9093 -DCLIENT_ID=reply-kafka-req -DGROUPID_TOPIC_RESP=reply-kafka-req -DTOPIC_REQ=test-reply-topic-req -DTOPIC_RESP=test-reply-topic-resp
```
* JVM ARGS ReplyKafkaResp