package be.vinci.pae.domain;

import be.vinci.pae.services.ItemDAO;
import be.vinci.pae.services.utils.DALServices;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import jakarta.inject.Inject;
import java.util.List;

/**
 * ItemUCC implementation.
 */
public class ItemUCCImpl implements ItemUCC {

  @Inject
  private ItemDAO itemDAO;
  @Inject
  private NotificationUCC notificationUCC;
  @Inject
  private DALServices myDalServices;

  /**
   * get an item's information.
   *
   * @param id item's id
   * @return an itemDTO; null if item not found.
   */

  public ItemDTO getItemInformation(int id) {
    Item item;
    try {
      myDalServices.startTransaction();
      item = (Item) itemDAO.getOneItemWithUserFromItemId(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (item == null) {
      throw new IllegalBusinessException(new ElementNotFoundException("Item not found"));
    }
    return item;
  }

  /**
   * view list of items.
   *
   * @return a list of itemDTO; null if the list is empty.
   */
  public List<ItemDTO> viewAllItems() {

    List<Item> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllItemsWithUser().stream().map(itemDTO -> (Item) itemDTO).toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (items.isEmpty()) {
      throw new IllegalBusinessException(new NoContentException("Items not found"));
    }
    return items.stream().map(item -> (ItemDTO) item).toList();
  }

  /**
   * view list of items from a user.
   *
   * @param id user's id
   * @return a list of itemDTO; null if the list is empty.
   */
  public List<ItemDTO> viewAllItemsFromUser(int id) {
    List<Item> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllItemsWithUserFromUserId(id).stream().map(itemDTO -> (Item) itemDTO)
          .toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }

    return items.stream().map(item -> (ItemDTO) item).toList();
  }

  /**
   * view list of offered items.
   *
   * @return a list of itemDTO; null if the list is empty.
   */
  @Override
  public List<ItemDTO> viewOfferedItems() {
    List<Item> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllOfferedItemsWithUser().stream().map(itemDTO -> (Item) itemDTO).toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (items.isEmpty()) {
      throw new IllegalBusinessException(new NoContentException("Offered items not found"));
    }
    return items.stream().map(item -> (ItemDTO) item).toList();
  }

  /**
   * view list of all items in the state "in_store", "for_sale" and "sold".
   *
   * @return a list of itemDTO; null if the list is empty.
   */
  @Override
  public List<ItemDTO> viewAllStartingItems() {
    List<Item> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllStartingItemsWithUser("in_store",
              "for_sale", "sold").stream()
          .map(itemDTO -> (Item) itemDTO)
          .toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (!items.isEmpty()) {
      items.forEach(item -> {
        if (!item.getState().equals("in_store") && !item.getState().equals("for_sale")
            && !item.getState().equals("sold")) {
          throw new IllegalBusinessException(
              new NoContentException("list contains an item without right type"));
        }
      });
    }
    return items.stream().map(item -> (ItemDTO) item).toList();
  }

  /**
   * view list of all items in given type.
   *
   * @return a list of itemDTO; null if the list is empty.
   */
  @Override
  public List<ItemDTO> viewAllItemsOfType(String type) {

    List<Item> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllItemsOfTypeWithUser(type).stream()
          .map(itemDTO -> (Item) itemDTO)
          .toList();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }

    if (!items.isEmpty()) {
      items.forEach(item -> {
        if (!item.getType().equals(type)) {
          throw new IllegalBusinessException(
              new NoContentException("list contains an item without right type"));
        }
      });
    }

    return items.stream().map(item -> (ItemDTO) item).toList();
  }

