package acme.media.kafkainrestout;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.SendTo;


@SpringBootApplication
public class ItemOfferProcessor {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        SpringApplication.run(ItemOfferProcessor.class, args);
    }

    @EnableBinding(ItemOfferBinding.class)
    public static class KStreamToTableJoinApplication {

        @StreamListener
        @SendTo("output")
        public @Output("output") KStream<String, String> process(

                @Input("aggregated-offers") KTable<String, String> aggregatedOffers,

                @Input("items") KTable<String, String> items

                ) {


            return items.leftJoin(aggregatedOffers, (value1, value2) -> value1+value2)

                    .toStream()

                    .peek((key, value) -> printForDebug(key, value, 7));

        }
    }

    interface ItemOfferBinding {

        @Input("aggregated-offers")
        KTable<?, ?> offers();

        @Input("items")
        KTable<?, ?> items();

        @Output("output")
        KStream<?, ?> output();

    }

    private static void printForDebug(String key, String value, int location) {
        System.out.format("[%d] key: %s val: %s%n", location, key, value);
    }
}
