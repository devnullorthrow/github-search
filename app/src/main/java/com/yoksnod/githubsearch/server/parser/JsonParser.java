package com.yoksnod.githubsearch.server.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public abstract class JsonParser<T> {

    public List<T> parse(JSONArray jsonArray) throws JSONException {
        List<T> result = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            T item = parseInternal(jsonArray.getJSONObject(i));
            result.add(item);
        }
        return result;
    }

    protected abstract T parseInternal(JSONObject item) throws JSONException;

    public static class Profile extends JsonParser<com.yoksnod.githubsearch.database.entity.Profile> {

        @Override
        protected com.yoksnod.githubsearch.database.entity.Profile parseInternal(JSONObject item) throws JSONException {
            String login = item.getString("login");
            String avatar = item.getString("avatar_url");
            long id = item.getLong("id");
            return new com.yoksnod.githubsearch.database.entity.Profile(0, id, avatar, login);
        }
    }
}
