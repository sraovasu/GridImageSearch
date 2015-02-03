package com.yahoo.gridimagesearch;

import android.media.Image;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by sraovasu on 2/1/15.
 */

interface ImageSearchCallback {
    public void onSuccess(ArrayList<SearchImage> images);
    public void onFailure(Throwable throwable);
}

public class ImageSearchAPI {
    private static String IMAGE_SEARCH_API = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=8";

    public static void getImagesForQuery(String query,
                                         int startIndex,
                                         String imageSize,
                                         String imageColor,
                                         String imageType,
                                         String siteSearch,
                                         final ImageSearchCallback callback) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = IMAGE_SEARCH_API+"&q="+query+"&start="+startIndex;
        if (imageSize != null) { url += "&imgsz="+imageSize; }
        if (imageColor != null) { url += "&imgcolor="+imageColor; }
        if (imageType != null) { url += "&imgtype="+imageType; }
        if (siteSearch != null) { url += "&as_sitesearch="+siteSearch; }

        Log.i("INFO","Fetching query "+url);

        client.get(url, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                ArrayList<SearchImage> images = null;
                try {
                    images = processResponse(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                callback.onSuccess(images);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject response) {
                Log.i("INFO","Failure");
                callback.onFailure(throwable);
            }
        });
    }

    private static ArrayList<SearchImage> processResponse(JSONObject response) throws JSONException {
        JSONArray results = response.getJSONObject("responseData").getJSONArray("results");
        ArrayList<SearchImage> images = new ArrayList<>();

        for (int i = 0; i < results.length(); i++) {
            JSONObject resultJSON = (JSONObject)results.get(i);
            String tbUrl = resultJSON.getString("tbUrl");
            int tbWidth = resultJSON.getInt("tbWidth");
            int tbHeight = resultJSON.getInt("tbHeight");

            SearchImage image = new SearchImage();
            image.url = tbUrl;
            image.width = tbWidth;
            image.height = tbHeight;

            images.add(image);
        }

        return images;
    }
}
