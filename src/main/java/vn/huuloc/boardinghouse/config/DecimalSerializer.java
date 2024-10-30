package vn.huuloc.boardinghouse.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import vn.huuloc.boardinghouse.util.CommonUtils;

import java.io.IOException;
import java.math.BigDecimal;

public class DecimalSerializer extends JsonSerializer<BigDecimal> {
    @Override
    public void serialize(BigDecimal bigDecimal, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeNumber(CommonUtils.removeZeroTrail(bigDecimal));
    }
}