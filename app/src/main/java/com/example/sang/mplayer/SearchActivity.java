package com.example.sang.mplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.sang.mplayer.data.JsonResponse;
import com.example.sang.mplayer.data.RequestInterface;
import com.example.sang.mplayer.data.TrackAdapter;
import com.example.sang.mplayer.modal.Track;

import java.util.ArrayList;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener ,
        TrackAdapter.OnTrackClickHandler , View.OnClickListener{

//    @BindView(R.id.rv_track_list)
//    RecyclerView rvTrackList;

    @BindView(R.id.sv_track_search)
    SearchView tvTextSearch;

    @BindView(R.id.iv_fav_white_icon)
    ImageView ivFavWhiteIcon;
    @BindView(R.id.tv_fav_count)
    TextView tvFavCount;
    @BindView(R.id.viewpager)
    ViewPager vpTrackList;




    private Context context;
    private TrackAdapter adapter;
    ArrayList<Track> trackList;
    private static int resultSize;

    private static final String BASE_URL="https://itunes.apple.com/";
    private static final String SEARCH_KEY="search";


    private static final String LOCATION="US";
    private static final String LOOKUP="lookup";

    private static final String default_sort ="popularity.desc";

    private static String TERM_PARAM="term";
    private static String LIMIT_PARAM="limit";
    private static String ENTITY_PARAM="entity";
    private static String SONG_ID_PARAM="id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        context = getApplicationContext();
        tvTextSearch.setOnQueryTextListener(this);

        ivFavWhiteIcon.setOnClickListener(this);

    }



    private void loadJson( String query) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<JsonResponse> call = request.getJSON(query);
        call.enqueue(new Callback<JsonResponse>() {
            @Override
            public void onResponse(Call<JsonResponse> call, Response<JsonResponse> response) {

                JsonResponse jsonResponse = response.body();

                if (jsonResponse !=null ) {
                    trackList = new ArrayList<Track>(jsonResponse.getResults());
                    resultSize = jsonResponse.getResultCount();


                if( trackList != null )
                    tvFavCount.setText( trackList.size()+"" );

                createViewPager();
                }
            }

            @Override
            public void onFailure(Call<JsonResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });

    }

    public void createViewPager(){
        ListView listview = null;
        RecyclerView recyclerView =null;
        ArrayList<Track> aList = new ArrayList<>() ;
        LinearLayoutManager layoutManager;
        int j=0;

        Vector<View> pages = new Vector<View>();

        int j1 = 0;

        if( trackList != null ){

        for(int i = 0;i < resultSize ; i++) {


            Track t  = trackList.get(i);


            if( i == 0 ){
                aList = new ArrayList<>();
                layoutManager =
                        new LinearLayoutManager( this ,LinearLayoutManager.VERTICAL, false);
                recyclerView = new RecyclerView( context );
                recyclerView.setLayoutManager( layoutManager );
                aList.add( t );

            }
            else if (i % 4 == 0) {

                pages.add( recyclerView );

                adapter = new TrackAdapter( this );
                recyclerView.setAdapter(adapter);
                adapter.setTracks( aList );

                aList = new ArrayList<>();
                recyclerView = new RecyclerView( context );
                layoutManager =
                        new LinearLayoutManager( this ,LinearLayoutManager.VERTICAL, false);
                recyclerView.setLayoutManager( layoutManager );
                aList.add( t);

            } else if (i == resultSize-1) {

                pages.add( recyclerView );
                aList.add( t );


                adapter = new TrackAdapter( this );
                recyclerView.setAdapter(adapter);
                adapter.setTracks( aList );


            }else{
                aList.add( t );
            }
}


        }

        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(vp, true);

        CustomPagerAdapter adapter = new CustomPagerAdapter(context,pages);
        vp.setAdapter(adapter);

    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        loadJson(newText);
        return false;
    }

    @Override
    public void onTrackClick(Track track) {
        Intent intent = new Intent(this , PlayerActivity.class);
        intent.putExtra(getString(R.string.trackselected) , track );
        startActivity( intent );
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch ( id ){
            case R.id.iv_fav_white_icon:
                Intent intent = new Intent(this , FavActivity.class);
                startActivity( intent );
                break;
        }
    }

    public class CustomPagerAdapter extends PagerAdapter {

        private Context mContext;
        private Vector<View> pages;

        public CustomPagerAdapter(Context context, Vector<View> pages) {
            this.mContext=context;
            this.pages=pages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = pages.get(position);
            container.addView(page);
            return page;
        }

        @Override
        public int getCount() {
            return pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

    }
}
