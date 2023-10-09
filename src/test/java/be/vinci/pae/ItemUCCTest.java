package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.DomainFactoryImpl;
import be.vinci.pae.domain.Item;
import be.vinci.pae.domain.ItemDTO;
import be.vinci.pae.domain.ItemUCC;
import be.vinci.pae.domain.ItemUCCImpl;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.services.ItemDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.ApplicationBinder;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
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

class ItemUCCTest {

  private static ItemDAO itemDAOmock;
  private static NotificationUCC notificationUCCmock;
  private static PhotoDTO photoDAOmock;
  private static DALServices dalServicesMock;
  private static ServiceLocator locator;
  private ItemUCC itemUCC;
  private DomainFactory domainFactory;
  private ItemDTO existingItem;
  private PhotoDTO existingPhoto;


  @BeforeAll
  static void setupBeforeAll() {
    itemDAOmock = Mockito.mock(ItemDAO.class);
    dalServicesMock = Mockito.mock(DALServices.class);
    notificationUCCmock = Mockito.mock(NotificationUCC.class);
    AbstractBinder testApplicationBinder = new ApplicationBinder() {
      @Override
      protected void configure() {
        bind(ItemUCCImpl.class).to(ItemUCC.class).in(Singleton.class);
        bind(DomainFactoryImpl.class).to(DomainFactory.class).in(Singleton.class);
        bind(itemDAOmock).to(ItemDAO.class);
        bind(dalServicesMock).to(DALServices.class);
        bind(notificationUCCmock).to(NotificationUCC.class);
      }
    };
    locator = ServiceLocatorUtilities.bind(testApplicationBinder);
  }

  @BeforeEach
  void setup() {
    Mockito.doNothing().when(dalServicesMock).startTransaction();
    Mockito.doNothing().when(dalServicesMock).commit();
    Mockito.doNothing().when(dalServicesMock).rollback();
    itemUCC = locator.getService(ItemUCC.class);
    domainFactory = locator.getService(DomainFactory.class);
  }

  @AfterEach
  void tearDown() {
    Mockito.reset(itemDAOmock);
    Mockito.reset(dalServicesMock);
  }

  @Test
  @DisplayName("Test setItemStateConfirmed() method with an item "
      + "where its state is defined to 'proposed'")
  void testConfirmedAnOfferedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;

    assertTrue(itemBiz.checkState(existingItem, "confirmed"));

    Mockito.when(itemDAOmock.updateStateConfirmed(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("confirmed");
      return existingItem;
    });
    ItemDTO itemReturned = itemUCC.setItemStateConfirmed(1);

