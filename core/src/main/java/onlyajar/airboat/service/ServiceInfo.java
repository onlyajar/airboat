package onlyajar.airboat.service;

import android.os.IInterface;

class ServiceInfo{
    private IInterface instance;

    private ServiceStatus status = ServiceStatus.UNBIND;

    private String action;

    private String packageName;


    public IInterface getInstance() {
        return instance;
    }

    public void setInstance(IInterface instance) {
        this.instance = instance;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
