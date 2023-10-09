package be.vinci.pae.domain;

import java.time.LocalDate;

/**
 * ItemDTO implementation of the ItemDTO interface.
 */
class ItemImpl implements Item {

  private int id;
  private String name;
  private String type;
  private String description;
  private PhotoDTO photo;
  private double price;
  private String state;
  private String reasonOfRefusal;
  private UserDTO offeringMember;
  private String phoneNumber;
  private AvailabilityDTO meetingDate;
  private String timeSlot;
  private LocalDate decisionDate;
  private LocalDate storeDepositDate;
  private LocalDate marketWithdrawalDate;
  private LocalDate sellingDate;
  private int version;

  public ItemImpl() {

  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public PhotoDTO getPhoto() {
    return photo;
  }

  @Override
  public void setPhoto(PhotoDTO photo) {
    this.photo = photo;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public void setPrice(double price) {
    this.price = price;
  }

  @Override
  public String getState() {
    return state;
  }

  @Override
  public void setState(String state) {
    this.state = state;
  }

  @Override
  public String getReasonOfRefusal() {
    return reasonOfRefusal;
  }

  @Override
  public void setReasonOfRefusal(String reasonOfRefusal) {
    this.reasonOfRefusal = reasonOfRefusal;
  }

  @Override
  public UserDTO getOfferingMember() {
    return offeringMember;
  }

  @Override
  public void setOfferingMember(UserDTO offeringMember) {
    this.offeringMember = offeringMember;
  }

  @Override
  public String getPhoneNumber() {
    return phoneNumber;
  }

  @Override
  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  @Override
  public AvailabilityDTO getMeetingDate() {
    return meetingDate;
  }

  @Override
  public void setMeetingDate(AvailabilityDTO meetingDate) {
    this.meetingDate = meetingDate;
  }

  @Override
  public String getTimeSlot() {
    return timeSlot;
  }

  @Override
  public void setTimeSlot(String timeSlot) {
    this.timeSlot = timeSlot;
  }

  @Override
  public LocalDate getDecisionDate() {
    return decisionDate;
  }

  @Override
  public void setDecisionDate(LocalDate decisionDate) {
    this.decisionDate = decisionDate;
  }

  @Override
  public LocalDate getStoreDepositDate() {
    return storeDepositDate;
  }

  @Override
  public void setStoreDepositDate(LocalDate storeDepositDate) {
    this.storeDepositDate = storeDepositDate;
  }

  @Override
  public LocalDate getMarketWithdrawalDate() {
    return marketWithdrawalDate;
  }

  @Override
  public void setMarketWithdrawalDate(LocalDate marketWithdrawalDate) {
    this.marketWithdrawalDate = marketWithdrawalDate;
  }

  @Override
  public LocalDate getSellingDate() {
    return sellingDate;
  }

  @Override
  public void setSellingDate(LocalDate sellingDate) {
    this.sellingDate = sellingDate;
  }

  @Override
  public int getVersion() {
    return version;
  }

  @Override
  public void setVersion(int version) {
    this.version = version;
  }

  @Override
  public boolean checkState(ItemDTO item, String state) {
    String itemState = item.getState();
    switch (itemState) {
      case "proposed":
        if (!state.equals("denied") && !state.equals("confirmed")) {
          return false;
        }
        break;
      case "denied", "removed", "sold":
        return false;
      case "confirmed":
        if (!state.equals("in_workshop") && !state.equals("in_store")) {
          return false;
        }
        break;
      case "in_workshop":
        if (!state.equals("in_store")) {
          return false;
        }
        break;
      case "in_store":
        if (!state.equals("for_sale") && !state.equals("sold")) {
          return false;
        }
        break;
      case "for_sale":
        if (!state.equals("sold") && !state.equals("removed")) {
          return false;
        }
        break;
      default:
        break;
    }
    return true;
  }

}
