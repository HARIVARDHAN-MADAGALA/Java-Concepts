package docs.examples;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class KafkaStreamsDLQExample {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-dlq-example");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "broker:9092");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> input = builder.stream("my-topic");

        // Simple try/catch: on processing error, forward to DLQ topic
        input.flatMapValues(value -> {
            try {
                // processing logic that may throw
                String result = value.toUpperCase();
                return java.util.Collections.singletonList(result);
            } catch (Exception e) {
                // on error, produce a record to DLQ topic
                // We return an empty list here and rely on a side-producer (or use a branch/peek)
                return java.util.Collections.emptyList();
            }
        }).to("my-topic-processed", Produced.with(Serdes.String(), Serdes.String()));

        // Alternative: use branch to separate failed records and forward to DLQ

        KafkaStreams streams = new KafkaStreams(builder.build(), props);
        streams.start();
    }
}

