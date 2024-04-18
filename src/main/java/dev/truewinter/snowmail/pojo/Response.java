package dev.truewinter.snowmail.pojo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

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
