package com.minggo.pluto.db.manager;

import android.content.Context;

import com.minggo.pluto.Pluto;
import com.minggo.pluto.common.AppContext;
import com.minggo.pluto.db.orm.FinalDb;
import com.minggo.pluto.util.LogUtils;
import com.minggo.pluto.util.PlutoFileCache;
import com.minggo.pluto.util.SharePreferenceUtils;

/**
 * Created by minggo on 2017/2/21.
 */

public class DataManagerProxy{

    public static final String TAG = "DATA_MANAGER";

    public enum DataType{
        SQLITE,FILECACHE,SHAREPREFERENCE
    }

    private static DataManager dataManagerStub;

    private static DataManagerProxy dataManagerProxy;

    private DataManagerProxy(){

    }
    public static DataManagerProxy getInstance(DataType type){
        if (dataManagerProxy == null){
            synchronized (DataManagerProxy.class){
                if (dataManagerProxy == null){
                    dataManagerProxy = new DataManagerProxy();
                }
            }
        }
        getDataManager(type);
        return dataManagerProxy;
    }

    private static DataManager getDataManager(DataType type){
        switch (type){
            case SQLITE:
                dataManagerStub = FinalDb.create(AppContext.getInstance().context);
                break;
            case FILECACHE:
                dataManagerStub = PlutoFileCache.getInstance();
                break;
            case SHAREPREFERENCE:
                dataManagerStub = SharePreferenceUtils.getInstance();
                break;
            default:
        }

        return dataManagerStub;
    }

    public void saveData(Object key, Object object) {
        dataManagerStub.saveData(key,object);
    }

    public <T> T queryData(Object key,Class<T> clazz) {
        return dataManagerStub.queryData(key,clazz);
    }

    public <T> void deleteData(Object key,Class<T> clazz) {
        dataManagerStub.deleteData(key,clazz);
    }

    public void updateData(Object key, Object object) {
        dataManagerStub.updateData(key,object);
    }

    /**
     * 适配finaldb的没有就保存有就更新
     * @param object
     * @return
     */
    public boolean saveOrUpdate(Object object){
        if (dataManagerStub instanceof FinalDb){
            ((FinalDb) dataManagerStub).saveOrUpdate(object);
            return true;
        }else {
            LogUtils.info(TAG,"this methord just can be used when DataManager is FinalDb's instance");
            return false;
        }
    }

    /**
     * 是否失效文件
     * @param key
     * @param min
     * @return
     */
    public boolean isExpiredFile(String key,int min){
        if (dataManagerStub instanceof PlutoFileCache){
            return ((PlutoFileCache) dataManagerStub).isCacheDataFailure(key,min);
        }else {
            LogUtils.info(TAG,"this methord just can be used when DataManager is PlutoFileCache instance");
            return false;
        }
    }

    /**
     * 适配SharePreference 指定文件保存数据
     * @param name xml文件名字
     * @param key 关键key
     * @param data 保存数据
     * @return
     */
    public boolean saveByNameAndKey(String name,String key,Object data){
        if (dataManagerStub instanceof SharePreferenceUtils){
            if (data instanceof Integer){
                ((SharePreferenceUtils) dataManagerStub).putInt(name,key,(int)data);
            }else if (data instanceof String){
                ((SharePreferenceUtils) dataManagerStub).putString(name,key,data.toString());
            }else if (data instanceof Boolean){
                ((SharePreferenceUtils) dataManagerStub).putBoolean(name,key,(boolean)data);
            }

            return true;
        }else {
            LogUtils.info(TAG,"this methord just can be used when DataManager is SharePreferenceUtils instance");
            return false;
        }
    }

    /**
     * 适配SharePreference 查询指定文件数据
     * @param name xml文件名字
     * @param key 关键key
     * @param <T> 查询类型
     * @return
     */
    public <T> T queryByNameAndKey(String name,String key){

        T t = null;
        if (dataManagerStub instanceof SharePreferenceUtils){
            if (t instanceof Integer){

                t =  (T) Integer.valueOf (((SharePreferenceUtils) dataManagerStub).getInt(name,key,0));
            }else if (t instanceof String){
                t = (T) ((SharePreferenceUtils) dataManagerStub).getString(name,key);
            }else if (t instanceof Boolean){
                t = (T) Boolean.valueOf(((SharePreferenceUtils) dataManagerStub).getBoolean(name,key));
            }
            return t;
        }else {
            LogUtils.info(TAG,"this methord just can be used when DataManager is SharePreferenceUtils instance");
            return null;
        }
    }
}
