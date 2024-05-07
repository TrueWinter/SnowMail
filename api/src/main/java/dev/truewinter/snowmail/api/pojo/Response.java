package dev.truewinter.snowmail.api.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.jetbrains.annotations.ApiStatus;

/**
 * @hidden
 */
@JsonSerialize(using = APISerializer.class)
public class Response {
    private final Object object;
    private final Class<? extends Views.View> view;

    public Response(Object object, Class<? extends Views.View> view) {
        this.object = object;
        this.view = view;
    }

    public Object getObject() {
        return object;
    }

    public Class<?> getView() {
        return view;
    }
}
