package org.fiware.impress.repository;

import org.fiware.baas.model.MachineInfoVO;
import org.fiware.baas.model.MachineVO;
import org.fiware.broker.api.EntitiesApiClient;
import org.fiware.impress.configuration.GeneralProperties;
import org.fiware.impress.mapping.EntityMapper;
import org.fiware.impress.model.Machine;
import org.fiware.impress.model.MachineInfo;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MachineRepository extends BrokerBaseRepository {

	// the demo currently only supports machines of type crane. Can be extended in the future.
	private static final String MACHINE_TYPE = "Crane";

	public MachineRepository(GeneralProperties generalProperties, EntitiesApiClient entitiesApi, EntityMapper entityMapper) {
		super(generalProperties, entitiesApi, entityMapper);
	}

	public List<Machine> getMachinesByCoordinates(String lat, String longi, Double perimeter) {
		return entitiesApi.queryEntities(
						generalProperties.getTenant(),
						null,
						null,
						MACHINE_TYPE,
						null,
						null,
						String.format("near;maxDistance==%s", perimeter / 2),
						"Point",
						String.format("[%s,%s]", lat, longi),
						null,
						null,
						null,
						null,
						getLinkHeader()).body().stream()
				.map(entityMapper::entityVoToMachine)
				.collect(Collectors.toList());
	}

	public MachineInfo getMachineInfoByCoordinates(String lat, String longi, Double perimeter) {
		List<Machine> machines = getMachinesByCoordinates(lat, longi, perimeter);
		int machinesInUse = machines.stream().filter(Machine::inUse).collect(Collectors.counting()).intValue();
		int machinesAvailable = machines.size() - machinesInUse;

		Double averageAvailablity = 0d;
		Double averageUsage = 0d;
		if (machines.size() > 0) {
			averageAvailablity = machines.stream().map(Machine::averageUsage).collect(Collectors.summingDouble(Double::doubleValue)) / machines.size();
			averageUsage = machines.stream().map(Machine::averageAvailability).collect(Collectors.summingDouble(Double::doubleValue)) / machines.size();
		}
		return new MachineInfo(machinesAvailable, machinesInUse, averageUsage, averageAvailablity);
	}

	public Optional<Machine> getMachineById(String id) {
		return Optional.ofNullable(entityMapper
				.entityVoToMachine(
						entitiesApi.retrieveEntityById(generalProperties.getTenant(),
								URI.create(id),
								null,
								MACHINE_TYPE,
								null,
								getLinkHeader()).body()));
	}
}
