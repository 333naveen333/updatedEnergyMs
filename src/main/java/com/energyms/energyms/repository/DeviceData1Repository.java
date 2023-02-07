package com.energyms.energyms.repository;



import org.springframework.data.jpa.repository.JpaRepository;

import com.energyms.energyms.model.DeviceData1;
import com.energyms.energyms.model.User;

import java.util.List;

public interface DeviceData1Repository extends JpaRepository<DeviceData1,Integer> {
    

    List<DeviceData1> findByDeviceId(String deviceId);
    
    List<DeviceData1> findBydayOfMonth(int today);

   
    List<DeviceData1> findByDeviceIdAndApplianceNameAndUser(String deviceId, String applianceName, User user);

   

    List<DeviceData1> findByDeviceIdAndApplianceNameAndUserAndDayOfMonth(String deviceId, String applianceName, User user1, int today);

    List<DeviceData1> findByDeviceIdAndApplianceNameAndUserAndMonthOfYear(String deviceId, String applianceName, User user1, int month);

   

    List<DeviceData1> findByDeviceIdAndApplianceNameAndUserAndMonthOfYearAndYear(String deviceId, String applianceName, User user1, int i, int year);

    List<DeviceData1> findByDeviceIdAndApplianceNameAndUserAndDayOfMonthAndYearAndMonthOfYear(String deviceId, String applianceName, User user1, int i, int year, int month);

   List< DeviceData1> findByDeviceIdAndApplianceNameAndUserAndDayOfMonthAndYearAndMonthOfYearAndHour(String deviceId, String applianceName, User user1, int today, int year, int month, int i);
List<DeviceData1> findByDeviceIdAndApplianceNameAndUserAndMonthOfYearAndDayOfMonth(String deviceId,
		String applianceName, User user1, int month, int j);

   
}
