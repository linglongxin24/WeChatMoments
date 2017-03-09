package cn.bluemobi.dylan.wechatmoments.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import cn.bluemobi.dylan.photoview.ImagePagerActivity;
import cn.bluemobi.dylan.wechatmoments.R;
import cn.bluemobi.dylan.wechatmoments.entity.TweetsEntity;

/**
 * 描述:
 * 作者：HMY
 * 时间：2016/5/10
 */
public class NineGridLayout extends ViewGroup {
    private String TAG = "NineGridLayout";
    protected static final int MAX_W_H_RATIO = 3;
    /**
     * 默认间距
     */
    private static final float DEFUALT_SPACING = 3f;

    protected Context mContext;
    private float mSpacing = DEFUALT_SPACING;
    private int mColumns;
    private int mRows;
    private int mTotalWidth;
    private int mSingleWidth;
    private List<TweetsEntity.ImagesEntity> mUrlList = new ArrayList<>();

    public NineGridLayout(Context context) {
        super(context);
        init(context);
    }

    public NineGridLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.NineGridLayout);

        mSpacing = typedArray.getDimension(R.styleable.NineGridLayout_sapcing, DEFUALT_SPACING);
        typedArray.recycle();
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mTotalWidth = right - left;
        /**计算单个的宽度**/
        mSingleWidth = (int) ((mTotalWidth - mSpacing * (3 - 1)) / 3);

    }

    /**
     * 设置图片数据
     *
     * @param urlList
     */
    public void setUrlList(List<TweetsEntity.ImagesEntity> urlList) {
        if (getListSize(urlList) == 0) {
            setVisibility(GONE);
            return;
        }
        mUrlList.clear();
        mUrlList.addAll(urlList);
        notifyDataSetChanged();
    }

    /**
     * 更新界面
     */
    public void notifyDataSetChanged() {
        post(new TimerTask() {
            @Override
            public void run() {
                refresh();
            }
        });
    }

    /**
     * 刷新界面
     */
    private void refresh() {
        removeAllViews();
        int size = getListSize(mUrlList);
        if (size > 0) {
            setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
        }

        if (size == 1) {
            /**当只有一张图片的时候**/
            String url = mUrlList.get(0).getUrl();
            ImageView imageView = createImageView(0, url);

            LayoutParams params = getLayoutParams();
            params.height = mSingleWidth;
            setLayoutParams(params);
            imageView.layout(0, 0, mSingleWidth, mSingleWidth);

            boolean isShowDefualt = displayOneImage(imageView, url, mTotalWidth);
            if (isShowDefualt) {
                layoutImageView(imageView, 0, url);
            } else {
                addView(imageView);
            }
            return;
        }

        generateChildrenLayout(size);
        layoutParams();

        for (int i = 0; i < size; i++) {
            String url = mUrlList.get(i).getUrl();
            ImageView imageView;
            imageView = createImageView(i, url);
            layoutImageView(imageView, i, url);
        }
    }

    private void layoutParams() {
        int singleHeight = mSingleWidth;

        //根据子view数量确定高度
        LayoutParams params = getLayoutParams();
        params.height = (int) (singleHeight * mRows + mSpacing * (mRows - 1));
        setLayoutParams(params);
    }

    private ImageView createImageView(final int i, final String url) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImage(i, url, mUrlList);
            }
        });
        return imageView;
    }

    /**
     * 布局每张图片
     *
     * @param imageView
     * @param url
     */
    private void layoutImageView(ImageView imageView, int i, String url) {
        final int singleWidth = (int) ((mTotalWidth - mSpacing * (3 - 1)) / 3);
        int singleHeight = singleWidth;

        int[] position = findPosition(i);
        int left = (int) ((singleWidth + mSpacing) * position[1]);
        int top = (int) ((singleHeight + mSpacing) * position[0]);
        int right = left + singleWidth;
        int bottom = top + singleHeight;

        imageView.layout(left, top, right, bottom);

        addView(imageView);
        displayImage(imageView, url);
    }

    private int[] findPosition(int childNum) {
        int[] position = new int[2];
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mColumns; j++) {
                if ((i * mColumns + j) == childNum) {
                    position[0] = i;//行
                    position[1] = j;//列
                    break;
                }
            }
        }
        return position;
    }

    /**
     * 根据图片个数确定行列数量
     * 对应关系如下
     * num  row column
     * 1       1    1
     * 2       1    2
     * 3       1    3
     * 4       2    2
     * 5       2    3
     * 6       2    3
     * 7       3    3
     * 8       3    3
     * 9       3    3
     *
     * @param length
     */
    private void generateChildrenLayout(int length) {
        if (length <= 3) {
            mRows = 1;
            mColumns = length;
        } else if (length <= 6) {
            mRows = 2;
            mColumns = 3;
            if (length == 4) {
                mColumns = 2;
            }
        } else {
            mColumns = 3;
            mRows = 3;
        }

    }

    /**
     * 重新设置单张图图片是的宽高
     *
     * @param imageView
     * @param width
     * @param height
     */
    private void setOneImageLayoutParams(ImageView imageView, int width, int height) {
        imageView.setLayoutParams(new LayoutParams(width, height));
        imageView.layout(0, 0, width, height);

        LayoutParams params = getLayoutParams();
        params.height = height;
        setLayoutParams(params);
    }

    private int getListSize(List<TweetsEntity.ImagesEntity> list) {
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    /**
     * @param imageView
     * @param url
     * @param parentWidth 父控件宽度
     * @return true 代表按照九宫格默认大小显示，false 代表按照自定义宽高显示
     */

    protected boolean displayOneImage(final ImageView imageView, String url, final int parentWidth) {
        Glide.with(mContext).load(url).asBitmap().placeholder(R.mipmap.image_default).error(R.mipmap.image_default).into(imageView);
//       Glide.with(mContext).load(url).asBitmap().placeholder(R.mipmap.image_default).error(R.mipmap.image_default).into(new SimpleTarget<Bitmap>() {
//            @Override
//            public void onResourceReady(Bitmap bitmap, GlideAnimation<? super Bitmap> glideAnimation) {
//                int w = bitmap.getWidth();
//                int h = bitmap.getHeight();
//
//                int newW;
//                int newH;
//                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
//                    newW = parentWidth / 2;
//                    newH = newW * 5 / 3;
//                } else if (h < w) {//h:w = 2:3
//                    newW = parentWidth * 2 / 3;
//                    newH = newW * 2 / 3;
//                } else {//newH:h = newW :w
//                    newW = parentWidth / 2;
//                    newH = h * newW / w;
//                }
//                setOneImageLayoutParams(imageView, newW, newH);
//                imageView.setImageBitmap(bitmap);
//            }
//        });
//Glide.with(mContext).load(url).listener(new RequestListener<String, GlideDrawable>() {
//    @Override
//    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//        return false;
//    }
//
//    @Override
//    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//
//        return true;
//    }
//}).into(imageView);
//        ImageLoaderUtil.displayImage(mContext, imageView, url, ImageLoaderUtil.getPhotoImageOption(), new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap bitmap) {
//                int w = bitmap.getWidth();
//                int h = bitmap.getHeight();
//
//                int newW;
//                int newH;
//                if (h > w * MAX_W_H_RATIO) {//h:w = 5:3
//                    newW = parentWidth / 2;
//                    newH = newW * 5 / 3;
//                } else if (h < w) {//h:w = 2:3
//                    newW = parentWidth * 2 / 3;
//                    newH = newW * 2 / 3;
//                } else {//newH:h = newW :w
//                    newW = parentWidth / 2;
//                    newH = h * newW / w;
//                }
//                setOneImageLayoutParams(imageView, newW, newH);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        });
        return false;
    }

    protected void displayImage(ImageView imageView, String url) {
        Glide.with(mContext).load(url).placeholder(R.mipmap.image_default).error(R.mipmap.image_default).into(imageView);
//        ImageLoaderUtil.getImageLoader(mContext).displayImage(url, imageView, ImageLoaderUtil.getPhotoImageOption());
    }

    protected void onClickImage(int position, String url, List<TweetsEntity.ImagesEntity> urlList) {
        String[] urls=new String[urlList.size()];
        for(int i = 0; i <urlList.size() ; i++) {
            urls[i]=urlList.get(i).getUrl();
        }
        Intent intent = new Intent(mContext, ImagePagerActivity.class);
        String[] arr = new String[urlList.size()];
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, arr);
        intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX,position);
        mContext.startActivity(intent);
    }
}
