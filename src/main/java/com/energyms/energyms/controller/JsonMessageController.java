package com.energyms.energyms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.energyms.energyms.dto.DeviceDataDto;
import com.energyms.energyms.model.Appliance;
import com.energyms.energyms.model.Device;
import com.energyms.energyms.model.DeviceData1;
import com.energyms.energyms.model.User;
import com.energyms.energyms.repository.ApplianceRepository;
import com.energyms.energyms.repository.DeviceData1Repository;
import com.energyms.energyms.repository.DeviceRepository;
import com.energyms.energyms.repository.UserRepository;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

@RestController
@RequestMapping("/kafka")
public class JsonMessageController {

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ApplianceRepository applianceRepository;
	@Autowired
	private DeviceData1Repository deviceData1Repository;

	public ResponseEntity<?> publish(DeviceDataDto deviceDataDto, Principal principal) {
		deviceDataDto.setEmailId(principal.getName());
		User user = userRepository.findByEmailId(deviceDataDto.getEmailId())
				.orElseThrow(() -> new UsernameNotFoundException("Email not found"));
		if (user == null) {
			return new ResponseEntity<>("user doesnt exist", HttpStatus.NOT_FOUND);
		}

		String s1 = deviceDataDto.getDeviceId();

		double Consumption = 0;
		double priceValue = 0;
		double co2EmissionValue = 0;
		deviceDataDto.setTimestamp(LocalDateTime.now());
		deviceDataDto.setConsumption(Consumption);
		deviceDataDto.setPrice(priceValue);
		deviceDataDto.setCo2Emission(co2EmissionValue);
		deviceDataDto.setPricePerUnit(0.002);
		deviceDataDto.setCo2EmissionPerUnit(0.001);
		long time = LocalDateTime.now().getHour();
		Appliance appliance = applianceRepository.findByDeviceDeviceId(s1);
		Device device = deviceRepository.findByDeviceId(s1);
		deviceDataDto.setApplianceName(appliance.getApplianceName());
		deviceDataDto.setApplianceName(appliance.getApplianceName());
		if (device == null) {
			return new ResponseEntity<>("There is no existing device", HttpStatus.NOT_FOUND);
		}

		List<DeviceData1> deviceData2 = deviceData1Repository.findByDeviceId(deviceDataDto.getDeviceId());

		deviceDataDto.setTimestamp(LocalDateTime.now());

		if (deviceData2.size() == 0) {

			long totalMinutes = (deviceDataDto.getTimeInMinutes());// 1112
			long totalHours = (totalMinutes) / 60;// 18.5
			long totalDays = (totalMinutes) / 1440;
			if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() == deviceDataDto.getTimeWhenAppisOn().getMonth()
					&& deviceDataDto.getTimestamp().getDayOfMonth() == deviceDataDto.getTimeWhenAppisOn()
							.getDayOfMonth()
					&& deviceDataDto.getTimestamp().getHour() == deviceDataDto.getTimeWhenAppisOn().getHour()) {

				DeviceData1 deviceData1 = new DeviceData1();
				deviceData1.setUser(user);
				deviceData1.setDeviceId(deviceDataDto.getDeviceId());
				deviceData1.setTimestamp(LocalDateTime.now());
				deviceData1.setEventValue(deviceDataDto.getEventValue());
				deviceData1.setApplianceName(deviceDataDto.getApplianceName());
				deviceData1.setConsumption((deviceDataDto.getEventValue() * totalMinutes) / 1000);
				deviceData1.setPrice(deviceData1.getConsumption() * 2);
				deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
				deviceData1.setPricePerUnit(2);
				deviceData1.setCo2EmissionPerUnit(0.8);
				deviceData1.setTimeInMinutes(deviceDataDto.getTimeInMinutes());
				deviceData1.setTotalTimeInMinutes(deviceDataDto.getTimeInMinutes());
				deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData1.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData1.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData1.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData1.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData1.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData1Repository.save(deviceData1);
			} else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() == deviceDataDto.getTimeWhenAppisOn().getMonth()
					&& deviceDataDto.getTimestamp().getDayOfMonth() == deviceDataDto.getTimeWhenAppisOn()
							.getDayOfMonth()
					&& deviceDataDto.getTimestamp().getHour() != deviceDataDto.getTimeWhenAppisOn().getHour()) {

				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(60 - deviceDataDto.getTimeWhenAppisOn().getMinute());
				deviceData21.setConsumption(
						(deviceDataDto.getEventValue() * (60 - deviceDataDto.getTimeWhenAppisOn().getMinute())) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				int minu = 60 - deviceDataDto.getTimeWhenAppisOn().getMinute();
				long totalMinutes1 = totalMinutes - minu;
				long totalhours = totalMinutes1 / 60;
				for (int i = 1; i <= totalhours + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour() + i;
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth();
					int monthOfYear = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int year = deviceDataDto.getTimeWhenAppisOn().getYear();
					if (hour > 24) {

						if (dayOfMonth > 31) {

							monthOfYear = monthOfYear + 1;
							if (monthOfYear > 12) {
								year = year + 1;
								monthOfYear = monthOfYear - 12;
							}
							dayOfMonth = dayOfMonth - 31;
							hour = 1;
						}
						dayOfMonth = dayOfMonth + 1;
						hour = hour - 24;
					}
					long totalminutesPerHour = totalMinutes1;
					long min = 60;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60);
					if (total < 60) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setTotalTimeInMinutes(totalminutesPerHour);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth);
					deviceData1.setMonthOfYear(monthOfYear);
					deviceData1.setYear(year);
					deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
					deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
					deviceData1Repository.save(deviceData1);
				}
			} else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() == deviceDataDto.getTimeWhenAppisOn().getMonth()
					&& deviceDataDto.getTimestamp().getDayOfMonth() != deviceDataDto.getTimeWhenAppisOn()
							.getDayOfMonth()) {

				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;

				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int year = deviceDataDto.getTimeWhenAppisOn().getYear();
					if (dayOfMonth > 31) {
						monthOfYear = monthOfYear + 1;
						month = month.plus(1);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth);
					deviceData1.setMonthOfYear(monthOfYear);
					deviceData1.setYear(year);
					deviceData1.setMonth(month);
					deviceData1.setDayOfWeek(dayOfWeek);
					deviceData1Repository.save(deviceData1);
				}
			}

			else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() != deviceDataDto.getTimeWhenAppisOn().getMonth()) {
				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;
				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear1 = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int monthOfYear = monthOfYear1;
					int year1 = deviceDataDto.getTimeWhenAppisOn().getYear();
					int year = year1;
					if (dayOfMonth > 31) {
						monthOfYear = monthOfYear + (dayOfMonth / 31);

						month = month.plus(dayOfMonth / 31);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					int dayOfMonth2 = dayOfMonth;
					int monthOfYear2 = monthOfYear;
					int year2 = year;
					Month month2 = month;
					DayOfWeek dayOfWeek2 = dayOfWeek;
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth2);
					deviceData1.setMonthOfYear(monthOfYear2);
					deviceData1.setYear(year2);
					deviceData1.setMonth(month2);
					deviceData1.setDayOfWeek(dayOfWeek2);
					deviceData1Repository.save(deviceData1);
				}
			}

			else if (deviceDataDto.getTimestamp().getYear() != deviceDataDto.getTimeWhenAppisOn().getYear()) {

				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;

				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear1 = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int monthOfYear = monthOfYear1;
					int year1 = deviceDataDto.getTimeWhenAppisOn().getYear();
					int year = year1;
					if (dayOfMonth > 30) {// 32--11--1,60--
						monthOfYear = monthOfYear + (dayOfMonth / 31);// --31/31==1--62/31==2
						month = month.plus(dayOfMonth / 31);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31)) + 1;
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					int dayOfMonth2 = dayOfMonth;
					int monthOfYear2 = monthOfYear;
					int year2 = year;
					Month month2 = month;
					DayOfWeek dayOfWeek2 = dayOfWeek;
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth2);
					deviceData1.setMonthOfYear(monthOfYear2);
					deviceData1.setYear(year2);
					deviceData1.setMonth(month2);
					deviceData1.setDayOfWeek(dayOfWeek2);
					deviceData1Repository.save(deviceData1);
				}
			}

			return ResponseEntity.ok("Data has been saved");
		}

		else {
			DeviceData1 oldDeviceData = deviceData2.get(deviceData2.size() - 1);
			long totalMinutes = (deviceDataDto.getTimeInMinutes());// 1112
			long totalHours = (totalMinutes) / 60;// 18.5
			long totalDays = (totalMinutes) / 1440;
			if ((oldDeviceData.getTimestamp().getYear() == deviceDataDto.getTimestamp().getYear())
					&& (oldDeviceData.getTimestamp().getMonth() == deviceDataDto.getTimestamp().getMonth())
					&& (oldDeviceData.getTimestamp().getDayOfMonth() == deviceDataDto.getTimestamp().getDayOfMonth())
					&& (oldDeviceData.getTimestamp().getHour() == deviceDataDto.getTimestamp().getHour())) {
				
				if (oldDeviceData.getTimeInMinutes() <= 60) {
					oldDeviceData.setTimeInMinutes(deviceDataDto.getTimeInMinutes() + oldDeviceData.getTimeInMinutes());
					Consumption = ((deviceDataDto.getEventValue() * totalMinutes)
							+ (oldDeviceData.getConsumption() * 1000));
					oldDeviceData.setConsumption(Consumption / 1000);
					oldDeviceData.setTotalTimeInMinutes(totalMinutes);
					oldDeviceData.setPrice(oldDeviceData.getConsumption() * 2);
					oldDeviceData.setCo2Emission(oldDeviceData.getConsumption() * 0.8);
					oldDeviceData.setEventValue(deviceDataDto.getEventValue());
					oldDeviceData.setCo2EmissionPerUnit(2);
					oldDeviceData.setPricePerUnit(0.8);
					oldDeviceData.setTimestamp(LocalDateTime.now());
					oldDeviceData.setTimeInMinutes(deviceDataDto.getTimeInMinutes() + oldDeviceData.getTimeInMinutes());
					deviceData1Repository.save(oldDeviceData);
				} else {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * totalMinutes) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTimeInMinutes(deviceDataDto.getTimeInMinutes());
					deviceData1.setTotalTimeInMinutes(totalMinutes);
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					deviceData1.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
					deviceData1.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
					deviceData1.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
					deviceData1.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
					deviceData1.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
					deviceData1.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
					deviceData1Repository.save(deviceData1);
				}
			}
			// 
			else if (((oldDeviceData.getTimestamp().getYear() == deviceDataDto.getTimestamp().getYear())
					&& (oldDeviceData.getTimestamp().getMonth() == deviceDataDto.getTimestamp().getMonth())
					&& (oldDeviceData.getTimestamp().getDayOfMonth() != deviceDataDto.getTimestamp().getDayOfMonth()))

					|| (((oldDeviceData.getTimestamp().getYear() == deviceDataDto.getTimestamp().getYear())
							&& (oldDeviceData.getTimestamp().getMonth() == deviceDataDto.getTimestamp().getMonth())
							&& (oldDeviceData.getTimestamp().getDayOfMonth() == deviceDataDto.getTimestamp()
									.getDayOfMonth())
							&& (oldDeviceData.getTimestamp().getHour() != deviceDataDto.getTimestamp().getHour())))) {

				DeviceData1 deviceData1 = new DeviceData1();
				deviceData1.setUser(user);
				deviceData1.setDeviceId(deviceDataDto.getDeviceId());
				deviceData1.setTimestamp(LocalDateTime.now());
				deviceData1.setEventValue(deviceDataDto.getEventValue());
				deviceData1.setApplianceName(deviceDataDto.getApplianceName());
				deviceData1.setConsumption((deviceDataDto.getEventValue() * totalMinutes) / 1000);
				deviceData1.setPrice(deviceData1.getConsumption() * 2);
				deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
				deviceData1.setPricePerUnit(2);
				deviceData1.setCo2EmissionPerUnit(0.8);
				deviceData1.setTimeInMinutes(deviceDataDto.getTimeInMinutes());
				deviceData1.setTotalTimeInMinutes(totalMinutes);
				deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData1.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData1.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData1.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData1.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData1.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData1.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData1);
			}

			
			else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() == deviceDataDto.getTimeWhenAppisOn().getMonth()
					&& deviceDataDto.getTimestamp().getDayOfMonth() == deviceDataDto.getTimeWhenAppisOn()
							.getDayOfMonth()
					&& deviceDataDto.getTimestamp().getHour() != deviceDataDto.getTimeWhenAppisOn().getHour()) {
				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(60 - deviceDataDto.getTimeWhenAppisOn().getMinute());
				deviceData21.setConsumption(
						(deviceDataDto.getEventValue() * (60 - deviceDataDto.getTimeWhenAppisOn().getMinute())) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				int minu = 60 - deviceDataDto.getTimeWhenAppisOn().getMinute();
				long totalMinutes1 = totalMinutes - minu;
				long totalhours = totalMinutes1 / 60;
				for (int i = 1; i <= totalhours + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour() + i;
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth();
					int monthOfYear = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek();
					int year = deviceDataDto.getTimeWhenAppisOn().getYear();
					if (hour > 24) {

						if (dayOfMonth > 31) {

							monthOfYear = monthOfYear + 1;
							month = month.plus(1);
							if (monthOfYear > 12) {
								year = year + 1;
								monthOfYear = monthOfYear - 12;
							}
							dayOfMonth = dayOfMonth - 31;
							hour = 1;
						}
						dayOfMonth = dayOfMonth + 1;
						dayOfWeek = dayOfWeek.plus(1);
						hour = hour - 24;
					}
					long totalminutesPerHour = totalMinutes1;
					long min = 60;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60);
					if (total < 60) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setTotalTimeInMinutes(totalminutesPerHour);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth);
					deviceData1.setMonthOfYear(monthOfYear);
					deviceData1.setYear(year);
					deviceData1.setDayOfWeek(dayOfWeek);
					deviceData1.setMonth(month);
					deviceData1Repository.save(deviceData1);
				}
			}

			else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() == deviceDataDto.getTimeWhenAppisOn().getMonth()
					&& deviceDataDto.getTimestamp().getDayOfMonth() != deviceDataDto.getTimeWhenAppisOn()
							.getDayOfMonth()) {

				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;

				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int year = deviceDataDto.getTimeWhenAppisOn().getYear();
					if (dayOfMonth > 31) {
						monthOfYear = monthOfYear + 1;
						month = month.plus(1);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth);
					deviceData1.setMonthOfYear(monthOfYear);
					deviceData1.setYear(year);
					deviceData1.setMonth(month);
					deviceData1.setDayOfWeek(dayOfWeek);
					deviceData1Repository.save(deviceData1);
				}

			} else if (deviceDataDto.getTimestamp().getYear() == deviceDataDto.getTimeWhenAppisOn().getYear()
					&& deviceDataDto.getTimestamp().getMonth() != deviceDataDto.getTimeWhenAppisOn().getMonth()) {

				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;
				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear1 = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int monthOfYear = monthOfYear1;
					int year1 = deviceDataDto.getTimeWhenAppisOn().getYear();
					int year = year1;
					if (dayOfMonth > 31) {
						monthOfYear = monthOfYear + (dayOfMonth / 31);

						month = month.plus(dayOfMonth / 31);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					int dayOfMonth2 = dayOfMonth;
					int monthOfYear2 = monthOfYear;
					int year2 = year;
					Month month2 = month;
					DayOfWeek dayOfWeek2 = dayOfWeek;
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth2);
					deviceData1.setMonthOfYear(monthOfYear2);
					deviceData1.setYear(year2);
					deviceData1.setMonth(month2);
					deviceData1.setDayOfWeek(dayOfWeek2);
					deviceData1Repository.save(deviceData1);
				}
			}

			else if (deviceDataDto.getTimestamp().getYear() != deviceDataDto.getTimeWhenAppisOn().getYear()) {

				LocalDateTime time1 = deviceDataDto.getTimeWhenAppisOn();
				int minute1 = 60 - time1.getMinute();
				int hour1 = 23 - time1.getHour();
				int day1 = time1.getDayOfMonth();
				LocalDateTime time2 = LocalDateTime.now();
				int minute2 = time2.getMinute();
				int hour2 = time2.getHour();
				long totalminuteslastday = (hour2 * 60) + minute2;

				long totalminuteson1stday = (hour1 * 60) + minute1;

				DeviceData1 deviceData21 = new DeviceData1();
				deviceData21.setUser(user);
				deviceData21.setDeviceId(deviceDataDto.getDeviceId());
				deviceData21.setTimestamp(LocalDateTime.now());
				deviceData21.setEventValue(deviceDataDto.getEventValue());
				deviceData21.setApplianceName(deviceDataDto.getApplianceName());
				deviceData21.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
				deviceData21.setTimeInMinutes(totalminuteson1stday);
				deviceData21.setConsumption((deviceDataDto.getEventValue() * totalminuteson1stday) / 1000);
				deviceData21.setPrice(deviceData21.getConsumption() * 2);
				deviceData21.setCo2Emission(deviceData21.getConsumption() * 0.8);
				deviceData21.setPricePerUnit(2);
				deviceData21.setCo2EmissionPerUnit(0.8);
				deviceData21.setTotalTimeInMinutes(totalMinutes);
				deviceData21.setHour(deviceDataDto.getTimeWhenAppisOn().getHour());
				deviceData21.setDayOfMonth(deviceDataDto.getTimeWhenAppisOn().getDayOfMonth());
				deviceData21.setMonthOfYear(deviceDataDto.getTimeWhenAppisOn().getMonthValue());
				deviceData21.setYear(deviceDataDto.getTimeWhenAppisOn().getYear());
				deviceData21.setDayOfWeek(deviceDataDto.getTimeWhenAppisOn().getDayOfWeek());
				deviceData21.setMonth(deviceDataDto.getTimeWhenAppisOn().getMonth());
				deviceData1Repository.save(deviceData21);
				long totalMinutes1 = totalMinutes - totalminuteson1stday;
				long totalhours = totalMinutes1 / 60;
				long totalDays1 = totalMinutes1 / 1440;
				for (int i = 1; i <= totalDays1 + 1; i++) {
					DeviceData1 deviceData1 = new DeviceData1();
					deviceData1.setUser(user);
					deviceData1.setDeviceId(deviceDataDto.getDeviceId());
					deviceData1.setTimestamp(LocalDateTime.now());
					deviceData1.setEventValue(deviceDataDto.getEventValue());
					deviceData1.setApplianceName(deviceDataDto.getApplianceName());
					deviceData1.setConsumption((deviceDataDto.getEventValue() * deviceDataDto.getTimeInMinutes()));
					deviceData1.setTimeWhenAppisOn(deviceDataDto.getTimeWhenAppisOn());
					int hour = deviceDataDto.getTimeWhenAppisOn().getHour();
					int dayOfMonth = deviceDataDto.getTimeWhenAppisOn().getDayOfMonth() + i;
					DayOfWeek dayOfWeek = deviceDataDto.getTimeWhenAppisOn().getDayOfWeek().plus(i);
					Month month = deviceDataDto.getTimeWhenAppisOn().getMonth();
					int monthOfYear1 = deviceDataDto.getTimeWhenAppisOn().getMonthValue();
					int monthOfYear = monthOfYear1;
					int year1 = deviceDataDto.getTimeWhenAppisOn().getYear();
					int year = year1;
					if (dayOfMonth > 30) {
						monthOfYear = monthOfYear + (dayOfMonth / 31);
						month = month.plus(1);
						dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						if (monthOfYear > 12) {
							year = year + 1;
							monthOfYear = monthOfYear - 12;
							dayOfMonth = dayOfMonth - (31 * (dayOfMonth / 31));
						}
					}
					int dayOfMonth2 = dayOfMonth;
					int monthOfYear2 = monthOfYear;
					int year2 = year;
					Month month2 = month;
					DayOfWeek dayOfWeek2 = dayOfWeek;
					long totalminutesPerHour = totalMinutes1;
					long min = 60 * 24;
					long total = totalminutesPerHour;
					total = total - ((i - 1) * 60 * 24);
					if (total < 60 * 24) {
						min = total;
					}
					deviceData1.setTimeInMinutes(min);
					deviceData1.setConsumption((deviceDataDto.getEventValue() * min) / 1000);
					deviceData1.setPrice(deviceData1.getConsumption() * 2);
					deviceData1.setCo2Emission(deviceData1.getConsumption() * 0.8);
					deviceData1.setPricePerUnit(2);
					deviceData1.setCo2EmissionPerUnit(0.8);
					deviceData1.setTotalTimeInMinutes(total);
					deviceData1.setHour(hour);
					deviceData1.setDayOfMonth(dayOfMonth2);
					deviceData1.setMonthOfYear(monthOfYear2);
					deviceData1.setYear(year2);
					deviceData1.setMonth(month2);
					deviceData1.setDayOfWeek(dayOfWeek2);
					deviceData1Repository.save(deviceData1);
				}
			}

			return ResponseEntity.ok("Data has been saved");
		}
	}
}
