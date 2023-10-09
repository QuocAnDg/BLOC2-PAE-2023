package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.PhotoUCC;
import be.vinci.pae.domain.PhotoUCCImpl;
import be.vinci.pae.services.PhotoDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Singleton;
import java.io.InputStream;
import java.sql.SQLException;
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

class PhotoUCCTest {

  private static PhotoDAO photoDAOmock;
  private static DALServices dalServicesMock;
  private static ServiceLocator locator;
  private PhotoUCC photoUCC;
  private DomainFactory domainFactory;
  private PhotoDTO photoCreated;

  @BeforeAll
  static void setupBeforeAll() {
    Config.load("dev.properties");

    photoDAOmock = mock(PhotoDAO.class);
    dalServicesMock = mock(DALServices.class);
    AbstractBinder testApplicationBinder = new ApplicationBinder() {
      @Override
      protected void configure() {
        bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
        bind(photoDAOmock).to(PhotoDAO.class);
        bind(dalServicesMock).to(DALServices.class);
        bind(PhotoUCCImpl.class).to(PhotoUCC.class).in(Singleton.class);
      }
    };
    locator = ServiceLocatorUtilities.bind(testApplicationBinder);
  }

  @BeforeEach
  void setup() {
    this.photoUCC = locator.getService(PhotoUCC.class);
    this.domainFactory = locator.getService(DomainFactory.class);
  }

  @AfterEach
  void tearDown() {
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commit();
    Mockito.doNothing().when(dalServicesMock).rollback();
    Mockito.reset(photoDAOmock);
    Mockito.reset(dalServicesMock);
  }

  @Test
  @DisplayName("Test add photo, assets folder created if not existing")
  void testAddPhoto() {
    photoCreated = domainFactory.getPhotoDTO();
    photoCreated.setType("picture");
    photoCreated.setAccessPath("example.jpg");
    photoCreated.setId(5);
    when(photoDAOmock.setPhoto(any(), any())).thenReturn(photoCreated);

    PhotoDTO photo = photoUCC.addPhoto(InputStream.nullInputStream(), "example.jpg", "picture");

    assertAll(
        () -> assertEquals(photo.getType(), "picture"),
        () -> assertEquals(photo.getId(), 5),
        () -> assertEquals(photo.getAccessPath(), "example.jpg")
    );

  }

  @Test
  @DisplayName("Test add photo, assets folder already existing because of first test ")
  void testAddPhoto2() {
    photoCreated = domainFactory.getPhotoDTO();
    photoCreated.setType("picture");
    photoCreated.setAccessPath("example.jpg");
    photoCreated.setId(5);
    when(photoDAOmock.setPhoto(any(), any())).thenReturn(photoCreated);

    PhotoDTO photo = photoUCC.addPhoto(InputStream.nullInputStream(), "example.jpg", "picture");

    assertAll(
        () -> assertEquals(photo.getType(), "picture"),
        () -> assertEquals(photo.getId(), 5),
        () -> assertEquals(photo.getAccessPath(), "example.jpg")
    );

  }

  @Test
  @DisplayName("Test add photo with DAO error")
  void testAddPhotoWithDAOError() {
    photoCreated = domainFactory.getPhotoDTO();
    photoCreated.setType("picture");
    photoCreated.setAccessPath("example.jpg");
    photoCreated.setId(5);
    when(photoDAOmock.setPhoto(any(), any())).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      photoUCC.addPhoto(InputStream.nullInputStream(), "example.jpg", "picture");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(UnauthorizedException.class, cause)
    );

  }

  @Test
  @DisplayName("Test addPhoto method when PhotoDAO setPhoto method throws FatalException")
  void testAddPhotoFatalException() {

    Mockito.when(photoDAOmock.setPhoto(any(), any()))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      photoUCC.addPhoto(InputStream.nullInputStream(), "example.jpg", "picture");
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test getAllAvatars method with empty photo list")
  public void testGetAllAvatarsWithEmptyList() {

    List<PhotoDTO> listAvatars;
    List<PhotoDTO> emptyList = new ArrayList<>();
    Mockito.when(photoDAOmock.getAllAvatars()).thenReturn(emptyList);

    listAvatars = photoDAOmock.getAllAvatars();

    assertTrue(listAvatars.isEmpty());
  }

  @Test
  @DisplayName("Test getAllAvatars method with one photo in list")
  public void testGetAllAvatarsWithListHavingOnePhoto() {

    List<PhotoDTO> list = new ArrayList<>();
    PhotoDTO photoDTO = domainFactory.getPhotoDTO();
    list.add(photoDTO);

    Mockito.when(photoDAOmock.getAllAvatars()).thenReturn(list);
    List<PhotoDTO> receivedList = photoUCC.getAllAvatars();

    assertNotNull(receivedList);
  }

  @Test
  @DisplayName("Test getAllAvatars method when PhotoDAO getAllAvatars throws FatalException")
  public void testGetAllAvatarsExceptions() {

    Mockito.when(photoDAOmock.getAllAvatars())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      photoUCC.getAllAvatars();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test copyAvatar method, assets folder created if not existing")
  void testCopyAvatarNoExistingFolder() {
    photoCreated = domainFactory.getPhotoDTO();
    photoCreated.setType("picture");
    photoCreated.setAccessPath("bazz.jpg");
    photoCreated.setId(5);
    when(photoDAOmock.setPhoto(any(), any())).thenReturn(photoCreated);

    PhotoDTO photo = photoUCC.copyAvatar("bazz.jpg");

    assertAll(
        () -> assertEquals(photo.getType(), "picture"),
        () -> assertEquals(photo.getId(), 5),
        () -> assertEquals(photo.getAccessPath(), "bazz.jpg")
    );
  }

  @Test
  @DisplayName("Test copyAvatar method, assets folder already existing because of first test ")
  void testCopyAvatarExistingFolder() {
    photoCreated = domainFactory.getPhotoDTO();
    photoCreated.setType("picture");
    photoCreated.setAccessPath("bazz.jpg");
    photoCreated.setId(5);
    when(photoDAOmock.setPhoto(any(), any())).thenReturn(photoCreated);

    PhotoDTO photo = photoUCC.copyAvatar("bazz.jpg");

    assertAll(
        () -> assertEquals(photo.getType(), "picture"),
        () -> assertEquals(photo.getId(), 5),
        () -> assertEquals(photo.getAccessPath(), "bazz.jpg")
    );
  }

  @Test
  @DisplayName("Test add photo with DAO error")
  void testCopyAvatarWithDAOError() {

    when(photoDAOmock.setPhoto(any(), any())).thenReturn(null);

    IllegalBusinessException parentException = assertThrows(IllegalBusinessException.class, () -> {
      photoUCC.copyAvatar("bazz.jpg");
    });
    Throwable cause = parentException.getCause();

    assertAll(
        () -> assertInstanceOf(UnauthorizedException.class, cause)
    );
  }

  @Test
  @DisplayName("Test copyAvatar method when PhotoDAO setPhoto throws FatalException")
  public void testCopyAvatarFatalException() {

    Mockito.when(photoDAOmock.getAllAvatars())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      photoUCC.getAllAvatars();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }
}