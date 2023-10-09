package be.vinci.pae.domain;

/**
 * Item interface inheriting the ItemDTO interface and containing business methods.
 */
public interface Item extends ItemDTO {

  /**
   * Check if an item is allowed to change to the state in parameter.
   *
   * @param itemDTO an item.
   * @param state   state to change to.
   * @return true if the current item is allowed to change to the state; false else.
   */
  boolean checkState(ItemDTO itemDTO, String state);


}
