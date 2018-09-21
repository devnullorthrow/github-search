package com.yoksnod.githubsearch.server;

import android.net.Uri;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.yoksnod.githubsearch.database.entity.Profile;
import com.yoksnod.githubsearch.server.parser.JsonParser;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchGithubProfilesCommand extends AbstractRequest<String, RequestResult<ProfilesAndTotalCount>> {

    private static final String ITEMS = "items";
    private static final String TOTAL_COUNT = "total_count";

    public SearchGithubProfilesCommand(String query) {
        super(query);
    }

    @Override
    public RequestResult<ProfilesAndTotalCount> execute() {
        final List<Profile> result = new ArrayList<>();
        final Uri uri = new Uri
                .Builder()
                .scheme("https")
                .encodedAuthority("api.github.com")
                .appendEncodedPath("search")
                .appendEncodedPath("users")
                .appendQueryParameter("q", getParams())
                .build();
        final long totalCount;
        try {
            NetHttpTransport transport = new NetHttpTransport();
            final GenericUrl url = new GenericUrl(uri.toString());

            HttpRequest request = transport
                    .createRequestFactory()
                    .buildGetRequest(url);
            HttpResponse response = request.execute();
            String toStringResult = IOUtils.toString(response.getContent());

            JSONObject json = new JSONObject(toStringResult);
            totalCount = json.getLong(TOTAL_COUNT);
            JSONArray items = json.getJSONArray(ITEMS);
            List<Profile> profiles = new JsonParser.Profile().parse(items);
            result.addAll(profiles);
        } catch (HttpResponseException e) {
            return new RequestResult.RateLimitExceedError<>(e);
        } catch (IOException e) {
            return new RequestResult.ServerError<>(e);
        } catch (JSONException e){
            return new RequestResult.ServerError<>(e);
        }
        return new RequestResult<>(new ProfilesAndTotalCount(Collections.unmodifiableList(result), totalCount));
    }
}
