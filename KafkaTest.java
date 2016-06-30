package kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.Test;

import java.util.Collections;
import java.util.Properties;

public class KafkaTest {
	
	@Test
	public void testProduct() throws Exception {
		System.setProperty("java.security.auth.login.config", "F:/project/eclipse44workspace/ChaoxingIM/mqttmanage/src/test/java/kafka/kafka_client_jaas.conf");
		
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.98.38:10092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		
		props.put("security.protocol", "SASL_PLAINTEXT");
		props.put("sasl.mechanism", "PLAIN");
		
		Producer<String, String> producer = new KafkaProducer<>(props);
		for (int i = 0; i < 100; i++) {
			producer.send(new ProducerRecord<>("Test-topic", Integer.toString(i), Integer.toString(i)));
		}
		producer.flush();
		producer.close();
	}
	
	@Test
	public void testConsumer() throws Exception {
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.98.38:10092");
		props.put("group.id", "test");
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		
		props.put("security.protocol", "SASL_PLAINTEXT");
		props.put("sasl.mechanism", "PLAIN");
		
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Collections.singletonList("Test-topic"));
		while (true) {
			System.out.println("aaaaaaaa");
			ConsumerRecords<String, String> records = consumer.poll(Long.MAX_VALUE);
			System.out.println("bbbbbbbb" + records.count());
			for (ConsumerRecord<String, String> record : records) {
				System.out.printf("offset = %d, key = %s, value = %s, partition = %d %n",
				                  record.offset(),
				                  record.key(),
				                  record.value(),
				                  record.partition());
			}
		}
	}
}