    assertEquals("confirmed", itemReturned.getState());


  }

  @Test
  @DisplayName("Test setItemStateConfirmed() method with an item "
      + "where its state is not defined to 'proposed'")
  void testConfirmedWithIncorrectStateItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Item itemBiz = (Item) existingItem;

    assertFalse(itemBiz.checkState(existingItem, "confirmed"));

    Mockito.when(itemDAOmock.updateState(Mockito.eq(1), Mockito.eq("confirmed")))
        .thenAnswer(invocation -> {
          existingItem.setState(invocation.getArgument(1));
          return existingItem;
        });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateConfirmed(1));

    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateConfirmed method throws FatalException")
  public void testConfirmedExceptions() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateConfirmed(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateConfirmed(1);
    });

  }

  @Test
  @DisplayName(
      "Test setItemStateDenied() method with " + "an item where his state is defined to 'proposed'")
  void testDeniedAnOfferedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "denied"));

    Mockito.when(itemDAOmock.updateState(Mockito.eq(1), Mockito.eq("denied"), Mockito.eq("test")))
        .thenAnswer(invocation -> {
          existingItem.setState(invocation.getArgument(1));
          existingItem.setReasonOfRefusal(invocation.getArgument(2));
          return existingItem;
        });
    ItemDTO itemReturned = itemUCC.setItemStateDenied(1, "test");
    assertEquals("denied", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateDenied() method with an item where his state "
      + "is not defined to 'proposed'")
  void testDeniedNotAnOfferedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "denied"));

    Mockito.when(itemDAOmock.updateState(Mockito.eq(1), Mockito.eq("denied"), Mockito.eq("test")))
        .thenAnswer(invocation -> {
          existingItem.setState(invocation.getArgument(1));
          existingItem.setReasonOfRefusal(invocation.getArgument(2));
          return existingItem;
        });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateDenied(1, "test"));

    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateDenied method throws FatalException")
  public void testDeniedExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateState(1, "denied", "test"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));
    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateDenied(1, "test");
    });
  }

  @Test
  @DisplayName("Test setItemStateInWorkshop() method with an item "
      + "where his state is defined to 'confirmed'")
  void testInWorkshopAConfirmedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "in_workshop"));
    Mockito.when(
            itemDAOmock.updateState(Mockito.eq(1), Mockito.eq("in_workshop")))
        .thenAnswer(
            invocation -> {
              existingItem.setState(invocation.getArgument(1));
              return existingItem;
            }
        );
    ItemDTO itemReturned = itemUCC.setItemStateInWorkshop(1);
    assertEquals("in_workshop", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateInWorkshop() method with an item "
      + "where his state is not defined to 'confirmed'")
  void testInWorkshopNotAConfirmedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "in_workshop"));

    Mockito.when(itemDAOmock.updateState(Mockito.eq(1), Mockito.eq("in_workshop")))
        .thenAnswer(invocation -> {
          existingItem.setState(invocation.getArgument(1));
          return existingItem;
        });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateInWorkshop(1));

    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);

  }

  @Test
  @DisplayName("Test setItemStateInWorkshop method throws FatalException")
  public void testInWorkshopExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateState(1, "in_workshop"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateInWorkshop(1);
    });
  }

  @Test
  @DisplayName("Test setItemStateInStore() method with an item "
      + "where his state is defined to 'in_workshop'")
  void testInStoreAInWorkshopItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_workshop");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "in_store"));

    Mockito.when(itemDAOmock.updateStateInStore(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("in_store");
      return existingItem;
    });
    ItemDTO itemReturned = itemUCC.setItemStateInStore(1);
    assertEquals("in_store", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateInStore() method with an item "
      + "where his state is defined to 'confirmed'")
  void testInStoreAConfirmedItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "in_store"));

    Mockito.when(itemDAOmock.updateStateInStore(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("in_store");
      return existingItem;
    });
    ItemDTO itemReturned = itemUCC.setItemStateInStore(1);
    assertEquals("in_store", itemReturned.getState());
  }

  @Test
  @DisplayName("Test updateState() method with an item "
      + "where his state is not defined to 'confirmed' or 'in_workshop'")
  void testInStoreNotConfirmedOrInWorkshopItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "in_store"));

    Mockito.when(itemDAOmock.updateStateInStore(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("in_store");
      return existingItem;
    });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateInStore(1));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateInStore throws FatalException")
  public void testInStoreExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateInStore(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateInStore(1);
    });
  }

  @Test
  @DisplayName("Test setItemStateForSale() method with an item "
      + "where his state is defined to 'in_store'")
  void testForSaleInStoreItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "for_sale"));

    Mockito.when(itemDAOmock.updateStateForSale(Mockito.eq(1), Mockito.eq(3.50)))
        .thenAnswer(invocation -> {
          existingItem.setState("for_sale");
          existingItem.setPrice(invocation.getArgument(1));
          return existingItem;
        });
    ItemDTO itemReturned = itemUCC.setItemStateForSale(1, 3.50);
    assertEquals("for_sale", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateForSale() method with an item "
      + "where his state is not defined to 'in_store'")
  void testForSaleNotInStoreItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "for_sale"));

    Mockito.when(itemDAOmock.updateStateForSale(Mockito.eq(1), Mockito.eq(3.50)))
        .thenAnswer(invocation -> {
          existingItem.setState("for_sale");
          existingItem.setPrice(invocation.getArgument(1));
          return existingItem;
        });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateForSale(1, 3.50));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateForSale throws FatalException")
  public void testForSaleExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateForSale(1, 3.5))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateForSale(1, 3.5);
    });
  }

  @Test
  @DisplayName("Test setItemStateRemoved() method with an item "
      + "where his state is defined to 'for_sale'")
  void testRemoveAnItemForSale() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("for_sale");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "removed"));

    Mockito.when(itemDAOmock.updateStateRemoved(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("for_sale");
      return existingItem;
    });
    ItemDTO itemReturned = itemUCC.setItemStateRemoved(1);
    assertEquals("for_sale", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateRemoved() method with an item "
      + "where his state is not defined to 'for_sale'")
  void testRemoveNotAItemForSale() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "removed"));

    Mockito.when(itemDAOmock.updateStateRemoved(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("removed");
      return existingItem;
    });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateRemoved(1));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateRemoved throws FatalException")
  public void testRemovedExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("for_sale");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateRemoved(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateRemoved(1);
    });
  }

  @Test
  @DisplayName(
      "Test setItemStateSold() method with an item " + "where his state is defined to 'for_sale'")
  void testSoldAnItemForSale() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("for_sale");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertTrue(itemBiz.checkState(existingItem, "sold"));

    Mockito.when(itemDAOmock.updateStateSold(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("sold");
      return existingItem;
    });
    ItemDTO itemReturned = itemUCC.setItemStateSold(1);
    assertEquals("sold", itemReturned.getState());
  }

  @Test
  @DisplayName("Test setItemStateSold() method with an item "
      + "where his state is not defined to 'for_sale'")
  void testSoldAnItemNotForSale() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Item itemBiz = (Item) existingItem;
    assertFalse(itemBiz.checkState(existingItem, "sold"));

    Mockito.when(itemDAOmock.updateStateSold(Mockito.eq(1))).thenAnswer(invocation -> {
      existingItem.setState("sold");
      return existingItem;
    });
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateSold(1));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateSold method throws FatalException")
  public void testSoldExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("for_sale");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateSold(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateSold(1);
    });
  }

  @Test
  @DisplayName("Test viewAllOffered() with one item in the list")
  void testViewAllOfferedItemsWithOneItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllOfferedItemsWithUser()).thenReturn(itemsDTO);
    List<ItemDTO> itemReturned = itemUCC.viewOfferedItems();
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllOffered() with no items")
  void testViewAllOfferedItemsWithNoItem() {
    List<ItemDTO> itemsDTO = new ArrayList<>();
    Mockito.when(itemDAOmock.getAllOfferedItemsWithUser()).thenReturn(itemsDTO);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.viewOfferedItems());
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test viewOfferedItems method throws FatalException")
  public void testViewOfferedItemsException() {
    Mockito.when(itemDAOmock.getAllOfferedItemsWithUser())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.viewOfferedItems();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test viewAllItems() with one item in the list")
  void testViewAllItemsWithOneItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllItemsWithUser()).thenReturn(itemsDTO);
    List<ItemDTO> itemReturned = itemUCC.viewAllItems();
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllItems() with no items")
  void testViewAllItemsWithNoItem() {
    List<ItemDTO> itemsDTO = new ArrayList<>();
    Mockito.when(itemDAOmock.getAllItemsWithUser()).thenReturn(itemsDTO);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.viewAllItems());
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test viewAllItems method throws FatalException")
  public void testAllItemsExceptions() {
    Mockito.when(itemDAOmock.getAllItemsWithUser())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));
    assertAll(() -> assertThrows(Exception.class, () -> {
      itemUCC.viewAllItems();
    }), () -> Mockito.verify(dalServicesMock, Mockito.never()).commit());
  }

  @Test
  @DisplayName("Test viewAllItemsFromUser(int id) with one item")
  void testViewAllItemsFromUserWithOneItem() {
    UserDTO userDTO = domainFactory.getUserDTO();
    userDTO.setId(1);
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("denied");
    existingItem.setOfferingMember(userDTO);
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllItemsWithUserFromUserId(1)).thenReturn(itemsDTO);
    List<ItemDTO> itemReturned = itemUCC.viewAllItemsFromUser(1);
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllItemsFromUser throws FatalException")
  public void testViewAllItemsFromUserFatalException() {

    UserDTO userDTO = domainFactory.getUserDTO();
    userDTO.setId(1);
    Mockito.when(itemDAOmock.getAllItemsWithUserFromUserId(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));
    assertAll(() -> assertThrows(Exception.class, () -> {
      itemUCC.viewAllItemsFromUser(1);
    }));
  }

  @Test
  @DisplayName("Test getItemInformation(int id) with existing item")
  void testgetInformationOfItem() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    ItemDTO itemReturned = itemUCC.getItemInformation(1);
    assertNotNull(itemReturned);
  }

  @Test
  @DisplayName("Test getItemInformation(int id) with inexistant item")
  void testGetInformationOfInexistantItem() {

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(null);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.getItemInformation(1));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(ElementNotFoundException.class, cause);
  }

  @Test
  @DisplayName("Test getItemInformation method when "
      + "dalServices startTransaction method throws FatalException")
  public void testItemInformationExceptions() {

    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));
    assertThrows(FatalException.class, () -> {
      itemUCC.getItemInformation(1);
    });
  }

  @Test
  @DisplayName("Test viewAllItemTypes() with one type in the list")
  void testViewAllItemTypesWithOneType() {
    String type1 = "Chaise";
    List<String> typesItem = new ArrayList<>();
    typesItem.add(type1);
    Mockito.when(itemDAOmock.getAllItemTypes()).thenReturn(typesItem);
    List<String> itemReturned = itemUCC.viewAllItemTypes();
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllItemTypes() Exception")
  void testViewAllItemTypesException() {
    Mockito.when(itemDAOmock.getAllItemTypes())
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.viewAllItemTypes();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test addOneOfferedItem() no error")
  void testAddOneOfferedItem() {
    existingPhoto = domainFactory.getPhotoDTO();
    AvailabilityDTO availabilityDTO = domainFactory.getAvailabilityDTO();
    availabilityDTO.setDate(LocalDate.now());
    existingPhoto.setId(1);
    existingPhoto.setAccessPath("example.png");
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    existingItem.setTimeSlot("evening");
    existingItem.setMeetingDate(availabilityDTO);
    existingItem.setPhoneNumber("0123/45.67.89");
    existingItem.setPhoto(existingPhoto);
    existingItem.setName("Chaise");
    Mockito.when(itemDAOmock.addOneOfferItem(existingItem)).thenReturn(existingItem);
    ItemDTO itemReturned = itemUCC.addOneOfferedItem(existingItem);
    assertAll(() -> assertEquals(itemReturned, existingItem));
  }

  @Test
  @DisplayName("Test addOneOfferedItem() Exception")
  void testAddOneOfferedItemException() {
    existingPhoto = domainFactory.getPhotoDTO();
    AvailabilityDTO availabilityDTO = domainFactory.getAvailabilityDTO();
    availabilityDTO.setDate(LocalDate.now());
    existingPhoto.setId(1);
    existingPhoto.setAccessPath("example.png");
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    existingItem.setTimeSlot("evening");
    existingItem.setMeetingDate(availabilityDTO);
    existingItem.setPhoto(existingPhoto);
    existingItem.setName("Chaise");
    Mockito.when(itemDAOmock.addOneOfferItem(existingItem))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.addOneOfferedItem(existingItem);
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test addOneOfferedItem() with wrong phone number format")
  void testAddOneOfferedItemExceptionBIZ() {
    existingPhoto = domainFactory.getPhotoDTO();
    AvailabilityDTO availabilityDTO = domainFactory.getAvailabilityDTO();
    availabilityDTO.setDate(LocalDate.now());
    existingPhoto.setId(1);
    existingPhoto.setAccessPath("example.png");
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    existingItem.setTimeSlot("evening");
    existingItem.setMeetingDate(availabilityDTO);
    existingItem.setPhoneNumber("10Wrong24é&1");
    existingItem.setPhoto(existingPhoto);
    existingItem.setName("Chaise");
    Mockito.when(itemDAOmock.addOneOfferItem(existingItem)).thenReturn(existingItem);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.addOneOfferedItem(existingItem));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(UnauthorizedException.class, cause);
  }

  @Test
  @DisplayName("Test viewAllItemsOfType(String type) with item with right type")
  void testViewAllItemsOfTypeWithItemWithRightType() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setType("Meuble");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllItemsOfTypeWithUser("Meuble")).thenReturn(itemsDTO);
    List<ItemDTO> itemReturned = itemUCC.viewAllItemsOfType("Meuble");
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllItemsOfType(String type) with one item with wrong type")
  void testViewAllItemsOfOneTypeButOneItemIsWrongType() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setType("Meuble");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllItemsOfTypeWithUser("Chaise")).thenReturn(itemsDTO);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.viewAllItemsOfType("Chaise"));
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test viewAllItemsOfType(String type) method throws FatalException")
  public void testViewAllItemsOfTypeThrowsFatal() {
    Mockito.when(itemDAOmock.getAllItemsOfTypeWithUser("Chaise"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.viewAllItemsOfType("Chaise");
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test viewAllStartingItems() with item in right state")
  void testViewAllStartingItemsWithItemInRightState() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenReturn(itemsDTO);
    List<ItemDTO> itemReturned = itemUCC.viewAllStartingItems();
    assertAll(() -> assertEquals(1, itemReturned.size()));
  }

  @Test
  @DisplayName("Test viewAllStartingItems() with one item with wrong state")
  void testViewAllStartingItemsButOneItemIsWrongState() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");
    List<ItemDTO> itemsDTO = new ArrayList<>();
    itemsDTO.add(existingItem);
    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenReturn(itemsDTO);
    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.viewAllStartingItems());
    Throwable cause = parentsException.getCause();
    assertInstanceOf(NoContentException.class, cause);
  }

  @Test
  @DisplayName("Test viewAllStartingItems() method throws FatalException")
  public void testViewAllStartingItemsThrowsFatal() {
    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.viewAllStartingItems();
    });

    Mockito.verify(dalServicesMock, Mockito.never()).commit();
  }

  @Test
  @DisplayName("Test setItemInformation on item not in database")
  void testSetItemInformationForNonExistingId() {
    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(5)).thenReturn(null);

    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemInformation(5,
            "salut", "Meuble", 1));
    Throwable cause = parentsException.getCause();

    assertInstanceOf(ElementNotFoundException.class, cause);
  }

  @Test
  @DisplayName("Test setItemInformation successfully changes description and type")
  void testSetItemInformationSuccess() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    existingItem.setDescription("description1");
    existingItem.setType("Chaise");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Mockito.when(itemDAOmock.updateInformation(Mockito.eq(1), Mockito.eq("description2"),
        Mockito.eq("Meuble"), Mockito.eq(1))).thenAnswer(invocation -> {
          existingItem.setDescription(invocation.getArgument(1));
          existingItem.setType(invocation.getArgument(2));
          return existingItem;
        }
    );

    ItemDTO itemReturned = itemUCC.setItemInformation(1, "description2", "Meuble", 1);
    assertAll(
        () -> assertEquals(itemReturned.getDescription(), "description2"),
        () -> assertEquals(itemReturned.getType(), "Meuble")
    );
  }

  @Test
  @DisplayName("Test setItemInformation() method throws FatalException")
  public void testSetItemInformationFatalException() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("confirmed");
    existingItem.setDescription("description1");
    existingItem.setType("Chaise");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Mockito.when(itemDAOmock.updateInformation(1, "description2", "Meuble", 1))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemInformation(1, "description2", "Meuble", 1);
    });
  }

  @Test
  @DisplayName("Test setItemInformation() method with matching word and not empty")
  public void testSearchItemByNameWithWordMatchingAndNotEmpty() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setName("Fauteuil");
    existingItem.setState("in_store");
    List<ItemDTO> list1 = new ArrayList<>();
    list1.add(existingItem);

    ItemDTO existingItem2 = domainFactory.getItemDTO();
    existingItem2.setId(2);
    existingItem2.setName("Chaise");
    existingItem2.setState("in_store");
    List<ItemDTO> list2 = new ArrayList<>();
    list2.add(existingItem2);

    Mockito.when(itemDAOmock.getAllItemsThatContainsInTheirName("ch")).thenReturn(list2);

    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenReturn(list1);

    assertEquals(list2, itemUCC.searchItemsByName("ch"));
  }

  @Test
  @DisplayName("Test SearchItemByName method with empty word")
  public void testSearchItemByNameWithWordEmpty() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setName("Fauteuil");
    existingItem.setState("in_store");
    List<ItemDTO> list1 = new ArrayList<>();
    list1.add(existingItem);

    ItemDTO existingItem2 = domainFactory.getItemDTO();
    existingItem2.setId(2);
    existingItem2.setName("Chaise");
    existingItem2.setState("in_store");
    List<ItemDTO> list2 = new ArrayList<>();
    list2.add(existingItem2);

    Mockito.when(itemDAOmock.getAllItemsThatContainsInTheirName("ch")).thenReturn(list2);

    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenReturn(list1);

    assertEquals(list1, itemUCC.searchItemsByName(""));
  }

  @Test
  @DisplayName("Test SearchItemByName method with not empty word throws FatalException")
  public void testSearchItemByNameNotEmptyFatalException1() {
    Mockito.when(itemDAOmock.getAllItemsThatContainsInTheirName("ch"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.searchItemsByName("ch");
    });
  }

  @Test
  @DisplayName("Test SearchItemByName method with empty word throws FatalException")
  public void testSearchItemByNameWithWordEmptyFatalException1() {
    Mockito.when(itemDAOmock.getAllStartingItemsWithUser("in_store", "for_sale", "sold"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.searchItemsByName("");
    });
  }

  @Test
  @DisplayName("Test setPhoto method")
  public void testSetPhoto() {
    PhotoDTO newPhoto = domainFactory.getPhotoDTO();
    newPhoto.setId(15);
    ItemDTO itemToReturn = domainFactory.getItemDTO();
    itemToReturn.setId(2);
    itemToReturn.setPhoto(newPhoto);
    Mockito.when(itemDAOmock.changePhoto(2, 15)).thenReturn(itemToReturn);

    assertEquals(itemUCC.setPhoto(2, newPhoto), itemToReturn);
  }

  @Test
  @DisplayName("Test setPhoto method throws fatal exception")
  public void testSetPhotoThrowsFatal() {
    PhotoDTO newPhoto = domainFactory.getPhotoDTO();
    newPhoto.setId(15);
    Mockito.when(itemDAOmock.changePhoto(1, newPhoto.getId()))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setPhoto(1, newPhoto);
    });
  }

  @Test
  @DisplayName("Test setItemStateSoldWithPrice method with item state not valid")
  public void testSetItemStateSoldWithPriceInvalidState() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("proposed");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateSoldWithPrice(1, 2.5));
    Throwable cause = parentsException.getCause();

    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateSoldWithPrice method with price already set")
  public void testSetItemStateSoldWithPriceButPriceAlreadySet() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("for_sale");
    existingItem.setPrice(3.0);

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.setItemStateSoldWithPrice(1, 2.5));
    Throwable cause = parentsException.getCause();

    assertInstanceOf(NotAllowedException.class, cause);
  }

  @Test
  @DisplayName("Test setItemStateSoldWithPrice method with valid item state and price not set yet")
  public void testSetItemStateSoldWithPriceWithValidStateAndPriceNotSet() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);

    Mockito.when(itemDAOmock.updateStateSoldAndSetPrice(Mockito.eq(1), Mockito.eq(2.5)))
        .thenAnswer(invocation -> {
              existingItem.setState("sold");
              existingItem.setPrice(invocation.getArgument(1));
              return existingItem;
            }
        );

    ItemDTO itemReturned = itemUCC.setItemStateSoldWithPrice(1, 2.5);
    assertAll(
        () -> assertEquals(itemReturned.getState(), "sold"),
        () -> assertEquals(itemReturned.getPrice(), 2.5)
    );
  }

  @Test
  @DisplayName("Test setItemStateSoldWithPrice method throws FatalException")
  public void testSetItemStateSoldWithPriceFatalException() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setState("in_store");

    Mockito.when(itemDAOmock.getOneItemWithUserFromItemId(1)).thenReturn(existingItem);
    Mockito.when(itemDAOmock.updateStateSoldAndSetPrice(1, 2.5))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.setItemStateSoldWithPrice(1, 2.5);
    });
  }

  @Test
  @DisplayName("Test SearchItemByType with item of right type")
  public void testSearchItemByTypeWithRightType() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setName("Meuble");
    existingItem.setType("Meuble");
    List<ItemDTO> list1 = new ArrayList<>();
    list1.add(existingItem);

    Mockito.when(itemDAOmock.getAllItemsOfATypeWithUser("Meuble")).thenReturn(list1);

    assertEquals(list1, itemUCC.getAllItemsOfAType("Meuble"));
  }

  @Test
  @DisplayName("Test SearchItemByType gives empty list (no result)")
  public void testSearchItemByTypeWithEmptyList() {
    List<ItemDTO> list1 = new ArrayList<>();

    Mockito.when(itemDAOmock.getAllItemsOfATypeWithUser("Meuble")).thenReturn(list1);

    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.getAllItemsOfAType("Meuble"));
    Throwable cause = parentsException.getCause();

    assertInstanceOf(ElementNotFoundException.class, cause);
  }

  @Test
  @DisplayName("Test SearchItemByType throws FatalException")
  public void testSearchItemByNameNotEmptyFatalException1sdfsdfsdf() {
    Mockito.when(itemDAOmock.getAllItemsOfATypeWithUser("Meuble"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.getAllItemsOfAType("Meuble");
    });
  }


  @Test
  @DisplayName("Test SearchItemByState with item of right state")
  public void testSearchItemByStateWithRightState() {
    existingItem = domainFactory.getItemDTO();
    existingItem.setId(1);
    existingItem.setName("Meuble");
    existingItem.setState("in_store");
    List<ItemDTO> list1 = new ArrayList<>();
    list1.add(existingItem);

    Mockito.when(itemDAOmock.getAllItemsInAStateWithUser("in_store")).thenReturn(list1);

    assertEquals(list1, itemUCC.getAllItemsOfAState("in_store"));
  }

  @Test
  @DisplayName("Test SearchItemByType gives empty list (no result)")
  public void testSearchItemByStateWithEmptyList() {
    List<ItemDTO> list1 = new ArrayList<>();

    Mockito.when(itemDAOmock.getAllItemsInAStateWithUser("in_store")).thenReturn(list1);

    IllegalBusinessException parentsException = assertThrows(IllegalBusinessException.class,
        () -> itemUCC.getAllItemsOfAState("in_store"));
    Throwable cause = parentsException.getCause();

    assertInstanceOf(ElementNotFoundException.class, cause);
  }

  @Test
  @DisplayName("Test SearchItemByState throws FatalException")
  public void testSearchItemByStateNotEmptyFatalException1sdfsdfsdf() {
    Mockito.when(itemDAOmock.getAllItemsInAStateWithUser("in_store"))
        .thenThrow(new FatalException(new SQLException("A problem was occurred in the server")));

    assertThrows(FatalException.class, () -> {
      itemUCC.getAllItemsOfAState("in_store");
    });
  }

}