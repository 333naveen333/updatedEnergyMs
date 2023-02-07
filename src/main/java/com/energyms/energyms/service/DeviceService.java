package com.energyms.energyms.service;


import java.util.ArrayList;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;


import com.energyms.energyms.model.Appliance;
import com.energyms.energyms.model.Device;

import com.energyms.energyms.repository.ApplianceRepository;
import com.energyms.energyms.repository.DeviceRepository;



@Service
public class DeviceService  {

	@Autowired
	 private DeviceRepository deviceRepository;
	@Autowired
	public ApplianceRepository applianceRepository;
	
	public DeviceService(DeviceRepository deviceRepository) {
		
		this.deviceRepository = deviceRepository;
	}

	


	public List<Device> getDevices() {
		// TODO Auto-generated method stub
		return deviceRepository.findAll();
	}

	public List<Device>  getActiveDevices(){
		List<Device> allDevices=new ArrayList<Device>();
		allDevices= deviceRepository.findAll();
		List<Appliance> appliances=new ArrayList<Appliance>();
		appliances=applianceRepository.findAll();
		List<Device> usedDevices=new ArrayList<Device>();
		for(Device d : allDevices)
		{
			int c=0;
			for(Appliance i:appliances)
			{
				
			if(d==i.getDevice())
			{
				c+=1;
			}
		}
			if(c!=0)
			{
				usedDevices.add(d);
			}
	}
		return usedDevices;
		
	}



	public List<Device> getInAvtiveDevices() {
		// TODO Auto-generated method stub
		List<Device> allDevices=new ArrayList<Device>();
		allDevices= deviceRepository.findAll();
		List<Appliance> appliances=new ArrayList<Appliance>();
		appliances=applianceRepository.findAll();
		List<Device> unUsedDevices=new ArrayList<Device>();
		
			for(Device d : allDevices)
			{
				int c=0;
				for(Appliance i:appliances)
				{
					
				if(d!=i.getDevice())
				{
					c+=1;
				}
			}
				if(c==appliances.size())
				{
					unUsedDevices.add(d);
				}
		}
			return unUsedDevices;
	}

	 public Page<Device> findProductsWithPagination(int offset,int pageSize){
		 Pageable pageable=PageRequest.of(offset, pageSize);
	        Page<Device> devices =  deviceRepository.findAll(pageable);
	        return  devices;//.getContent();
	    }

	 public List<Device> searchProducts(String query) {
	        List<Device> devices = deviceRepository.searchProducts(query);
	        return devices;
	    }
	 
	 
	 public Page<Device> findProductsWithPaginationAndSort(int offset,int pageSize,String field){
		
		 Pageable pageable=PageRequest.of(offset, pageSize).withSort(Sort.by(field));
	        Page<Device> devices =  deviceRepository.findAll(pageable);
        return  devices;
	    }
}
