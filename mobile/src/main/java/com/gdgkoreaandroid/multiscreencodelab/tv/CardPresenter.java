package com.gdgkoreaandroid.multiscreencodelab.tv;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.view.View;
import android.view.ViewGroup;

import com.gdgkoreaandroid.multiscreencodelab.MyApplication;
import com.gdgkoreaandroid.multiscreencodelab.R;
import com.gdgkoreaandroid.multiscreencodelab.data.Movie;
import com.gdgkoreaandroid.multiscreencodelab.util.ImageDownloader;
import com.gdgkoreaandroid.multiscreencodelab.util.ImageSetter;

import java.net.URI;

/**
 * Simple presenter, which binds a {@link com.gdgkoreaandroid.multiscreencodelab.data.Movie} instance
 * to a ImageCardView.
 */
public class CardPresenter extends Presenter {

    private static Context mContext;
    private static final int CARD_IMAGE_WIDTH = 313;
    private static final int CARD_IMAGE_HEIGHT = 176;

    static class ViewHolder extends Presenter.ViewHolder {
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mDefaultCardImage
                    = new ColorDrawable(mContext.getResources().getColor(R.color.primary_dark_color));
        }

        protected void updateCardViewImage(URI uri) {
            ImageDownloader downloader = MyApplication.getImageDownloaderInstance();
            downloader.downloadImage(uri.toString(), new ImageSetter() {

                @Override
                public void setEmptyDrawable() {
                    mCardView.setMainImage(mDefaultCardImage);
                }

                @Override
                public View getTargetView() {
                    return mCardView;
                }

                @Override
                public Bitmap setImageBitmap(Bitmap bitmap) {
                    Bitmap scaledBitmap = resizeBitmap(bitmap, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
                    Drawable bitmapDrawable = new BitmapDrawable(
                            mContext.getResources(), scaledBitmap);
                    mCardView.setMainImage(bitmapDrawable);
                    return scaledBitmap;
                }

                @Override
                public void setErrorDrawable() {
                    mCardView.setMainImage(mDefaultCardImage);
                }

                private Bitmap resizeBitmap(Bitmap bitmap, int width, int height) {
                    if( bitmap == null ||
                            (bitmap.getWidth() == width && bitmap.getHeight() == height) ) {
                        return bitmap;
                    }

                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                            bitmap, CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT, true);
                    bitmap.recycle();
                    return scaledBitmap;
                }
            });
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;

        if (movie.getCardImageUrl() != null) {
            ((ViewHolder) viewHolder).mCardView.setTitleText(movie.getTitle());
            ((ViewHolder) viewHolder).mCardView.setContentText(movie.getStudio());
            ((ViewHolder) viewHolder).mCardView.setMainImageDimensions(
                    CARD_IMAGE_WIDTH, CARD_IMAGE_HEIGHT);
            ((ViewHolder) viewHolder).updateCardViewImage(movie.getCardImageURI());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
    }
}
