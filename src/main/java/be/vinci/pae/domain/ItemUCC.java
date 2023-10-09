package be.vinci.pae.domain;

import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.NoContentException;
import be.vinci.pae.utils.exceptions.NotAllowedException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import java.util.List;

/**
 * ItemUCC interface.
 */
public interface ItemUCC {

  /**
   * get an item's information.
   *
   * @param id the item's id.
   * @return an itemDTO; null if no item found.
   */
  ItemDTO getItemInformation(int id) throws FatalException, ElementNotFoundException;

  /**
   * View all items.
   *
   * @return a list of ItemDTO; null if list is empty.
   */
  List<ItemDTO> viewAllItems() throws FatalException, NoContentException;

  /**
   * View all items from a user.
   *
   * @param id the item's id.
   * @return a list of ItemDTO; null if list is empty.
   */
  List<ItemDTO> viewAllItemsFromUser(int id) throws FatalException, NoContentException;


  /**
   * View all offered items.
   *
   * @return a list of ItemDTO; null if list is empty.
   */
  List<ItemDTO> viewOfferedItems() throws FatalException, NoContentException;


  /**
   * View all items in given state.
   *
   * @return a list of ItemDTO; null if list is empty.
   */
  List<ItemDTO> viewAllStartingItems() throws FatalException, NoContentException;

  /**
   * View all items of the given type.
   *
   * @param type the type of the items
   * @return a list of ItemDTO; null if list is empty.
   */
  List<ItemDTO> viewAllItemsOfType(String type) throws FatalException, NoContentException;


  /**
   * Change the description and type of item.
   *
   * @param id          item's id.
   * @param description item's description.
   * @param type        item's type.
   * @param version     item's version number.
   * @return an ItemDTO.
   */
  ItemDTO setItemInformation(int id, String description, String type, int version);


  /**
   * Change the state of the item to "confirmed".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateConfirmed(int id)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * Change the state of the item to "in_workshop".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateInWorkshop(int id)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * Change the state of the item to "in_store".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateInStore(int id)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * Change the state of the item to "for_sale".
   *
   * @param id    item's id.
   * @param price item's price.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateForSale(int id, double price)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * Change the state of the item to "sold".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateSold(int id)
      throws FatalException, ElementNotFoundException, UnauthorizedException, NotAllowedException;

  /**
   * Change the state of the item to "removed".
   *
   * @param id item's id.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateRemoved(int id)
      throws FatalException, ElementNotFoundException, UnauthorizedException;

  /**
   * Change the state of the item to "denied".
   *
   * @param id              item's id.
   * @param reasonOfRefusal reason of refusal for the offered item.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateDenied(int id, String reasonOfRefusal)
      throws FatalException, UnauthorizedException, ElementNotFoundException, NotAllowedException;

  /**
   * view all item's types.
   *
   * @return a list of String (type's name).
   */
  List<String> viewAllItemTypes();

  /**
   * Add an offered item.
   *
   * @param item an itemDTO
   * @return an itemDTO.
   */
  ItemDTO addOneOfferedItem(ItemDTO item);

  /**
   * Search items by name.
   *
   * @param name the searched name
   * @return a list of itemDTO.
   */
  List<ItemDTO> searchItemsByName(String name);

  /**
   * Change the photo of an item.
   *
   * @param id    item's id.
   * @param photo item's photo.
   * @return an ItemDTO.
   */
  ItemDTO setPhoto(int id, PhotoDTO photo);

  /**
   * Change the state of the item to "sold" and set the price amount.
   *
   * @param id    item's id.
   * @param price item's price to set.
   * @return an ItemDTO.
   */
  ItemDTO setItemStateSoldWithPrice(int id, double price);

  /**
   * Get all items of a type.
   *
   * @param type the item type
   * @return a list of itemDTO.
   */
  List<ItemDTO> getAllItemsOfAType(String type);

  /**
   * Get all items of a state.
   *
   * @param state the item's state
   * @return a list of itemDTO.
   */
  List<ItemDTO> getAllItemsOfAState(String state);

}