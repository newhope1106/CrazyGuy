package com.charles.crazyguy.util;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import com.charles.crazyguy.widget.CustomLinearGradientDrawable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Liu Liaopu
 * date 2019/8/16
 * <p>为了让开发更简单的使用半屏背景渐变能力，该帮助类会做如下操作: </p>
 * <p>1.自动设置半屏渐变背景，支持渐变方向</p>
 * <p>2.自动扩展容器的padding值，并且通过热点检测，超过热区的点击直接透传</p>
 * <p>不要用于横竖屏切换动态布局的控件</p>
 */
public class HalfScreenPageHelper {
    private static final String TAG = "HalfScreenPageHelper";

    /**
     * 横全屏
     * */
    public static final int HORIZONTAL_FULL_SCREEN = 0;

    /**
     * 竖全屏
     * */
    public static final int VERTICAL_FULL_SCREEN = 1;

    /**
     * 有效区域相对大小
     * */
    private static final int sRelativeHotContentSize = 339;

    /**
     * 整个区域相对大小
     * */
    private static final int sRelativeFullContentSize = 409;

    /**
     * 记录构建过的参数
     * */
    private static class RebuildTag{
        int orientation = -1;
        int originWidth = -1;
        int originHeight = -1;
        int originPaddingLeft = 0;
        int originPaddingTop = 0;
        int extensionPaddingLeft = 0;
        int extensionPaddingTop = 0;
    }


    /**
     * 重新构建半屏容器属性
     * @param contentId 半屏容器id
     * @param parentView 父容器
     * */
    public static void rebuild(int contentId, View parentView) {
        rebuild(contentId, parentView, HORIZONTAL_FULL_SCREEN);
    }

    /**
     * 重新构建半屏容器属性
     * @param contentId 半屏容器id
     * @param parentView 父容器
     * @param screenOrientation 屏幕方向:横全屏{@link #HORIZONTAL_FULL_SCREEN},竖全屏{@link #VERTICAL_FULL_SCREEN}
     * */
    public static void rebuild(int contentId, View parentView, int screenOrientation) {
        if(parentView == null) {
            return;
        }
        View contentView = parentView.findViewById(contentId);
        if(contentView == null) {
            return;
        }

        rebuild(contentView, screenOrientation);
    }

    /**
     * 重新构建半屏容器属性
     * @param contentView 半屏容器
     * */
    public static void rebuild(View contentView) {
        rebuild(contentView, HORIZONTAL_FULL_SCREEN);
    }

