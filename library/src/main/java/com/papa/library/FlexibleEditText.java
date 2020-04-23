package com.papa.library;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 伸缩搜索栏
 * 收起时：只有Button显示
 * 展开时：Botton背景变色并扩大至包裹住编辑栏
 * 默认：编辑栏在Button左边
 */
public class FlexibleEditText extends FrameLayout {

    private static final String TAG = "SearchView";

    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;//默认背景颜色
    public static final int DEFAULT_DURATION = 500;//默认背景颜色

    private EditText editText;//用户写的编辑栏，需要用户自己定义在xml中
    private ViewGroup editTextLayout;//编辑栏布局，用于占位，在解析用户编写的EditText后会将其添加进该占位布局
    private ImageView imageView;//icon
    private View view;//伸缩背景
    private ViewWrapper wrapper;//包装类，用于实现动画改变View的宽度

    private Drawable buttonIcon;//图标
    private int buttonIconTint;//图标着色

    /*伸缩背景↓*/
    private GradientDrawable background;//背景
    private int backgroundColor;//背景颜色
    private float corner;//背景圆角大小
    private boolean leftTopCornerEnable;//是否启用左上圆角
    private boolean rightTopCornerEnable;//是否启用右上圆角
    private boolean leftBottomCornerEnable;//是否启用左下圆角
    private boolean rightBottomCornerEnable;//是否启用右下圆角
    private float[] leftTopCorner = new float[2];//左上圆角大小
    private float[] rightTopCorner = new float[2];//右上圆角大小
    private float[] leftBottomCorner = new float[2];//左下圆角大小
    private float[] rightBottomCorner = new float[2];//右下圆角大小
    private float[] corners = new float[8];//所有圆角大小
    /*伸缩背景↑*/

    private int openedWidth;//展开时宽度
    private int closedWidth;//收起时宽度

    private float buttonWidth;//icon按钮的宽度
    private float buttonHeight;//icon按钮的高度
    private float editTextWidth;//编辑栏editText的宽度

    private long duration;

    protected InputMethodManager inputMethodManager;//输入法服务

    private OnIconClickListener listener;

    private boolean isOpen = false;//展开/收起标志，默认为收起
    private boolean isFirst = true;//判断是否为第一次修改

    /*构造函数↓*/
    public FlexibleEditText(Context context) {
        super(context);
        init(context, null);
    }

