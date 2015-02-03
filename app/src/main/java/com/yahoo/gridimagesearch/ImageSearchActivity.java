package com.yahoo.gridimagesearch;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.etsy.android.grid.StaggeredGridView;

import java.util.ArrayList;


public class ImageSearchActivity extends ActionBarActivity implements EditSettingsDialog.EditSettingsDialogListener {

    private ImageSearchAdapter adapter;
    private StaggeredGridView gvSearchResults;
    private String query;
    private int startIndex = 0;
    private int fetchingIndex = -1;

    private String imageSize = null;
    private String imageColor = null;
    private String imageType = null;
    private String siteSearch = null;

    private static int pageSize = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        this.gvSearchResults = (StaggeredGridView) this.findViewById(R.id.gvSearchResults);
        final ImageSearchActivity instance = this;
        this.gvSearchResults.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (instance.query != null && instance.query.length() > 0) {
                    if (firstVisibleItem+visibleItemCount >= totalItemCount){
                        if (instance.fetchingIndex == instance.startIndex){
                            return;
                        }

                        instance.fetchingIndex = instance.startIndex;

                        if (instance.startIndex/instance.pageSize == 8) {
                            Log.i("INFO","End of stream");
                            return;
                        }

                        Log.i("INFO","Fetching page "+instance.startIndex/instance.pageSize);
                        fetchImagesAsync(instance.query, instance.startIndex);
                    }
                }
            }
        });

        this.gvSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchImage image = instance.adapter.getItem(position);
                Intent i = new Intent(ImageSearchActivity.this, FullScreenImageActivity.class);
                i.putExtra("imageUrl",image.url);
                startActivity(i);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);

        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
        searchView.setIconifiedByDefault(false);
        final ImageSearchActivity instance = this;

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                instance.query = s;

                if (instance.adapter != null){
                    instance.adapter.clear();
                }

                if (instance.fetchingIndex == instance.startIndex){
                    return true;
                }

                instance.fetchingIndex = instance.startIndex;
                Log.i("INFO","Fetching page "+instance.startIndex/instance.pageSize);
                fetchImagesAsync(s, instance.startIndex);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s == null || s.length() == 0) {
                    instance.clearQuery();
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            this.showSettingsDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fetchImagesAsync(String query, final int startIndex) {
        final ImageSearchActivity instance = this;
        ImageSearchAPI.getImagesForQuery(query,
                                         startIndex,
                                         this.imageSize,
                                         this.imageColor,
                                         this.imageType,
                                         this.siteSearch,
                                         new ImageSearchCallback() {
            @Override
            public void onSuccess(ArrayList<SearchImage> images) {
                if (instance.adapter == null) {
                    instance.adapter = new ImageSearchAdapter(instance, images);
                    instance.gvSearchResults.setAdapter(instance.adapter);
                }
                else {
                    instance.adapter.addAll(images);
                }

                instance.startIndex = startIndex+pageSize;
                Log.i("INFO", "Next page "+instance.startIndex/instance.pageSize);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.d("DEBUG","Error fetching images");
            }
        });
    }

    private void clearQuery() {
        this.query = null;
        this.clearParams();
        Log.i("INFO", "Cleared query");
    }

    private void clearParams() {
        this.startIndex = 0;
        this.fetchingIndex = -1;
        this.adapter.clear();
        Log.i("INFO","Cleared params");
    }

    private void showSettingsDialog() {
        FragmentManager fm = getSupportFragmentManager();
        EditSettingsDialog editSettingsDialog = EditSettingsDialog
                                                    .newInstance(this.imageSize,
                                                            this.imageColor,
                                                            this.imageType,
                                                            this.siteSearch);
        editSettingsDialog.show(fm, "fragment_edit_settings");
    }

    @Override
    public void onSaveOrCancelSettings(String imageSize, String imageColor, String imageType, String siteSearch) {
        boolean updated = false;

        if (imageSize != this.imageSize) {
            this.imageSize = imageSize;
            updated = true;
        }

        if (imageColor != this.imageColor) {
            this.imageColor = imageColor;
            updated = true;
        }

        if (imageType != this.imageType) {
            this.imageType = imageType;
            updated = true;
        }

        if (siteSearch != this.siteSearch) {
            this.siteSearch = siteSearch;
            updated = true;
        }

        if (updated) {
            this.clearParams();
            this.fetchImagesAsync(this.query, this.startIndex);
        }
    }
}
