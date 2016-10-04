package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by PC on 26/06/2015.
 */
public class ImageAdapter extends PagerAdapter {
    Context context;

    private ArrayList<String> urlImages = new ArrayList<String>();

    ImageAdapter(Context context){
        this.context=context;
    }

    @Override
    public int getCount() {
        return urlImages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((NetworkImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imageLoader = new ImageLoader(newRequestQueue(context), imageCache);

        NetworkImageView networkImageView =  new NetworkImageView(context);

        //ImageView imageView = new ImageView(context);
        //int padding = context.getResources().getDimensionPixelSize(R.dimen.abc_dialog_padding_material);
        int padding = 0;
        networkImageView.setPadding(padding, padding, padding, padding);
        networkImageView.setAdjustViewBounds(true);
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //imageView.setImageResource(GalImages[position]);
        networkImageView.setImageUrl(urlImages.get(position), imageLoader);
        ((ViewPager) container).addView(networkImageView, 0);
        return networkImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }


    public ArrayList<String> getUrlImages() {
        return urlImages;
    }

    public void setUrlImages(ArrayList<String> urlImages) {
        this.urlImages = urlImages;
    }







    // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;

    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "photos";

    // Most code copied from "Volley.newRequestQueue(..)", I only changed cache directory
    private static RequestQueue newRequestQueue(Context context) {
        // define cache folder
        File rootCache = context.getExternalCacheDir();
        if (rootCache == null) {
            //L.w("Can't find External Cache Dir, switching to application specific cache directory");
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
        cacheDir.mkdirs();

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
        RequestQueue queue = new RequestQueue(diskBasedCache, network);
        queue.start();

        return queue;
    }

}