  /**
   * Change the description and type of item.
   *
   * @param id          item's id.
   * @param description item's description.
   * @param type        item's type.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemInformation(int id, String description, String type, int version) {
    Item updatedItem;

    try {
      getItemInformation(id);
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateInformation(id, description, type, version);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }


  /**
   * Change the state of the item to "confirmed".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateConfirmed(int id) {

    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "confirmed")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;

    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateConfirmed(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (updatedItem.getOfferingMember() != null) {
      notificationUCC.createDecisionNotification(
          updatedItem.getId(),
          updatedItem.getOfferingMember().getId()
      );
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "denied".
   *
   * @param id              item's id.
   * @param reasonOfRefusal reason of refusal for the offered item.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateDenied(int id, String reasonOfRefusal) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "denied")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateState(id, "denied", reasonOfRefusal);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (updatedItem.getOfferingMember() != null) {
      notificationUCC.createDecisionNotification(
          updatedItem.getId(),
          updatedItem.getOfferingMember().getId()
      );
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "in_workshop".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateInWorkshop(int id) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "in_workshop")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateState(id, "in_workshop");
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "in_store".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateInStore(int id) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "in_store")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateInStore(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "for_sale".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateForSale(int id, double price) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "for_sale")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateForSale(id, price);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "sold".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateSold(int id) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "sold")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateSold(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }

  /**
   * Change the state of the item to "removed".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateRemoved(int id) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "removed")) {
      throw new IllegalBusinessException(
          new NotAllowedException("Do not have permission to modify the state of the item"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateRemoved(id);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }

  /**
   * view all item's types.
   *
   * @return a list of String (type's name)
   */
  @Override
  public List<String> viewAllItemTypes() {
    List<String> itemTypes;
    try {
      myDalServices.startTransaction();
      itemTypes = itemDAO.getAllItemTypes();
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return itemTypes;
  }

  /**
   * Add an offered item.
   *
   * @param item an itemDTO
   * @return an itemDTO.
   */
  @Override
  public ItemDTO addOneOfferedItem(ItemDTO item) {
    ItemDTO itemTypes;
    try {
      if (item.getPhoneNumber() != null) {
        String phoneNumberFormatRegex = "^[0-9.\\/( )]*$";
        if (!item.getPhoneNumber().matches(phoneNumberFormatRegex)) {
          throw new IllegalBusinessException(
              new UnauthorizedException("incorrect phone number format"));
        }
      }
      myDalServices.startTransaction();
      itemTypes = itemDAO.addOneOfferItem(item);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    notificationUCC.createProposalNotification(itemTypes.getId());
    return itemTypes;
  }

  /**
   * Search items by name.
   *
   * @param name the searched name
   * @return a list of itemDTO.
   */
  @Override
  public List<ItemDTO> searchItemsByName(String name) {
    List<ItemDTO> itemList;
    try {
      myDalServices.startTransaction();
      if (name.trim().length() > 0) {
        itemList = itemDAO.getAllItemsThatContainsInTheirName(name);
      } else {

        itemList = itemDAO.getAllStartingItemsWithUser("in_store",
            "for_sale", "sold");
      }
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return itemList;
  }

  /**
   * Change the photo of an item.
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setPhoto(int id, PhotoDTO photo) {
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.changePhoto(id, photo.getId());
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }


  /**
   * Change the state of the item to "sold" and set the price amount.
   *
   * @param id    item's id.
   * @param price item's price to set.
   * @return an ItemDTO.
   */
  @Override
  public ItemDTO setItemStateSoldWithPrice(int id, double price) {
    Item itemToUpdate = (Item) getItemInformation(id);
    if (!itemToUpdate.checkState(itemToUpdate, "sold")) {
      throw new IllegalBusinessException(
          new NotAllowedException(
              "Not allowed to change the state to 'sold' from the current item state"));
    }
    if (itemToUpdate.getPrice() != 0.0) {
      throw new IllegalBusinessException(
          new NotAllowedException(
              "Price has already been set"));
    }
    Item updatedItem;
    try {
      myDalServices.startTransaction();
      updatedItem = (Item) itemDAO.updateStateSoldAndSetPrice(id, price);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    return updatedItem;
  }


  /**
   * Get all items of a type.
   *
   * @param type the item's type
   * @return a list of itemDTO.
   */
  @Override
  public List<ItemDTO> getAllItemsOfAType(String type) {
    List<ItemDTO> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllItemsOfATypeWithUser(type);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (items.isEmpty()) {
      throw new IllegalBusinessException(new ElementNotFoundException("Items not found"));
    }
    return items;
  }

  /**
   * Get all items of a state.
   *
   * @param state the item's state
   * @return a list of itemDTO.
   */
  @Override
  public List<ItemDTO> getAllItemsOfAState(String state) {
    List<ItemDTO> items;
    try {
      myDalServices.startTransaction();
      items = itemDAO.getAllItemsInAStateWithUser(state);
      myDalServices.commit();
    } catch (Exception e) {
      myDalServices.rollback();
      throw e;
    }
    if (items.isEmpty()) {
      throw new IllegalBusinessException(new ElementNotFoundException("Items not found"));
    }
    return items;
  }
}