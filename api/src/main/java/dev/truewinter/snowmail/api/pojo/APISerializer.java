package dev.truewinter.snowmail.api.pojo;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import dev.truewinter.snowmail.api.pojo.objects.Form;
import org.jetbrains.annotations.ApiStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @hidden
 */
public class APISerializer extends StdSerializer<Response> {
    protected APISerializer() {
        this(null);
    }

    protected APISerializer(Class<Response> t) {
        super(t);
    }

    @Override
    public void serialize(Response response, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String json = new ObjectMapper()
                .writerWithView(response.getView())
                .writeValueAsString(response.getObject());

        if (response.getView().equals(Views.Public.class) && response.getObject() instanceof Form) {
            List<String> keys = new ArrayList<>();
            Matcher m = Pattern.compile("%metadata:([0-9a-zA-Z\\-_]+)%").matcher(json);
            while (m.find()) {
                keys.add(m.group(1));
            }

            for (String key : keys) {
                String keyPlaceholder = String.format("%%metadata:%s%%", key);
                String value = ((Form) response.getObject()).getMetadata().get(key);
                if (value != null) {
                    json = json.replace(keyPlaceholder, value);
                }
            }
        }

        jsonGenerator.writeRaw(json);
    }
}
