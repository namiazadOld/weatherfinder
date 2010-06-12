package com.sa.mwa;

import com.sa.mwa.INotifyTemperatureChanged;

interface IPeerRemoteService 
{
	float retrieveTemparature();
	float retrieveHumidity();
	void registerCallBack(INotifyTemperatureChanged ntc);
	void unregisterCallBack(INotifyTemperatureChanged ntc);
}
