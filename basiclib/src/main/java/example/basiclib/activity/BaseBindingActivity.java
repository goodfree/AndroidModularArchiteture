package example.basiclib.activity;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.qmuiteam.qmui.util.QMUIStatusBarHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import example.basiclib.R;
import example.basiclib.event.RefreshSkinEvent;
import example.basiclib.util.EventBusUtils;
import example.basiclib.util.statusbar.StatusbarHelper;
import skin.support.SkinCompatManager;
import skin.support.content.res.SkinCompatResources;


/**
 * Email yummyl.lau@gmail.com
 * Created by yummylau on 2018/01/25.2.
 */

public abstract class BaseBindingActivity<DB extends ViewDataBinding> extends AppCompatActivity {

    public DB dataBinding;
    private @ColorInt
    int statusBarColor;

    @LayoutRes
    public abstract int getLayoutRes();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SkinCompatManager.getInstance() == null) {
            statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary);
        } else {
            statusBarColor = SkinCompatResources.getColor(this,R.color.colorPrimary);
        }
        dataBinding = DataBindingUtil.setContentView(this, getLayoutRes());
        EventBusUtils.register(this);
        if (supportHandleStatusBar() && !QMUIStatusBarHelper.isFullScreen(this)) {
            handleStatusBar(getStatusBarColor());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBusUtils.unRegister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(RefreshSkinEvent refreshSkinEvent) {
        if (refreshSkinEvent.color != null) {
            statusBarColor = refreshSkinEvent.color;
            handleStatusBar(getStatusBarColor());
        }
    }

    protected boolean supportHandleStatusBar() {
        return true;
    }

    @ColorInt
    public int getStatusBarColor() {
        return statusBarColor;
    }

    public void handleStatusBar(@ColorInt int statusBarColor) {
        StatusbarHelper.setStatusBarColor(this, statusBarColor);
    }
}
