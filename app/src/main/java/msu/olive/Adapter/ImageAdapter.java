package msu.olive.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import msu.olive.Model.Image;
import msu.olive.R;
import msu.olive.Server.Server;

public class ImageAdapter extends BaseAdapter {
    ArrayList<Image> images;
    Context context;

    public ImageAdapter(ArrayList<Image> images, Context context) {
        this.images = images;
        this.context = context;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.x_image, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.imageView = (ImageView) convertView.findViewById(R.id.image_image);
        final Image image = images.get(position);
        if (image.getUrl().length() > 0) {
            Picasso.with(context).load(Server.ImageURL + image.getUrl()).placeholder(R.drawable.ic_avatar).into(viewHolder.imageView);
        }
        return convertView;
    }

    public class ViewHolder {
        ImageView imageView;
    }

}
