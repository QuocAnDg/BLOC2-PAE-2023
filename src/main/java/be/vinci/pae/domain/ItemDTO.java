package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * ItemDTO interface containing only getters and setters of an Item.
 */
public interface ItemDTO {

  /**
   * Get item's id.
   *
   * @return the item's id.
   */
  int getId();

  /**
   * Set the item's id.
   *
   * @param id the id to set.
   */
  void setId(int id);

  /**
   * Get item's name.
   *
   * @return the item's name.
   */
  String getName();

  /**
   * Set the item's name.
   *
   * @param name the name to set.
   */
  void setName(String name);

  /**
   * Get item's type.
   *
   * @return the item's type.
   */
  String getType();

  /**
   * Set the item's type.
   *
   * @param type type to set.
   */
  void setType(String type);

  /**
   * Get item's descrption.
   *
   * @return the item's description.
   */
  String getDescription();

  /**
   * Set the item's description.
   *
   * @param description description to set.
   */
  void setDescription(String description);

  /**
   * Get item's photo name.
   *
   * @return the item's photo name.
   */
  PhotoDTO getPhoto();

  /**
   * Set the item's photo name.
   *
   * @param photo name's photo to set.
   */
  void setPhoto(PhotoDTO photo);

  /**
   * Get item's price.
   *
   * @return the item's price.
   */
  double getPrice();

  /**
   * Set the item's price.
   *
   * @param price price to set.
   */
  void setPrice(double price);

  /**
   * Get item's state.
   *
   * @return the item's state.
   */
  String getState();

  /**
   * Set the item's state.
   *
   * @param state state to set.
   */
  void setState(String state);

  /**
   * Get the item's reason of refusal.
   *
   * @return the item's reason of refusal.
   */
  String getReasonOfRefusal();

  /**
   * Set the item's reason of refusal.
   *
   * @param reasonOfRefusal reason of refusal to set.
   */
  void setReasonOfRefusal(String reasonOfRefusal);

  /**
   * Get the member who offered the item.
   *
   * @return a UserDTO that represents the member who offered the item.
   */
  UserDTO getOfferingMember();

  /**
   * Set the member that offered the item.
   *
   * @param offeringMember userDTO to set.
   */
  void setOfferingMember(UserDTO offeringMember);

  /**
   * Get the phone number of the user that offered the item.
   *
   * @return the phone number of the user that offered the item.
   */
  String getPhoneNumber();

  /**
   * Set the phone number of the user that offered the item.
   *
   * @param phoneNumber phone number to set.
   */
  void setPhoneNumber(String phoneNumber);

  /**
   * Get the item's meeting date.
   *
   * @return a LocalDate that represents the item's meeting date.
   */
  AvailabilityDTO getMeetingDate();

  /**
   * Get the item's meeting date.
   *
   * @param meetingDate meeting date to set.
   */
  void setMeetingDate(AvailabilityDTO meetingDate);

  /**
   * Get the item's time slot.
   *
   * @return the item's time slot.
   */
  String getTimeSlot();

  /**
   * Set the item's time slot.
   *
   * @param timeSlot time slot to set.
   */
  void setTimeSlot(String timeSlot);

  /**
   * Get the item's decision date.
   *
   * @return a LocalDate that represents the item's decision date.
   */
  LocalDate getDecisionDate();

  /**
   * Set the item's decision date.
   *
   * @param decisionDate decision date for the item.
   */
  void setDecisionDate(LocalDate decisionDate);

  /**
   * Get the item's store deposit date.
   *
   * @return a LocalDate that represents the item's store deposit date.
   */
  LocalDate getStoreDepositDate();

  /**
   * Set the item's store deposit date.
   *
   * @param storeDepositDate store deposit date to set.
   */
  void setStoreDepositDate(LocalDate storeDepositDate);

  /**
   * Get the item's market withdrawal date.
   *
   * @return a LocalDate that represents the item's market withdrawal date.
   */
  LocalDate getMarketWithdrawalDate();

  /**
   * Set the item's market withdrawal date.
   *
   * @param marketWithdrawalDate market withdrawal date to set.
   */
  void setMarketWithdrawalDate(LocalDate marketWithdrawalDate);

  /**
   * Get the item's selling date.
   *
   * @return a LocalDate that represents the item's selling date.
   */
  LocalDate getSellingDate();

  /**
   * Set the item's selling date.
   *
   * @param sellingDate selling date to set.
   */
  void setSellingDate(LocalDate sellingDate);

  /**
   * Get the item's version.
   *
   * @return an int corresponding to the item's version.
   */
  int getVersion();

  /**
   * Set the item's selling date.
   *
   * @param version version's number to set.
   */
  void setVersion(int version);
}
