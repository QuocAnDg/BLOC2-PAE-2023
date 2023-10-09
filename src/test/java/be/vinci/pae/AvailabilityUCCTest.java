package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.AvailabilityUCC;
import be.vinci.pae.domain.AvailabilityUCCImpl;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.services.AvailabilityDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Singleton;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class AvailabilityUCCTest {

  private static AvailabilityDAO availabilityMock;
  private static DALServices dalServicesMock;
  private static ServiceLocator locator;
  private AvailabilityUCC availabilityUCC;
  private DomainFactory domainFactory;

  @BeforeAll
  static void setupBeforeAll() {
    availabilityMock = Mockito.mock(AvailabilityDAO.class);
    dalServicesMock = Mockito.mock(DALServices.class);
    AbstractBinder testApplicationBinder = new ApplicationBinder() {
      @Override
      protected void configure() {
        bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
        bind(availabilityMock).to(AvailabilityDAO.class);
        bind(dalServicesMock).to(DALServices.class);
        bind(AvailabilityUCCImpl.class).to(AvailabilityUCC.class).in(Singleton.class);
      }
    };
    locator = ServiceLocatorUtilities.bind(testApplicationBinder);
  }

  @BeforeEach
  void setup() {
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commit();
    Mockito.doNothing().when(dalServicesMock).rollback();
    availabilityUCC = locator.getService(AvailabilityUCC.class);
    domainFactory = locator.getService(DomainFactory.class);
  }

  @AfterEach
  void tearDown() {
    Mockito.reset(availabilityMock);
    Mockito.reset(dalServicesMock);
  }

  @Test
  @DisplayName("Test getAllAvailabilitiesDates() with one date")
  void testAllAvailabilitiesDates() {
    LocalDate date1 = LocalDate.now();
    List<LocalDate> availabilitiesDates = new ArrayList<>();
    availabilitiesDates.add(date1);
    Mockito.when(availabilityMock.getAllAvailabilitiesDates()).thenReturn(availabilitiesDates);
    List<LocalDate> availabilitiesDatesReturned = availabilityUCC.getAllAvailabilitiesDates();
    assertAll(
        () -> assertEquals(1, availabilitiesDatesReturned.size())
    );
  }

  @Test
  @DisplayName("Test getAllAvailabilitiesDates() no elements")
  void testAllAvailabilitiesDatesWithNoDates() {
    List<LocalDate> availabilitiesDates = new ArrayList<>();
    Mockito.when(availabilityMock.getAllAvailabilitiesDates()).thenReturn(availabilitiesDates);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> availabilityUCC.getAllAvailabilitiesDates());
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test getAllAvailabilitiesDates() Exception")
  void testAllAvailabilitiesDatesException() {
    Mockito.when(availabilityMock.getAllAvailabilitiesDates())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      availabilityUCC.getAllAvailabilitiesDates();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test addAvailability")
  void testAddAvailability() {
    String dateToAdd = "2024-01-01";
    AvailabilityDTO availabilityToReturn = domainFactory.getAvailabilityDTO();

    availabilityToReturn.setDate(LocalDate.parse(dateToAdd));
    Mockito.when(availabilityMock.addAvailabilityDate(dateToAdd)).thenReturn(availabilityToReturn);
    AvailabilityDTO availabilityReturned = availabilityUCC.addAvailability(dateToAdd);
    assertAll(
        () -> assertEquals(availabilityReturned, availabilityToReturn)
    );
  }

  @Test
  @DisplayName("Test addAvailability Exception")
  void testAddAvailabilityException() {
    String dateToAdd = "2024-01-01";
    Mockito.when(availabilityMock.addAvailabilityDate(dateToAdd))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));
    assertThrows(FatalException.class, () -> {
      availabilityUCC.addAvailability(dateToAdd);
    });
  }

  @Test
  @DisplayName("Test addAvailability Exception BIZ")
  void testAddAvailabilityExceptionBIZ() {
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> availabilityUCC.addAvailability(null));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test getIdOfOneAvailabilityDate with existing date")
  void testGetIdOfOneAvailabilityDate() {
    String date = "2023-04-29";
    AvailabilityDTO availabilityDTO = domainFactory.getAvailabilityDTO();
    availabilityDTO.setDate(LocalDate.parse(date));
    availabilityDTO.setId(1);
    Mockito.when(availabilityMock.getIdOfAvailabilityDate(availabilityDTO.getDate()))
            .thenReturn(availabilityDTO.getId());
    int availabilityIdReturned = availabilityUCC
            .getIdOfOneAvailabilityDate(availabilityDTO.getDate());
    assertAll(
            () -> assertEquals(1, availabilityIdReturned)
    );
  }


  @Test
  @DisplayName("Test getIdOfOneAvailabilityDate Exception (FatalException)")
  void testGetIdOfOneAvailabilityDateException() {
    String date = "2000-01-01";
    Mockito.when(availabilityMock.getIdOfAvailabilityDate(LocalDate.parse(date)))
            .thenThrow(new FatalException(
                    new SQLException("A problem was occurred in the server")));
    assertThrows(FatalException.class, () -> {
      availabilityUCC.getIdOfOneAvailabilityDate(LocalDate.parse(date));
    });
  }

  @Test
  @DisplayName("Test getIdOfOneAvailabilityDate Exception BIZ (date not found)")
  void testGetIdOfOneAvailabilityDateExceptionBIZ() {
    String date = "2000-04-04";
    Mockito.when(availabilityMock.getIdOfAvailabilityDate(LocalDate.parse(date))).thenReturn(-1);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
            () -> availabilityUCC.getIdOfOneAvailabilityDate(LocalDate.parse(date)));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(ElementNotFoundException.class, cause);
  }
}