package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

import java.io.File;
import java.util.List;

/**
 * Created by PC on 12/07/2015.
 */
public class CommentAdapter extends ArrayAdapter<Comentario> {


    private LayoutInflater inflater;
    private int resourceId;
    private Context context;

    public CommentAdapter(Context context, int resource, List<Comentario> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Comentario comment = getItem(position);
        convertView = inflater.inflate(resourceId, parent, false);
        TextView tvComentario = (TextView) convertView.findViewById(R.id.tvComentario);
        tvComentario.setText(comment.comment);

        TextView tvNomeUSuario = (TextView) convertView.findViewById(R.id.tvNomeUsuarioComentario);
        tvNomeUSuario.setText(comment.nomeUsuario);

        ImageLoader.ImageCache imageCacheFotoUsuario = new BitmapLruCache();
        ImageLoader imageLoaderFotousuario = new ImageLoader(newRequestQueue(context), imageCacheFotoUsuario);

        CircularNetworkImageView vFotoUsuario = (CircularNetworkImageView) convertView.findViewById(R.id.imgFotoUsuarioComentario);
        vFotoUsuario.setImageUrl(comment.pathPhotoUser, imageLoaderFotousuario);
        /*
        chkProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prod.checado = isChecked;
            }
        });
        */

        return convertView;
    }





    // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;

    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "photos";

    // Most code copied from "Volley.newRequestQueue(..)", we only changed cache directory
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
