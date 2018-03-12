package com.crossphd.cnews;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by chris on 1/24/2018.
 */

public class ArticleAdapter extends ArrayAdapter<Article> {


    public ArticleAdapter(@NonNull Context context, @NonNull ArrayList<Article> articles) {
        super(context, 0, articles);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }


        Article currentArticle = getItem(position);

        ImageView image = listItemView.findViewById(R.id.article_image);

        if (currentArticle != null) {
            new DownloadImageTask(image).execute(currentArticle.getmImageUrl());
        }

        if (currentArticle != null) {
            Picasso.with(this.getContext())
                    .load(currentArticle.getmImageUrl())
                    .centerCrop()
                    .transform(new CircleTransform(50,0))
                    .fit()
                    .into(image);
        }


        TextView title = (TextView) listItemView.findViewById(R.id.article_title);
        title.setText(currentArticle.getmTitle());

//        TextView author = (TextView) listItemView.findViewById(R.id.article_author);
//        author.setText(currentArticle.getmAuthor());

        TextView source = (TextView) listItemView.findViewById(R.id.article_source);
        source.setText(currentArticle.getmSource());

        TextView date = (TextView) listItemView.findViewById(R.id.date_text_view);
        String dateSubstring = currentArticle.getmDate().substring(0,10);
        date.setText(dateSubstring);

        return listItemView;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;
        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap bmp = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                bmp = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bmp;
        }
        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
