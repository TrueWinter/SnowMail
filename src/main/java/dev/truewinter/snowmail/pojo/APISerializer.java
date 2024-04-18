package dev.truewinter.snowmail.pojo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

public class APISerializer extends StdSerializer<Response> {
    protected APISerializer() {
        this(null);
    }

    protected APISerializer(Class<Response> t) {
        super(t);
    }

    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeRaw(new ObjectMapper()
                .writerWithView(response.getView())
                .writeValueAsString(response.getObject())
        );
    }
}