    /**
     * 重新构建半屏容器属性
     * @param contentView 半屏容器
     * @param screenOrientation 屏幕方向:横全屏{@link #HORIZONTAL_FULL_SCREEN},竖全屏{@link #VERTICAL_FULL_SCREEN}
     * */
    public static void rebuild(final View contentView, final int screenOrientation) {
        if(contentView == null) {
            return;
        }

        int id = contentView.getId();
        Object tag = contentView.getTag(id);
        if(tag instanceof RebuildTag) {
            RebuildTag rebuildTag = (RebuildTag)tag;
            /*已经构建过，并且方向相同，就没有必要再次构建*/
            if(screenOrientation == rebuildTag.orientation) {
                return;
            }
            /*已经构建过，并且方向不同，则还原构建前的值*/
            contentView.setPadding(rebuildTag.originPaddingLeft, rebuildTag.originPaddingTop,
                    contentView.getPaddingRight(), contentView.getPaddingBottom());
            contentView.getLayoutParams().width = rebuildTag.originWidth;
            contentView.getLayoutParams().height = rebuildTag.originHeight;
            contentView.setTag(id, null);
            realRebuild(contentView, screenOrientation);
        } else {
            contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    contentView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    realRebuild(contentView, screenOrientation);
                }
            });
        }
    }

    private static void realRebuild(View contentView, int screenOrientation) {
        int id = contentView.getId();
        /*无效的点击热点*/
        if(HORIZONTAL_FULL_SCREEN == screenOrientation) {
            int width = contentView.getWidth();
            if(width == 0) {
                return;
            }
            float scale = (1.0f*sRelativeFullContentSize) / sRelativeHotContentSize;
            int fullWidth = (int)(scale * width);
            /*原始的padding left值*/
            int originPaddingLeft = contentView.getPaddingLeft();
            /*需要扩展的padding left值*/
            int extensionPaddingLeft = fullWidth - width;
            int originWidth = width;
            int originHeight = contentView.getHeight();
            contentView.getLayoutParams().width = fullWidth;
            contentView.setPadding(extensionPaddingLeft + originPaddingLeft,
                    contentView.getPaddingTop(), contentView.getRight(), contentView.getPaddingBottom());
            CustomLinearGradientDrawable drawable = new CustomLinearGradientDrawable();
            drawable.setGradientOrientation(CustomLinearGradientDrawable.ORIENTATION_HORIZONTAL);
            contentView.setBackground(drawable);

            RebuildTag rebuildTag = new RebuildTag();
            rebuildTag.orientation = HORIZONTAL_FULL_SCREEN;
            rebuildTag.originPaddingLeft = originPaddingLeft;
            rebuildTag.originPaddingTop = contentView.getPaddingTop();
            rebuildTag.extensionPaddingLeft = extensionPaddingLeft;
            rebuildTag.extensionPaddingTop = 0;
            rebuildTag.originWidth = originWidth;
            rebuildTag.originHeight = originHeight;
            contentView.setTag(id, rebuildTag);
        } else if(VERTICAL_FULL_SCREEN == screenOrientation) {
            int height = contentView.getHeight();
            if(height == 0) {
                return;
            }
            float scale = (1.0f*sRelativeFullContentSize) / sRelativeHotContentSize;
            int fullHeight = (int)(scale * height);
            /*原始的padding top值*/
            int originPaddingTop = contentView.getPaddingTop();
            /*需要扩展的padding top值*/
            int extensionPaddingTop = fullHeight - height;
            int originWidth = contentView.getWidth();
            int originHeight = height;
            contentView.getLayoutParams().height = fullHeight;
            contentView.setPadding(originPaddingTop + extensionPaddingTop,
                    contentView.getPaddingTop(), contentView.getRight(), contentView.getPaddingBottom());
            CustomLinearGradientDrawable drawable = new CustomLinearGradientDrawable();
            drawable.setGradientOrientation(CustomLinearGradientDrawable.ORIENTATION_VERTICAL);
            contentView.setBackground(drawable);

            RebuildTag rebuildTag = new RebuildTag();
            rebuildTag.orientation = VERTICAL_FULL_SCREEN;
            rebuildTag.originPaddingLeft = contentView.getPaddingLeft();
            rebuildTag.originPaddingTop = originPaddingTop;
            rebuildTag.extensionPaddingLeft = 0;
            rebuildTag.extensionPaddingTop = extensionPaddingTop;
            rebuildTag.originWidth = originWidth;
            rebuildTag.originHeight = originHeight;
            contentView.setTag(id, rebuildTag);
        }

        final boolean needRewriteTouchListener;
        if(contentView.isClickable() && contentView.isFocusable()) {
            contentView.setClickable(false);
            contentView.setFocusable(false);
            needRewriteTouchListener = true;
        } else {
            needRewriteTouchListener = false;
        }
        final View.OnTouchListener onTouchListener = getOnTouchListener(contentView);
        //通过反射方式获取OnTouchListener;
        if(needRewriteTouchListener || onTouchListener != null) {
            /*以下是为了劫持onTouch事件，只在热点区域生效*/
            contentView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                        float posX = motionEvent.getX();
                        float posY = motionEvent.getY();
                        Object tag = view.getTag(view.getId());
                        if(tag instanceof RebuildTag) {
                            RebuildTag rebuildTag = (RebuildTag)tag;
                            /*无效区域，不消费事件*/
                            if(rebuildTag.orientation == HORIZONTAL_FULL_SCREEN) {
                                if(posX < rebuildTag.extensionPaddingLeft) {
                                    return false;
                                }
                            } else if(rebuildTag.orientation == VERTICAL_FULL_SCREEN) {
                                if(posY < rebuildTag.extensionPaddingTop) {
                                    return false;
                                }
                            }
                        }

                    } else if(motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        /*必要时，需要处理click事件*/
                        view.performClick();
                    }
                    if(onTouchListener != null) {
                        return onTouchListener.onTouch(view, motionEvent);
                    }
                    return needRewriteTouchListener;
                }
            });
        }
    }

    /**
     * 通过反射获取触摸监听，然后进行劫持
     * */
    private static View.OnTouchListener getOnTouchListener(View contentView) {
        try{
            Class viewClazz = Class.forName("android.view.View");
            //事件监听器都是这个实例保存的
            Method listenerInfoMethod = viewClazz.getDeclaredMethod("getListenerInfo");
            if (!listenerInfoMethod.isAccessible()) {
                listenerInfoMethod.setAccessible(true);
            }
            Object listenerInfoObj = listenerInfoMethod.invoke(contentView);
            Class listenerInfoClazz = Class.forName("android.view.View$ListenerInfo");
            Field onTouchListenerField = listenerInfoClazz.getDeclaredField("mOnTouchListener");
            if(!onTouchListenerField.isAccessible()) {
                onTouchListenerField.setAccessible(true);
            }

            return (View.OnTouchListener) onTouchListenerField.get(listenerInfoObj);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
