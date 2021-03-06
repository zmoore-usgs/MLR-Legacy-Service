package gov.usgs.wma.mlrlegacy.validation;

import java.math.BigInteger;
import java.util.Arrays;
import javax.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gov.usgs.wma.mlrlegacy.dao.MonitoringLocationDao;
import gov.usgs.wma.mlrlegacy.model.MonitoringLocation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UniqueNormalizedStationNameValidatorTest {
	private MonitoringLocationDao dao;
	private ConstraintValidatorContext context;
	private UniqueNormalizedStationNameValidator instance;

	@BeforeEach
	public void setUp() {
		dao = mock(MonitoringLocationDao.class);
		context = ConstraintValidatorContextMockFactory.get();
		instance = new UniqueNormalizedStationNameValidator(dao);
	}
	
	@Test
	public void testMissingStationIxOnCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		ml.setStationIx(null);
		boolean valid = instance.isValid(ml, context);
		assertFalse(valid);
	}
	
	@Test
	public void testOneDuplicateFoundOnCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("123456789");
		ml.setStateFipsCode("55");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList(ml));
		boolean valid = instance.isValid(ml, context);
		assertFalse(valid);
	}
	
	@Test
	public void testManyDuplicatesFoundOnCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("123456789");
		ml.setStateFipsCode("55");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList(ml, ml, ml));
		boolean valid = instance.isValid(ml, context);
		assertFalse(valid);
	}
	
	@Test
	public void testNoDuplicateFoundOnCreate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("A");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList());
		boolean valid = instance.isValid(ml, context);
		assertTrue(valid);
	}
	
	@Test
	public void testMissingStationIxOnUpdate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setStationIx(null);
		boolean valid = instance.isValid(ml, context);
		assertTrue(valid);
	}
	
	@Test
	public void testOneMatchingMLFoundOnUpdate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setId(BigInteger.valueOf(1));
		ml.setTransactionType("M");
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList(ml));
		boolean valid = instance.isValid(ml, context);
		assertTrue(valid);
	}
	
	@Test
	public void testOneMatchingMLFoundOnUpdateInvalid() {
		MonitoringLocation mlUpd = new MonitoringLocation();
		MonitoringLocation mlFound = new MonitoringLocation();
		mlUpd.setId(BigInteger.valueOf(1));
		mlUpd.setTransactionType("M");
		mlUpd.setAgencyCode("USGS");
		mlUpd.setSiteNumber("1");
		mlUpd.setStationIx("WATERINGHOLE");
		
		mlFound.setId(BigInteger.valueOf(2));
		mlFound.setTransactionType("M");
		mlFound.setAgencyCode("USGS");
		mlFound.setSiteNumber("1");
		mlFound.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList(mlFound));
		boolean valid = instance.isValid(mlUpd, context);
		assertFalse(valid);
	}
	
	@Test
	public void testManyDuplicatesFoundOnUpdate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setAgencyCode("USGS");
		ml.setSiteNumber("1");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList(ml, ml));
		boolean valid = instance.isValid(ml, context);
		assertFalse(valid);
	}
	
	@Test
	public void testNoDuplicateFoundOnUpdate() {
		MonitoringLocation ml = new MonitoringLocation();
		ml.setTransactionType("M");
		ml.setStationIx("WATERINGHOLE");
		
		when(dao.getByNormalizedName(any())).thenReturn(Arrays.asList());
		boolean valid = instance.isValid(ml, context);
		assertTrue(valid);
	}
}
