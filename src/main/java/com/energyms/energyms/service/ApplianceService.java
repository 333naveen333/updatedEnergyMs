package com.energyms.energyms.service;

import java.security.Principal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.energyms.energyms.controller.JsonMessageController;
//import com.energymoni.energymoni.dto.DeviceDataDto;
import com.energyms.energyms.dto.ApplianceDto;
import com.energyms.energyms.dto.DeviceDataDto;
import com.energyms.energyms.kafka.DeviceKafkaProducer;
import com.energyms.energyms.model.Appliance;
import com.energyms.energyms.model.DataStats;
import com.energyms.energyms.model.Device;
import com.energyms.energyms.model.DeviceData;
import com.energyms.energyms.model.DeviceDataKafkaMessage;
import com.energyms.energyms.model.Room;
import com.energyms.energyms.model.RoomData;
import com.energyms.energyms.model.StatsDiff;
import com.energyms.energyms.model.User;
import com.energyms.energyms.repository.ApplianceRepository;
import com.energyms.energyms.repository.DeviceDataRepository;
import com.energyms.energyms.repository.DeviceRepository;
import com.energyms.energyms.repository.RoomRepository;
import com.energyms.energyms.repository.UserRepository;

@Service
public class ApplianceService {

	@Autowired
	private ApplianceRepository applianceRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoomRepository roomRepository;

	@Autowired
	private DeviceRepository deviceRepository;
	@Autowired
    private DeviceKafkaProducer deviceKafkaProducer;
	@Autowired
	private DeviceDataRepository deviceDataRepository;
	@Autowired
	private JsonMessageController jsonMessageController;
	
	
	public Appliance save(ApplianceDto applianceDto, String roomName, String deviceId) {
		User user = userRepository.findByEmailId(applianceDto.getUserEmailId())
				.orElseThrow(() -> new UsernameNotFoundException(" email not found" + applianceDto.getUserEmailId()));

		Room room = roomRepository.findByRoomNameAndUserEmailId(roomName, applianceDto.getUserEmailId());
		
		Device device = deviceRepository.findByDeviceId(deviceId);

		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		
		String time=formatDate.format(date);
		
		
		Appliance appliance = new Appliance(applianceDto.getApplianceName(), applianceDto.isApplianceStatus(), user,
				room, device,time);
		
		return applianceRepository.save(appliance);

	}

	public Optional<Room> findByRoomId(long id) {
		return roomRepository.findById(id);
	}

	public List<Appliance> getAllAppliances(String roomName, Principal principal) {
		User user = userRepository.findByEmailId(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException(" email not found" + principal.getName()));

		return applianceRepository.findByRoomRoomName(roomName, user);

	}

	public boolean deleteAppliance(String roomName, String applianceName, Principal principal) {
		User user = userRepository.findByEmailId(principal.getName())
				.orElseThrow(() -> new UsernameNotFoundException(" email not found" + principal.getName()));
		Appliance appliance = applianceRepository.findByApplianceNameAndRoomRoomNameAndUser(applianceName, roomName,user);
		
		if(appliance!=null)
		{
			applianceRepository.delete(appliance);
			
			return true;
		}
		else
			return false;		
	}
	

