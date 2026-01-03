package onlyajar.airboat.service;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import onlyajar.airboat.utils.AppUtils;

public final class ServiceManager {
    private static final String TAG = "ServiceManager";

    public static Map<Class<?>, ServiceInfo> SERVICES = new HashMap<>();

    private static CountDownLatch latch;
//    public static <T extends IInterface> void consumeService(Consumer<T> consumer) throws RemoteException{
//        System.out.println("consumeService: " +  consumer.getClass().getGenericInterfaces()[0]);
//
//        Type type = ((ParameterizedType) consumer.getClass().getGenericInterfaces()[0]).getActualTypeArguments()[0];
////        final String serviceName = getServiceName((Class<?>) type);
//        ServiceInfo serviceInfo = SERVICES.get(type);
//        if(serviceInfo == null){
//            throw new RemoteException("service info is null");
//        }
//        if(serviceInfo.getStatus() == ServiceStatus.UN_FIND){
//            throw new RemoteException("service is null");
//        }
//
//        if(serviceInfo.getInstance() == null){
//            new Thread(()->{
//                register(serviceInfo.getAction(), serviceInfo.getPackageName(), (Class<T>) type);
//            }).start();
//            latch = new CountDownLatch(1);
//            try {
//                latch.await(5, TimeUnit.SECONDS);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            latch = null;
//            if(serviceInfo.getInstance() == null) throw new RemoteException("服务绑定失败!!!");
//            else consumer.accept((T) serviceInfo.getInstance());
//        }else {
//            consumer.accept((T) serviceInfo.getInstance());
//        }
//    }

    public static <T extends IInterface> T getService(final Class<T> serviceClass) throws RemoteException{
        ServiceInfo serviceInfo = SERVICES.get(serviceClass);
        if(serviceInfo == null){
            throw new RemoteException("service info is null");
        }
        if(serviceInfo.getInstance() == null || serviceInfo.getStatus() != ServiceStatus.BIND){
            rebind(serviceInfo, serviceClass);
            throw new RemoteException(serviceClass.getName() + " is not bind, please retry!");
        }
        return (T) serviceInfo.getInstance();
    }


    public static<T extends IInterface>  void register(final String action, final String packageName, final Class<T> serviceClass){
        if(SERVICES.get(serviceClass) == null){
            ServiceInfo info = new ServiceInfo();
            info.setAction(action);
            info.setPackageName(packageName);
            SERVICES.put(serviceClass, info);
        }

        final ServiceInfo serviceInfo = SERVICES.get(serviceClass);

        if(serviceInfo.getStatus() == ServiceStatus.BIND || serviceInfo.getStatus() == ServiceStatus.BINDING){
            return;
        }
        serviceInfo.setStatus(ServiceStatus.BINDING);
        Intent intent = new Intent();
        intent.setAction(serviceInfo.getAction());
        intent.setPackage(serviceInfo.getPackageName());
        boolean flag = AppUtils.getApplication().bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, final IBinder service) {
                Log.e(TAG, "======onServiceConnected");
                serviceInfo.setStatus(ServiceStatus.BIND);
                serviceInfo.setInstance(asInterface(service, serviceClass));
                try {
                    service.linkToDeath(new IBinder.DeathRecipient() {
                        @Override
                        public void binderDied() {
                            Log.d(TAG, "-------------binder service is death!!!----------");
                            try {
                                Thread.sleep(3000);
                                serviceInfo.getInstance().asBinder().unlinkToDeath(this, 0);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            serviceInfo.setStatus(ServiceStatus.UNBIND);
                            serviceInfo.setInstance(null);
                            register(action, packageName, serviceClass);
                        }
                    } , 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                if(latch != null){
                    latch.countDown();
                }
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                serviceInfo.setStatus(ServiceStatus.UNBIND);
                Log.e(TAG, "======onServiceDisconnected");
            }
        }, Context.BIND_AUTO_CREATE);
        if (!flag) {
            serviceInfo.setStatus(ServiceStatus.UN_FIND);
            Log.i(TAG, "======服务绑定失败");
        } else {
            Log.i(TAG, "======服务绑定成功");
        }
    }


    private static <T extends IInterface> void rebind(ServiceInfo serviceInfo, Class<T> serviceClass){
        register(serviceInfo.getAction(), serviceInfo.getPackageName(), serviceClass);
    }

    private static IInterface asInterface(IBinder service, Class<?> serviceClass){
        Class<?>[] classes = serviceClass.getDeclaredClasses();
        Class<?> clazz = null;
        for (Class<?> c : classes
        ) {
            if(Objects.equals(c.getSimpleName(), "Stub")){
                clazz = c;
                break;
            }
        }
        if(clazz == null) return null;
        try {
            Method method = clazz.getMethod("asInterface", IBinder.class);
            method.setAccessible(true);
            return (IInterface) method.invoke(null, service);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
