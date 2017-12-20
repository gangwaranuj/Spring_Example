package com.workmarket.service.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.workmarket.biz.gen.Messages.WMLocale;
import org.apache.commons.lang.NotImplementedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class LocaleSerializationServiceImpl implements LocaleSerializationService {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(WMLocale.class, LocaleSerializer.getInstance())
            .create();

    @Override
    public String toJson(WMLocale locale) {
        return gson.toJson(locale);
    }

    @Override
    public String toJson(List<?> collection) {
        return gson.toJson(collection);
    }

    @Override
    public WMLocale fromJson(String json) {
        throw new NotImplementedException();
    }

    @Override
    public WMLocale mergeJson(WMLocale object, String json) {
        throw new NotImplementedException();
    }

    private static class LocaleSerializer implements JsonSerializer<WMLocale> {
        private static final LocaleSerializer INSTANCE = new LocaleSerializer();

        private LocaleSerializer(){}
        public static LocaleSerializer getInstance() { return INSTANCE; }

        @Override
        public JsonElement serialize(WMLocale locale, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("code", locale.getCode());
            jsonObject.addProperty("language", locale.getLanguage());
            jsonObject.addProperty("iconUrl", locale.getIconUrl());
            return jsonObject;
        }
    }
}