	public String applianceStatusChange(String applianceName, String roomName, Principal principal) {
		
		Appliance appliance = applianceRepository.findByApplianceNameAndRoomRoomNameAndUserEmailId(applianceName,
				roomName, principal.getName());

		if (appliance != null) {
			if (appliance.isApplianceStatus()) {
				
				appliance.setApplianceStatus(false);
				SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");

				Date date = new Date();

				formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
				
				String time=formatDate.format(date);
				String day1=time.substring(0, 2);
				String day2=appliance.getApplianceStatusChangingTime().substring(0, 2);
				DeviceDataKafkaMessage deviceDataKafkaMessage =new DeviceDataKafkaMessage();
				
				deviceDataKafkaMessage.setTopic("devicedata_topic-"+appliance.getDevice().getDeviceId());
				String t1=time.substring(0, 14);
				String t2=appliance.getApplianceStatusChangingTime().substring(0, 14);
				if(t1.equals(t2) )
				{
				deviceDataKafkaMessage.setPayload(Long.parseLong(time.substring(15,17))-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(15, 17)));
				}
				else
				{
					
					long hrsDiff=Long.parseLong(t1.substring(12, 14))-Long.parseLong(t2.substring(12, 14));
					long daysDiff=Long.parseLong(day1)-Long.parseLong(day2);
					long monthsDiff=0;
					
					if(Long.parseLong(time.substring(3, 5))-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(3, 5))<0)
					{
						monthsDiff=(Long.parseLong(time.substring(3, 5))-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(3, 5))+12);
					}
					else
					{
						 monthsDiff=(Long.parseLong(time.substring(3, 5))-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(3, 5)));
					}
					if(Long.parseLong(time.substring(3, 5))>Long.parseLong(appliance.getApplianceStatusChangingTime().substring(3, 5))&&(Long.parseLong(time.substring(0, 2))<Long.parseLong(appliance.getApplianceStatusChangingTime().substring(0, 2))))
					{
						daysDiff=Long.parseLong(time.substring(0, 2))+31-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(0, 2));
						monthsDiff=(Long.parseLong(time.substring(3, 5))-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(3, 5)));
						monthsDiff-=1;
					}
						
					deviceDataKafkaMessage.setPayload(Long.parseLong(time.substring(15,17))+(((monthsDiff)*30*24*60)+(daysDiff*24*60)+(60*hrsDiff)-Long.parseLong(appliance.getApplianceStatusChangingTime().substring(15,17))));
				}
				appliance.setApplianceStatusChangingTime(formatDate.format(date));
				applianceRepository.save(appliance);
				deviceKafkaProducer.sendMessage(deviceDataKafkaMessage);
				
				
				
				return appliance.getApplianceName()+" turned OFF";
				
			} else {
				appliance.setApplianceStatus(true);
				SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy  HH:mm:ss ");

				Date date = new Date();

				formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
				appliance.setApplianceStatusChangingTime(formatDate.format(date));
				applianceRepository.save(appliance);
				return appliance.getApplianceName()+" turned ON";
			}
		
		}

		else {
			return "unable to change appliance status.... please check the data" ;
		}
	}

	public DataStats getTotalStats(Principal principal)
	{
		
		List<DeviceData> dataList=deviceDataRepository.findByUserId(principal.getName());
		List<Appliance> applianceOnList=applianceRepository.findByApplianceStatusAndUserEmailId(true, principal.getName());
		List<Appliance> applianceOffList=applianceRepository.findByApplianceStatusAndUserEmailId(false, principal.getName());
		long totalConsumption=0;
		long totalPrice=0;
		double totalCarbon=0;
		for(DeviceData i:dataList)
		{
			totalConsumption+=i.getPowerConsumption();
			totalPrice+=i.getCost();
			totalCarbon+=i.getCarbonEmission();
		}
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		
		DataStats d= new DataStats();
		d.setUserId(principal.getName());
		d.setByThisDate(formatDate.format(date));
	  d.setTotalPowerConsumed(totalConsumption);
	  d.setTotalCost(totalPrice);
	  d.setTotalCarbon(totalCarbon);
	  d.setNoOfAppliancesOn(applianceOnList.size());
	  d.setNoOfAppliancesOff(applianceOffList.size());
	
	  return d;

		
	}
	
	
	
	public List<RoomData> getEachRoomDataPerDay(Principal principal)
	{
		
	
		List<Room> allRooms=roomRepository.findByUserEmailId(principal.getName());
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		
		String time=formatDate.format(date);
		List<RoomData> roomDataList=new ArrayList<>();
		
		for(Room i: allRooms)
		{
			List<Appliance> l1=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(true, principal.getName(), i.getRoomName());
			List<Appliance> l2=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(false, principal.getName(), i.getRoomName());
			List<DeviceData> l3=deviceDataRepository.findByUserIdAndRoomName(principal.getName(), i.getRoomName());
			double k=0;
			
			for(DeviceData j:l3)
			{
				if(time.equals(j.getEventTime().substring(0, 10)))
				k=k+j.getPowerConsumption();
				
			}
			
			RoomData r=new RoomData();
			r.setRoomName(i.getRoomName());
			
			r.setNoOfAppliancesOn(l1.size());
			r.setNoOfAppliancesOff(l2.size());
			r.setEmailId(principal.getName());
			r.setTodayPowerConsumed((double)Math.round(k * 100d) / 100d);
			
			
			roomDataList.add(r);
			
		}
	
		return roomDataList;
		
		
	
	}

	public DataStats getSevenDaysStat(Principal principal) {
		
		
		
		List<DeviceData> dataList=deviceDataRepository.findByUserId(principal.getName());
		List<Appliance> applianceOnList=applianceRepository.findByApplianceStatusAndUserEmailId(true, principal.getName());
		List<Appliance> applianceOffList=applianceRepository.findByApplianceStatusAndUserEmailId(false, principal.getName());
		double totalConsumption=0;
		double totalPrice=0;
		double totalCarbon=0;
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		String time=formatDate.format(date);
		
		for(DeviceData i:dataList)
		{

			 if(Long.parseLong(time.substring(0, 2))<7)
				 {
				 if ((Long.parseLong(i.getEventTime().substring(0, 2))>31-7+Long.parseLong(time.substring(0, 2)))&&Long.parseLong(time.substring(3, 5))-1 ==(Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(i.getEventTime().substring(6, 10)))))
				 {
					
					 totalConsumption+=i.getPowerConsumption();
						totalPrice+=i.getCost();
						totalCarbon+=i.getCarbonEmission();
				 }
				if(Long.parseLong(time.substring(0, 2))==(Long.parseLong(i.getEventTime().substring(0, 2)))&&(Long.parseLong(time.substring(3, 5))==(Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(i.getEventTime().substring(6, 10))))))
						{

					 totalConsumption+=i.getPowerConsumption();
						totalPrice+=i.getCost();
						totalCarbon+=i.getCarbonEmission();
						}
				if(Long.parseLong(i.getEventTime().substring(0, 2))<Long.parseLong(time.substring(0, 2))&&(Long.parseLong(time.substring(3, 5))==(Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(i.getEventTime().substring(6, 10))))))
				{
					totalConsumption+=i.getPowerConsumption();
					totalPrice+=i.getCost();
					totalCarbon+=i.getCarbonEmission();	
				}
				 }
			 else
			 {
				 if((Long.parseLong(time.substring(0, 2))-7) <Long.parseLong(i.getEventTime().substring(0, 2))&&(Long.parseLong(time.substring(3, 5))==(Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(i.getEventTime().substring(6, 10))))))
				 {
					 totalConsumption+=i.getPowerConsumption();
					totalPrice+=i.getCost();
					totalCarbon+=i.getCarbonEmission();
				 }
			 }
			
		}

		
		
		
		DataStats d= new DataStats();
		d.setUserId(principal.getName());
		d.setByThisDate("Stats From Last Seven Days");
	  d.setTotalPowerConsumed((double)Math.round(totalConsumption * 100d) / 100d);
	  d.setTotalCost((double)Math.round(totalPrice * 100d) / 100d);
	  d.setTotalCarbon((double)Math.round(totalCarbon * 100d) / 100d);//(double)Math.round(totalCarbon1 * 1000d) / 1000d
	  d.setNoOfAppliancesOn(applianceOnList.size());
	  d.setNoOfAppliancesOff(applianceOffList.size());
	  
	
	  return d;

	 
		
	}

	public List<RoomData> getSevenDaysRoomData(Principal principal) {
		
		List<Room> allRooms=roomRepository.findByUserEmailId(principal.getName());
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		
		String time=formatDate.format(date);
		List<RoomData> roomDataList=new ArrayList<>();
		for(Room i: allRooms)
		{
			List<Appliance> l1=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(true, principal.getName(), i.getRoomName());
			List<Appliance> l2=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(false, principal.getName(), i.getRoomName());
			List<DeviceData> l3=deviceDataRepository.findByUserIdAndRoomName(principal.getName(), i.getRoomName());
			double k=0;
			
			for(DeviceData j:l3)
			{
				
				 if(Long.parseLong(time.substring(0, 2))<7)
					 {
					 if ((Long.parseLong(j.getEventTime().substring(0, 2))>31-7+Long.parseLong(time.substring(0, 2)))&&Long.parseLong(time.substring(3, 5))-1 ==(Long.parseLong(j.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(j.getEventTime().substring(6, 10)))))
					 {
						 k=k+j.getPowerConsumption();
					 }
					if(Long.parseLong(time.substring(0, 2))==(Long.parseLong(j.getEventTime().substring(0, 2)))&&(Long.parseLong(time.substring(3, 5))==(Long.parseLong(j.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(j.getEventTime().substring(6, 10))))))
							{
						 k=k+j.getPowerConsumption();
							}
					 }
				 else
				 {
					 if((Long.parseLong(time.substring(0, 2))-7) <Long.parseLong(j.getEventTime().substring(0, 2))&&(Long.parseLong(time.substring(3, 5))==(Long.parseLong(j.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==(Long.parseLong(j.getEventTime().substring(6, 10))))))//(Long.parseLong(time.substring(0, 2))-7) <Long.parseLong(i.getEventTime().substring(0, 2))
							k=k+j.getPowerConsumption();
				 }
			}
			RoomData r=new RoomData();
			r.setRoomName(i.getRoomName());
			
			r.setNoOfAppliancesOn(l1.size());
			r.setNoOfAppliancesOff(l2.size());
			r.setEmailId(principal.getName());
			r.setTodayPowerConsumed((double)Math.round(k * 10d) / 10d);
			roomDataList.add(r);
			
			
		}
	return roomDataList;
	
		
	
	}
	
	public List<RoomData> getThisMonthRoomData(Principal principal) {
		
		List<Room> allRooms=roomRepository.findByUserEmailId(principal.getName());
		
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		
		String time=formatDate.format(date);
		List<RoomData> roomDataList=new ArrayList<>();
		for(Room i: allRooms)
		{
			List<Appliance> l1=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(true, principal.getName(), i.getRoomName());
			List<Appliance> l2=applianceRepository.findByApplianceStatusAndUserEmailIdAndRoomRoomName(false, principal.getName(), i.getRoomName());
			List<DeviceData> l3=deviceDataRepository.findByUserIdAndRoomName(principal.getName(), i.getRoomName());
			double k=0;
			
			for(DeviceData j:l3)
			{
				if((Long.parseLong(time.substring(3, 5))==Long.parseLong(j.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==Long.parseLong(j.getEventTime().substring(6, 10))))
				{
					k=k+j.getPowerConsumption();
				}
			}
			RoomData r=new RoomData();
			r.setRoomName(i.getRoomName());
			
			r.setNoOfAppliancesOn(l1.size());
			r.setNoOfAppliancesOff(l2.size());
			r.setEmailId(principal.getName());
			r.setTodayPowerConsumed(k);
			roomDataList.add(r);
			
			
		}
	return roomDataList;
		
		
	
	}

	public DataStats getDataStatForThisMonth(Principal principal) {
		

		List<DeviceData> dataList=deviceDataRepository.findByUserId(principal.getName());
		List<Appliance> applianceOnList=applianceRepository.findByApplianceStatusAndUserEmailId(true, principal.getName());
		List<Appliance> applianceOffList=applianceRepository.findByApplianceStatusAndUserEmailId(false, principal.getName());
		double totalConsumption=0;
		double totalPrice=0;
		double totalCarbon=0;
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		String time=formatDate.format(date);
		
		for(DeviceData i:dataList)
		{
			if((Long.parseLong(time.substring(3, 5))==Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==Long.parseLong(i.getEventTime().substring(6, 10))))
			{
			totalConsumption+=i.getPowerConsumption();
			totalPrice+=i.getCost();
			totalCarbon+=i.getCarbonEmission();
			}
		}
		
		
		DataStats d= new DataStats();
		d.setUserId(principal.getName());
		d.setByThisDate("Stats For This Current Month");
	  d.setTotalPowerConsumed(totalConsumption);
	  d.setTotalCost(totalPrice);
	  d.setTotalCarbon(totalCarbon);
	  d.setNoOfAppliancesOn(applianceOnList.size());
	  d.setNoOfAppliancesOff(applianceOffList.size());
	  
	
	  return d;

	}

	public StatsDiff getStatsDiff(Principal principal) {
		
		List<DeviceData> dataList=deviceDataRepository.findByUserId(principal.getName());
	
		double totalConsumption1=0;
		double totalPrice1=0;
		double totalCarbon1=0;
		double totalConsumption2=0;
		double totalPrice2=0;
		double totalCarbon2=0;
		SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

		Date date = new Date();

		formatDate.setTimeZone(TimeZone.getTimeZone("IST"));
		String time=formatDate.format(date);
		
		for(DeviceData i:dataList)
		{
			if((Long.parseLong(time.substring(3, 5))==Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==Long.parseLong(i.getEventTime().substring(6, 10))))
			{
			totalConsumption1+=i.getPowerConsumption();
			totalPrice1+=i.getCost();
			totalCarbon1+=i.getCarbonEmission();
			}
			if((Long.parseLong(time.substring(3, 5))==01) && Long.parseLong(i.getEventTime().substring(3, 5))==12 &&(Long.parseLong(time.substring(6, 10))-1==Long.parseLong(i.getEventTime().substring(6, 10))))
					{
				totalConsumption2+=i.getPowerConsumption();
				totalPrice2+=i.getCost();
				totalCarbon2+=i.getCarbonEmission();       
					}
			if((Long.parseLong(time.substring(3, 5))-1==Long.parseLong(i.getEventTime().substring(3, 5)))&&(Long.parseLong(time.substring(6, 10))==Long.parseLong(i.getEventTime().substring(6, 10))))
			{
				totalConsumption2+=i.getPowerConsumption();
				totalPrice2+=i.getCost();
				totalCarbon2+=i.getCarbonEmission();
			}
		}
		double totalConsumptionDiff=totalConsumption1-totalConsumption2;
		double totalPriceDiff=totalPrice1-totalPrice2;
		double totalCarbonDiff=totalCarbon1-totalCarbon2;
		DecimalFormat df = new DecimalFormat("#.##");

		StatsDiff s=new StatsDiff();
		s.setThisMonthPowerConsumed((double)Math.round(totalConsumption1 * 100d) / 100d);
		s.setLastMonthPowerConsumed((double)Math.round(totalConsumption2 * 100d) / 100d);
		s.setPowerDifference((double)Math.round(totalConsumptionDiff * 100d) / 100d);
		s.setThisMonthCost((double)Math.round(totalPrice1 * 100d) / 100d);
		s.setLastMonthCost((double)Math.round(totalPrice2 * 100d) / 100d);
		s.setCostDifference((double)Math.round(totalPriceDiff * 100d) / 100d);
		s.setThisMonthCarbon((double)Math.round(totalCarbon1 * 100d) / 100d);
		s.setLastMonthCarbon((double)Math.round(totalCarbon2 * 100d) / 100d);
		s.setCarbonDifference(Double. parseDouble( df.format(totalCarbonDiff)));
		return s;
	
	}







	

public void changeApplianceStatus(String applianceName,String roomName,Principal principal) {
    User user=userRepository.findByEmailId(principal.getName()).orElseThrow(()->
            new UsernameNotFoundException("Email not found"));
    Appliance appliance=applianceRepository.findByApplianceNameAndRoomRoomNameAndUser(applianceName,roomName,user);
    if(appliance.isApplianceStatus()==true){
      //  appliance.setApplianceStatus(false);
        DeviceDataDto deviceDataDto=new DeviceDataDto();
        deviceDataDto.setDeviceId(appliance.getDevice().getDeviceId());
        String strDate = appliance.getApplianceStatusChangingTime(); 
        // parse the date into date time 
        String strDate1=strDate.substring(6, 10)+"-"+strDate.substring(3, 5)+"-"+strDate.substring(0, 2)+"T"+strDate.substring(12,strDate.length()-1)+".000000";
        LocalDateTime date = LocalDateTime.parse(strDate1);
        //2023-01-25 17:52:16.887471
        deviceDataDto.setTimeWhenAppisOn(date);
        deviceDataDto.setEventValue(1);
        LocalDateTime timeAtTrue=date;//appliance.getApplianceStatusChangingTime();
    
        
        LocalDateTime timeAtFalse=LocalDateTime.now();
        long minutes=0;
//        int[] arrayjan = new int[]{ 1,3,5,7,8,10,12 };
//        int arrayfeb=2;
//        int[] arrayapril=new int[]{4,6,9,11};
        //true-today 11.00,today false-13.10
        if((timeAtFalse.getYear()==timeAtTrue.getYear())&&(timeAtFalse.getMonth()==timeAtTrue.getMonth())&&(timeAtFalse.getDayOfMonth()==timeAtTrue.getDayOfMonth())&&(timeAtFalse.getHour()==timeAtTrue.getHour())){
            minutes=timeAtFalse.getMinute()-timeAtTrue.getMinute();
        }
        else if((timeAtFalse.getYear()==timeAtTrue.getYear())&&(timeAtFalse.getMonth()==timeAtTrue.getMonth())&&(timeAtFalse.getDayOfMonth()==timeAtTrue.getDayOfMonth())&&(timeAtFalse.getHour()!=timeAtTrue.getHour())) {
            minutes = ((timeAtFalse.getHour() * 60) + timeAtFalse.getMinute()) - ((timeAtTrue.getHour() * 60) + timeAtTrue.getMinute());
        }
   
        else if((timeAtFalse.getYear()==timeAtTrue.getYear())&&(timeAtFalse.getMonth()==timeAtTrue.getMonth())&&(timeAtFalse.getDayOfMonth()!=timeAtTrue.getDayOfMonth())){
            //minutes= (timeAtFalse.getDayOfMonth()-timeAtTrue.getDayOfMonth())*1440+(timeAtFalse.getMinute()-timeAtTrue.getMinute()) ;
            minutes=(((timeAtFalse.getDayOfMonth()*1440)+(timeAtFalse.getHour()*60)+(timeAtFalse.getMinute()))-((timeAtTrue.getDayOfMonth()*1440)+(timeAtTrue.getHour()*60)+(timeAtTrue.getMinute())));
        }
       
        else if(((timeAtFalse.getYear()==timeAtTrue.getYear()))&&(timeAtFalse.getMonth()!=timeAtTrue.getMonth()))
        {
        	  minutes=(((timeAtFalse.getMonthValue()*(31*1440))+(timeAtFalse.getDayOfMonth()*1440)+(timeAtFalse.getHour()*60)
                      +(timeAtFalse.getMinute())))-(((timeAtTrue.getMonthValue()*(31*1440))+(timeAtTrue.getDayOfMonth()*1440)+
                      (timeAtTrue.getHour()*60)+(timeAtTrue.getMinute())));

        } else if ((timeAtFalse.getYear()!=timeAtTrue.getYear())) {

            int yearfalse=timeAtFalse.getYear();
            String temp = Integer.toString(yearfalse);
            int[] arr = new int[temp.length()];
            for (int i = 0; i < temp.length(); i++) {
                arr[i] = temp.charAt(i) - '0';
            }
            int[] anotherArray = new int[arr.length - 1];
            for (int i = 0, k = 0; i < arr.length; i++) {
                if (i == 0) {
                    continue;
                }
                anotherArray[k++] = arr[i];
            }
            int[] anotherArray1 = new int[anotherArray.length - 1];
            for (int i = 0, k = 0; i < anotherArray.length; i++) {
                if (i == 0) {
                    continue;
                }
                anotherArray1[k++] = anotherArray[i];
            }

            int res=0;
            for(int i=0;i<anotherArray1.length;i++) {
                res=res*10+anotherArray1[i];
            }
            int resultAtFalse=res;

            int yearTrue=timeAtTrue.getYear();
            String temp1 = Integer.toString(yearTrue);
            int[] arr1 = new int[temp1.length()];
            for (int i = 0; i < temp1.length(); i++) {
                arr1[i] = temp1.charAt(i) - '0';
            }
            int[] anotherArray2 = new int[arr1.length - 1];
            for (int i = 0, k = 0; i < arr1.length; i++) {
                if (i == 0) {
                    continue;
                }
                anotherArray2[k++] = arr1[i];
            }
            int[] anotherArray3 = new int[anotherArray2.length - 1];
            for (int i = 0, k = 0; i < anotherArray2.length; i++) {
                if (i == 0) {
                    continue;
                }
                anotherArray3[k++] = anotherArray2[i];
            }

            int res1=0;
            for(int i=0;i<anotherArray3.length;i++) {
                res1=res1*10+anotherArray3[i];
            }
            //10-11-2022 10:45 to 27-01-2023 10:15
            int resultAtTrue=res1;
            if(timeAtFalse.getMonthValue()<timeAtTrue.getMonthValue()){
                int a=timeAtFalse.getMonthValue();
                a=a+(resultAtFalse-resultAtTrue)*12;
                minutes=((a*(31*1440)+(timeAtFalse.getDayOfMonth()*1440)+(timeAtFalse.getHour()*60)
                        +(timeAtFalse.getMinute())))-(((timeAtTrue.getMonthValue()*(31*1440))+(timeAtTrue.getDayOfMonth()*1440)+
                        (timeAtTrue.getHour()*60)+(timeAtTrue.getMinute())));
            }
            else{
                minutes=((resultAtFalse*525600)+((timeAtFalse.getMonthValue()*(31*1440))+(timeAtFalse.getDayOfMonth()*1440)+(timeAtFalse.getHour()*60)
                        +(timeAtFalse.getMinute())))-((resultAtTrue*525600)+((timeAtTrue.getMonthValue()*(31*1440))+(timeAtTrue.getDayOfMonth()*1440)+
                        (timeAtTrue.getHour()*60)+(timeAtTrue.getMinute())));
            }


        }




        deviceDataDto.setTimeInMinutes(minutes);
        double eventValue=1;
        double priceValuePerUnit=0.05;
        double co2EmissionValuePerUnit=0.001;
        deviceDataDto.setEventValue(eventValue);
        deviceDataDto.setPricePerUnit(priceValuePerUnit);
        deviceDataDto.setCo2EmissionPerUnit(co2EmissionValuePerUnit);
        jsonMessageController.publish(deviceDataDto,principal);

    }

}
}





