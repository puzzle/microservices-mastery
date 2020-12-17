package ch.puzzle.mm.kafka.stock.article.boundary;


import ch.puzzle.mm.kafka.stock.stock.entity.ShopOrderDTO;
import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class ShopOrderDeserializer extends JsonbDeserializer<ShopOrderDTO> {

    public ShopOrderDeserializer() {
        super(ShopOrderDTO.class);
    }
}
