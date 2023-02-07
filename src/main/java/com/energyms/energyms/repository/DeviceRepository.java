package com.energyms.energyms.repository;



import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.energyms.energyms.model.Device;


@Repository
public interface DeviceRepository extends JpaRepository<Device,Long> {

	Device findByDeviceId(String deviceId);

	@Query("SELECT p FROM Device p WHERE " +
            "p.deviceId LIKE CONCAT('%',:query, '%')" )
    List<Device> searchProducts(String query);
	
	@Query("select distinct t.deviceId from Device t")
	List<String> gettingAllDeviceId();
	
	@Query("SELECT t FROM Device t ORDER BY deviceId DESC")
	List<Device> findAllFromDb();
}
