package com.creaty.photonwar.inferface;

import com.creaty.photonwar.entity.Ship;
import com.creaty.photonwar.entity.ShipGroup;
import com.creaty.photonwar.entity.StrongHold;

public interface EntityManager {
	public ShipGroup GetGroupInstance(StrongHold request);

	public void FreeGroupInstance(ShipGroup item);

	public Ship GetShipInstance();

	public void FreeShipInstance(Ship item);

}
