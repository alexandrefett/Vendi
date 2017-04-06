package com.vendi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.DynamicDrawableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

class MyMultiAutoCompleteTextView extends MultiAutoCompleteTextView {
    private static final String TAG = "--->>>";

    public MyMultiAutoCompleteTextView(Context context) {
        super(context);
    }

    @Override
    protected void replaceText(CharSequence text) {
        Log.d(TAG, "replaceText " + text.getClass() + " " + text);
        super.replaceText(getSpanned(text.toString()));
    }


    private Spanned getSpanned(String name) {
        TextView tv = (TextView) LayoutInflater.from(this.getContext()).inflate(R.layout.token_brand, null);
        tv.setText(name);
        SpannableStringBuilder sb = new SpannableStringBuilder(name);
        sb.setSpan(new ViewReplacementSpan(tv), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

    class ViewReplacementSpan extends DynamicDrawableSpan {
        private View v;
        private Drawable drawable;

        public ViewReplacementSpan(View v) {
            super(ALIGN_BOTTOM);
            this.v = v;
            int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
            v.measure(spec, spec);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            drawable = new SpanDrawable();
            drawable.setBounds(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        }

        @Override
        public Drawable getDrawable() {
            return drawable;
        }

        class SpanDrawable extends Drawable {
            @Override
            public void draw(Canvas canvas) {
                canvas.clipRect(getBounds());
                v.draw(canvas);
            }

            @Override
            public void setAlpha(int alpha) {
            }

            @Override
            public void setColorFilter(ColorFilter cf) {
            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSLUCENT;
            }
        }
    }
}