    public FlexibleEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FlexibleEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlexibleEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, null);
    }
    /*构造函数↑*/

    /*初始化函数↓*/
    private void init(Context context, AttributeSet attrs){
        initAttrs(context, attrs);
        inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        if (attrs != null){
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FlexibleEditText);
            backgroundColor = typedArray.getColor(R.styleable.FlexibleEditText_fet_backgroundColor, DEFAULT_BACKGROUND_COLOR);//获取背景颜色
            buttonIcon = typedArray.getDrawable(R.styleable.FlexibleEditText_fet_buttonIcon);//获取按钮图标
            if (buttonIcon != null){
                buttonIcon.mutate();
            }
            buttonIconTint = typedArray.getColor(R.styleable.FlexibleEditText_fet_buttonIconTint, -1);//获取图标着色
            corner = typedArray.getDimension(R.styleable.FlexibleEditText_fet_corner, 0f);//获取圆角大小
            leftTopCornerEnable = typedArray.getBoolean(R.styleable.FlexibleEditText_fet_leftTopCornerEnable, true);//获取左上角圆角与否
            rightTopCornerEnable = typedArray.getBoolean(R.styleable.FlexibleEditText_fet_rightTopCornerEnable, true);//获取左下角圆角与否
            leftBottomCornerEnable = typedArray.getBoolean(R.styleable.FlexibleEditText_fet_leftBottomCornerEnable, true);//获取又上角圆角与否
            rightBottomCornerEnable = typedArray.getBoolean(R.styleable.FlexibleEditText_fet_rightBottomCornerEnable, true);//获取右下角圆角与否
            getCorners(corner);//计算所有圆角大小
            duration = typedArray.getInteger(R.styleable.FlexibleEditText_fet_duration, DEFAULT_DURATION);
            buttonWidth = typedArray.getDimension(R.styleable.FlexibleEditText_fet_buttonWidth, 200);//获取icon按钮宽度
            buttonHeight = typedArray.getDimension(R.styleable.FlexibleEditText_fet_buttonHeight, 200);//获取icon按钮高度
            typedArray.recycle();
        }
    }
    /*初始化函数↑*/

    /*重写↓*/


    /**
     * 在完成解析时调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        //获取用户自定义的子View，只能是编辑栏EditText
        if (getChildCount() == 1 && getChildAt(0) instanceof EditText){
            editText = (EditText) getChildAt(0);
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            editText.setCursorVisible(true);
        }else{
            return;
        }

        addView(LayoutInflater.from(getContext()).inflate(R.layout.fet_layout, this, false));//加载xml布局进当前布局

        wrapper = new ViewWrapper(this);//实例化包装视图，并传入本View

        imageView = findViewById(R.id.fet_icon);//获取图标控件
        view = findViewById(R.id.fet_background);//获取伸缩背景
        editTextLayout = findViewById(R.id.fet_editTextLayout);//获取编辑栏占位布局

        LayoutParams etl_lp = (LayoutParams)editTextLayout.getLayoutParams();//获取编辑栏占位布局的布局属性
        etl_lp.setMarginEnd((int)buttonWidth);//设置尾部间隔
        editTextLayout.setLayoutParams(etl_lp);//设置编辑栏占位布局的布局属性
        editTextLayout.setPadding((int)corner, 0, 0, 0);

        //将编辑栏搬到占位布局中
        removeView(editText);//移除原布局中的editText
        editTextLayout.addView(editText);//将原布局中的editText添加到占位布局中


        imageView.setImageDrawable(buttonIcon);//设置按钮Icon
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && buttonIconTint != -1) {
            imageView.setImageTintList(ColorStateList.valueOf(buttonIconTint));//设置作色
        }

        ViewGroup.LayoutParams iv_lp = imageView.getLayoutParams();//获取icon按钮的布局属性
        iv_lp.width = (int)buttonWidth;//修改宽度
        iv_lp.height = (int)buttonHeight;//修改高度
        imageView.setLayoutParams(iv_lp);//设置icon按钮的布局属性

        //构建伸缩背景
        background = new GradientDrawable();
        background.setColor(backgroundColor);//设置背景颜色
        background.setCornerRadii(corners);//设置背景圆角效果
        view.setBackground(background);//设置背景

        //设置icon的点击事件
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clickEvent();
            }
        });
        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length()>0){
                    listener.clickEvent(editText);
                }else {
                    clickEvent();
                }
            }
        });
        editText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    close();
                }
            }
        });

    }

    /**
     * 测量布局的大小
     * 大小必须直接指定具体的值，20dp……或者match_parent，设置为wrap_content时长宽都为0
     * @param widthMeasureSpec xml中的layout_width
     * @param heightMeasureSpec xml中的layout_height
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int mRealWidth = 0;//宽
        int mRealHeight = 0;//高

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);//获取宽度模式
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);//获取高度模式
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);//获取宽度大小
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);//获取高度大小

        //具体数值或match_parent
        if (widthMode == MeasureSpec.EXACTLY) {
            mRealWidth = widthSize;
        }
        int measureWidth = MeasureSpec.makeMeasureSpec(mRealWidth, widthMode);

        if (heightMode == MeasureSpec.EXACTLY) {
            mRealHeight = heightSize;
        }
        int measureHeight = MeasureSpec.makeMeasureSpec(mRealHeight, heightMode);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (editText != null && isFirst){//只在第一次加载时执行
            editTextWidth = editText.getMeasuredWidth();//获取编辑栏的宽度
            openedWidth = getMeasuredWidth();//获取展开时的宽度
            closedWidth = imageView.getMeasuredWidth();//获取收起时的宽度
            postDelayed(new Runnable() {//延迟执行，修改整个View的宽度为收起时的宽度
                @Override
                public void run() {
                    ViewGroup.LayoutParams lp = getLayoutParams();
                    lp.width = closedWidth;
                    setLayoutParams(lp);
                    view.setAlpha(0f);
                }
            }, 20);
            isFirst = false;//更新flag
        }
    }
    /*重写↑*/

    /**
     * 伸缩按钮点击事件
     */
    private void clickEvent(){
        if (isOpen) {
            close();
        } else {
            open();
        }
    }

    public void setListener(OnIconClickListener listener){
        this.listener = listener;
    }

    private float[] getCorners(float corner) {
        leftTopCorner[0] = 0;
        leftTopCorner[1] = 0;
        rightTopCorner[0] = 0;
        rightTopCorner[1] = 0;
        leftBottomCorner[0] = 0;
        leftBottomCorner[1] = 0;
        rightBottomCorner[0] = 0;
        rightBottomCorner[1] = 0;
        if (this.leftTopCornerEnable || this.rightTopCornerEnable || this.leftBottomCornerEnable || this.rightBottomCornerEnable) {
            if (this.leftTopCornerEnable) {
                leftTopCorner[0] = corner;
                leftTopCorner[1] = corner;
            }
            if (this.rightTopCornerEnable) {
                rightTopCorner[0] = corner;
                rightTopCorner[1] = corner;
            }
            if (this.leftBottomCornerEnable) {
                leftBottomCorner[0] = corner;
                leftBottomCorner[1] = corner;
            }
            if (this.rightBottomCornerEnable) {
                rightBottomCorner[0] = corner;
                rightBottomCorner[1] = corner;
            }
        } else {
            leftTopCorner[0] = corner;
            leftTopCorner[1] = corner;
            rightTopCorner[0] = corner;
            rightTopCorner[1] = corner;
            leftBottomCorner[0] = corner;
            leftBottomCorner[1] = corner;
            rightBottomCorner[0] = corner;
            rightBottomCorner[1] = corner;
        }
        corners[0] = leftTopCorner[0];
        corners[1] = leftTopCorner[1];
        corners[2] = rightTopCorner[0];
        corners[3] = rightTopCorner[1];
        corners[4] = rightBottomCorner[0];
        corners[5] = rightBottomCorner[1];
        corners[6] = leftBottomCorner[0];
        corners[7] = leftBottomCorner[1];
        return corners;
    }

    /**
     * 展开
     */
    public void open(){
        ObjectAnimator.ofInt(wrapper, "width", closedWidth, openedWidth).setDuration(duration).start();
        ObjectAnimator.ofFloat(view, "alpha", 0f, 1f).setDuration(duration).start();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (editText != null) {
                    editText.requestFocus();//获取焦点
                    inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);//打开软键盘
                }
            }
        }, duration);
        isOpen = true;
    }

    /**
     * 收起
     */
    public void close(){
        ObjectAnimator.ofInt(wrapper, "width", openedWidth, closedWidth).setDuration(duration).start();
        ObjectAnimator.ofFloat(view, "alpha", 1f, 0f).setDuration(duration).start();
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);//关闭键盘
        editText.clearFocus();//清除焦点
        editText.setText("");//清空内容
        isOpen = false;
    }

    /**
     * 包装类，用于实现动画改变本View的宽度
     */
    private static class ViewWrapper {
        private View mTarget;

        // 构造方法:传入需要包装的对象
        public ViewWrapper(View target) {
            mTarget = target;
        }

        // 为宽度设置get（） & set（）
        public int getWidth() {
            return mTarget.getLayoutParams().width;
        }

        public void setWidth(int width) {
            mTarget.getLayoutParams().width = width;
            mTarget.requestLayout();
        }

    }

    /**
     * Icon点击事件接口，由外部实现
     * {@link #setListener(OnIconClickListener)}
     */
    public interface OnIconClickListener{
        void clickEvent(EditText view);
    }

}
