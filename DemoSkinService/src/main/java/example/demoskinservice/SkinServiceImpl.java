package example.demoskinservice;

import android.app.Application;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import example.basiclib.activity.ActivityEvent;
import example.basiclib.event.RefreshSkinEvent;
import example.basiclib.util.EventBusUtils;
import example.componentlib.service.skin.ISkinService;
import example.componentlib.service.skin.Skin;
import skin.support.SkinCompatManager;
import skin.support.app.SkinCardViewInflater;
import skin.support.design.app.SkinMaterialViewInflater;

/**
 * demo provide blue skin
 * Email yummyl.lau@gmail.com
 * Created by yummylau on 2018/01/25.
 */

public class SkinServiceImpl implements ISkinService {

    private Map<String, Skin> mSkinTypeMap;

    @Override
    public void createAsLibrary(Application application) {
        mSkinTypeMap = new HashMap<>();
        mSkinTypeMap.put(Constants.DEFAULT_KEY, new Skin(Constants.DEFAULT_KEY, null, Color.parseColor("#00BCD4")));
        mSkinTypeMap.put(Constants.BULE_GREY_KEY, new Skin(Constants.BULE_GREY_KEY, "blueGrey.skin", Color.parseColor("#607D8B")));
        mSkinTypeMap.put(Constants.GREEN_KEY, new Skin(Constants.GREEN_KEY, "green.skin", Color.parseColor("#4CAF50")));
        mSkinTypeMap.put(Constants.PICK_KEY, new Skin(Constants.PICK_KEY, "pick.skin", Color.parseColor("#E91E63")));

        SkinCompatManager.withoutActivity(application)                  // 基础控件换肤初始化
                .addInflater(new SkinMaterialViewInflater())            // material design 控件换肤初始化[可选]
                .addInflater(new SkinCardViewInflater())                // CardView v7 控件换肤初始化[可选]
                .setSkinStatusBarColorEnable(false)                      // 关闭状态栏换肤，默认打开[可选]
                .setSkinWindowBackgroundEnable(false)                   // 关闭windowBackground换肤，默认打开[可选]
                .loadSkin();
    }

    @Override
    public void changeSkin(@NonNull final Skin skin) {
        if (skin != null) {
            if (skin.key.equals(Constants.DEFAULT_KEY)) {
                SkinCompatManager.getInstance().restoreDefaultTheme();
                EventBusUtils.post(new RefreshSkinEvent(skin.color));
            } else {
                SkinCompatManager.getInstance().loadSkin(skin.sourceName, new SkinCompatManager.SkinLoaderListener() {
                    @Override
                    public void onStart() {
                        Log.d(Constants.LOG_TAG, "start load skin: " + skin.key);
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(Constants.LOG_TAG, "load skin " + skin.key + " success!");
                        EventBusUtils.post(new RefreshSkinEvent(skin.color));
                    }

                    @Override
                    public void onFailed(String errMsg) {
                        Log.d(Constants.LOG_TAG, "load skin " + skin.key + " fail!");
                        if (!TextUtils.isEmpty(errMsg)) {
                            Log.d(Constants.LOG_TAG, errMsg);
                        }
                    }
                }, SkinCompatManager.SKIN_LOADER_STRATEGY_ASSETS);
            }
        }
    }

    @Nullable
    @Override
    public Skin[] getSkins() {
        Skin[] result = null;
        Skin defaultSkin = null;
        if (mSkinTypeMap != null) {
            Set<String> keySet = mSkinTypeMap.keySet();
            result = new Skin[keySet.size()];
            int index = 0;
            for (String key : keySet) {
                if (!key.equals(Constants.DEFAULT_KEY)) {
                    result[index++] = mSkinTypeMap.get(key);
                } else {
                    defaultSkin = mSkinTypeMap.get(key);
                }
            }
            result[index] = defaultSkin;
        }
        return result;
    }

    @Override
    public void release() {
        mSkinTypeMap.clear();
    }
}
