package be.vinci.pae.services;

import be.vinci.pae.domain.ItemDTO;
import be.vinci.pae.utils.exceptions.FatalException;
import java.util.List;

/**
 * ItemDAO interface.
 */
public interface ItemDAO {

  /**
   * Get in the database an item matching the given id.
   *
   * @param id the item's id
   * @return an itemDTO; null if no item found in the database.
   */
  ItemDTO getOneItemWithUserFromItemId(int id) throws FatalException;

  /**
   * Get in the database all the items as well as the user who proposed the item.
   *
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllItemsWithUser() throws FatalException;

  /**
   * Get in the database all the items from a user.
   *
   * @param id the user's ID.
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllItemsWithUserFromUserId(int id);

  /**
   * Get in the database all proposed items.
   *
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllOfferedItemsWithUser();


  /**
   * Get in the database all items of given state.
   *
   * @param state1 state
   * @param state2 state
   * @param state3 state
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllStartingItemsWithUser(String state1, String state2, String state3);

  /**
   * Get in the database all items of given type.
   *
   * @param type type of the item.
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllItemsOfTypeWithUser(String type);

  /**
   * Change in the database, the description and type of an item.
   *
   * @param id          item's id.
   * @param description description to set for the item.
   * @param itemType    type to set for the item.
   * @param version     item's version number.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateInformation(int id, String description, String itemType, int version);


  /**
   * Change in the database, the state of an item.
   *
   * @param id    item's id.
   * @param state state to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateState(int id, String state);

  /**
   * Change in the database, the state of an item to denied with a reason of the refuse.
   *
   * @param id              item's id.
   * @param state           state to set for the item.
   * @param reasonOfRefusal reason of refusal for the offered item.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateState(int id, String state, String reasonOfRefusal);

  /**
   * Change in the database, the state of an item to 'confirmed'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateConfirmed(int id);

  /**
   * Change in the database, the state of an item to 'in_store'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateInStore(int id);

  /**
   * Change in the database, the state of an item to 'for_sale' and its price.
   *
   * @param id    item's id.
   * @param price price to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateForSale(int id, double price);

  /**
   * Change in the database, the state of an item to 'sold'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateSold(int id);

  /**
   * Change in the database, the state of an item to 'removed'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateRemoved(int id);

  /**
   * Get in the database all types of item.
   *
   * @return a list of String; null if no items found in database
   */
  List<String> getAllItemTypes();

  /**
   * Add one offer item in database.
   *
   * @param item an itemDTO
   * @return an itemDTO; null if no items found in database
   */
  ItemDTO addOneOfferItem(ItemDTO item);

  /**
   * Get all items in database that contains the word in their name.
   *
   * @param word a string
   * @return a list of itemDTO; null if no items found in database
   */
  List<ItemDTO> getAllItemsThatContainsInTheirName(String word);

  /**
   * Change the photo of an item.
   *
   * @param id      item's id.
   * @param photoId id of the new photo.
   * @return the updated ItemDTO.
   */
  ItemDTO changePhoto(int id, int photoId);


  /**
   * Change in the database, the state of an item to 'sold' and set its price.
   *
   * @param id    item's id.
   * @param price price to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  ItemDTO updateStateSoldAndSetPrice(int id, double price);

  /**
   * Get in the database all the items of a certain type as well as the user.
   *
   * @param type the item's type
   * @return a list of itemDTO; null if no item in the database.
   */
  List<ItemDTO> getAllItemsOfATypeWithUser(String type);

  /**
   * Get in the database all the items in a state as well as the user.
   *
   * @param state the item's state
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  List<ItemDTO> getAllItemsInAStateWithUser(String state);

}
