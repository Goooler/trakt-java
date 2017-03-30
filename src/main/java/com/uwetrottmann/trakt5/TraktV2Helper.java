package com.uwetrottmann.trakt5;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.uwetrottmann.trakt5.enums.ListPrivacy;
import com.uwetrottmann.trakt5.enums.Rating;
import com.uwetrottmann.trakt5.enums.Status;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TraktV2Helper {

    /**
     * trakt uses ISO 8601 dates with milliseconds, mostly in Zulu time (UTC).
     *
     * Have to use Z instead of X for time zone as Android SimpleDateFormat implementation does not support it despite
     * saying it does. trakt does not seem to have an issue with it.
     */
    public static final String DATE_FORMAT_TRAKT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String DATE_FORMAT_SHORT = "yyyy-MM-dd";
    public static final TimeZone DEFAULT_TIME_ZONE_TRAKT = TimeZone.getTimeZone("UTC");

    private static class DateTypeAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final SimpleDateFormat iso8601WithMillis;
        private final SimpleDateFormat iso8601Date;

        private DateTypeAdapter() {
            iso8601WithMillis = new SimpleDateFormat(DATE_FORMAT_TRAKT);
            iso8601WithMillis.setTimeZone(DEFAULT_TIME_ZONE_TRAKT);
            iso8601Date = new SimpleDateFormat(DATE_FORMAT_SHORT);
            iso8601Date.setTimeZone(DEFAULT_TIME_ZONE_TRAKT);
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT,
                JsonDeserializationContext context) throws JsonParseException {
            synchronized (iso8601WithMillis) {
                try {
                    return iso8601WithMillis.parse(json.getAsString());
                } catch (ParseException ignored) {
                }
                try {
                    return iso8601Date.parse(json.getAsString());
                } catch (ParseException e) {
                    throw new JsonSyntaxException(json.getAsString(), e);
                }
            }
        }

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            synchronized (iso8601WithMillis) {
                return new JsonPrimitive(iso8601WithMillis.format(src));
            }
        }
    }

    public static GsonBuilder getGsonBuilder() {
        GsonBuilder builder = new GsonBuilder();

        builder.registerTypeAdapter(Date.class, new DateTypeAdapter());

        // privacy
        builder.registerTypeAdapter(ListPrivacy.class, new JsonDeserializer<ListPrivacy>() {
            @Override
            public ListPrivacy deserialize(JsonElement json, Type typeOfT,
                    JsonDeserializationContext context) throws JsonParseException {
                return ListPrivacy.fromValue(json.getAsString());
            }
        });

        // rating
        builder.registerTypeAdapter(Rating.class, new JsonDeserializer<Rating>() {
            @Override
            public Rating deserialize(JsonElement json, Type typeOfT,
                    JsonDeserializationContext context) throws JsonParseException {
                return Rating.fromValue(json.getAsInt());
            }
        });
        builder.registerTypeAdapter(Rating.class, new JsonSerializer<Rating>() {
            @Override
            public JsonElement serialize(Rating src, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(src.value);
            }
        });

        // status
        builder.registerTypeAdapter(Status.class, new JsonDeserializer<Status>() {
            @Override
            public Status deserialize(JsonElement json, Type typeOfT,
                    JsonDeserializationContext context) throws JsonParseException {
                return Status.fromValue(json.getAsString());
            }
        });

        return builder;
    }

}
