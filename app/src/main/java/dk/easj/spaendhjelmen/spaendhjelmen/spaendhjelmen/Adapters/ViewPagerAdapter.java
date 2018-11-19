package dk.easj.spaendhjelmen.spaendhjelmen.spaendhjelmen.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import dk.easj.spaendhjelmen.spaendhjelmen.R;

public class ViewPagerAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater layoutInflater;
    public ArrayList<Drawable> pictureListdrawable = new ArrayList<>();
    //private Integer [] pictures = {R.drawable.side1, R.drawable.side2,R.drawable.side3,R.drawable.side4,R.drawable.side5};

    public ViewPagerAdapter(Context context, ArrayList<Drawable> list) {
        this.context = context;
        this.pictureListdrawable =list;
    }

    @Override
    public int getCount() {
            return pictureListdrawable.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.custom_viewpager, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewPagerSpecific);
        //imageView.setImageResource(pictures[position]);
        imageView.setImageDrawable(pictureListdrawable.get(position));
        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view,0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}
