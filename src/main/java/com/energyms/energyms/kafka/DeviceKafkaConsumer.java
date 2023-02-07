package com.energyms.energyms.kafka;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.energyms.energyms.model.Appliance;
import com.energyms.energyms.model.DeviceData;
import com.energyms.energyms.model.DeviceDataKafkaMessage;
import com.energyms.energyms.repository.ApplianceRepository;
import com.energyms.energyms.repository.DeviceDataRepository;

@Service
public class DeviceKafkaConsumer {
	
	@Autowired
	private DeviceDataRepository deviceDataRepository;

	@Autowired
	private ApplianceRepository applianceRepository;

	@KafkaListener(topics = "devicedata_topic") 
	public void consume(DeviceDataKafkaMessage deviceDataKafkaMessage) {
	

	
		String[] s = deviceDataKafkaMessage.getTopic().split("-", 2);
		String deviceId = s[1];
		List<DeviceData> deviceDataIdList = deviceDataRepository.findByDeviceId(deviceId);
	

		if (deviceDataIdList.isEmpty()) {
			DeviceData data = new DeviceData();
			data.setDeviceId(deviceId);
			data.setPowerConsumption(deviceDataKafkaMessage.getPayload()/1000.0);
			Appliance appliance = applianceRepository.findByDeviceDeviceId(deviceId);
			data.setUserId(appliance.getUser().getEmailId());
			data.setRoomName(appliance.getRoom().getRoomName());

			SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");

			Date date = new Date();

			formatDate.setTimeZone(TimeZone.getTimeZone("IST"));

			data.setEventTime(formatDate.format(date));
            data.setApplianceName(appliance.getApplianceName());
            data.setCost(data.getPowerConsumption()*2.0);
            data.setCarbonEmission(data.getPowerConsumption()*0.8);
			deviceDataRepository.save(data);
		}
		else {
			
			DeviceData deviceDataId=deviceDataIdList.get(deviceDataIdList.size()-1);
			
			Appliance appliance = applianceRepository.findByDeviceDeviceId(deviceId);
			deviceDataId.setApplianceName(appliance.getApplianceName());
		String oldTime = deviceDataId.getEventTime();
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));

		String newTime=formatDate.format(date);
		if(oldTime.charAt(13)==newTime.charAt(13) && oldTime.charAt(0)==oldTime.charAt(0) && oldTime.charAt(1)==oldTime.charAt(1))
		{
			deviceDataId.setEventTime(newTime);
			deviceDataId.setPowerConsumption((deviceDataKafkaMessage.getPayload()+deviceDataId.getPowerConsumption()*1000)/1000.0);
			
			deviceDataId.setCost(deviceDataId.getPowerConsumption()*2.0);
			deviceDataId.setCarbonEmission(deviceDataId.getPowerConsumption()*0.8);
			deviceDataRepository.save(deviceDataId);
			
		}
		else
		{
			DeviceData data = new DeviceData();
			data.setDeviceId(deviceId);
			data.setPowerConsumption(deviceDataKafkaMessage.getPayload()/1000.0);
			
			data.setUserId(appliance.getUser().getEmailId());
			data.setRoomName(appliance.getRoom().getRoomName());

			

			formatDate.setTimeZone(TimeZone.getTimeZone("IST"));

			data.setEventTime(formatDate.format(date));
          data.setApplianceName(appliance.getApplianceName());
          data.setCost(data.getPowerConsumption()*2.0);
          data.setCarbonEmission(data.getPowerConsumption()*0.8);
			deviceDataRepository.save(data);
			
		}
		}

	}
	
}




