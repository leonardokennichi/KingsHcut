package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Service {
    private StringProperty serviceId;
    private StringProperty serviceTypeId;
    private StringProperty serviceName;
    private IntegerProperty servicePrice;
    private IntegerProperty serviceDuration;
	public Service(StringProperty serviceId, StringProperty serviceTypeId, StringProperty serviceName, IntegerProperty servicePrice, IntegerProperty serviceDuration) {
		super();
		this.serviceId = serviceId;
		this.serviceTypeId = serviceTypeId;
		this.serviceName = serviceName;
		this.servicePrice = servicePrice;
		this.serviceDuration = serviceDuration;
	}
	public StringProperty getServiceId() {
		return serviceId;
	}
	public void setServiceId(StringProperty serviceId) {
		this.serviceId = serviceId;
	}
	public StringProperty getServiceTypeId() {
		return serviceTypeId;
	}
	public void setServiceTypeId(StringProperty serviceTypeId) {
		this.serviceTypeId = serviceTypeId;
	}
	public StringProperty getServiceName() {
		return serviceName;
	}
	public void setServiceName(StringProperty serviceName) {
		this.serviceName = serviceName;
	}
	public IntegerProperty getServicePrice() {
		return servicePrice;
	}
	public void setServicePrice(IntegerProperty servicePrice) {
		this.servicePrice = servicePrice;
	}
	public IntegerProperty getServiceDuration() {
		return serviceDuration;
	}
	public void setServiceDuration(IntegerProperty serviceDuration) {
		this.serviceDuration = serviceDuration;
	}
    
    
    
}