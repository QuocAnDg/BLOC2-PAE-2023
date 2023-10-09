package be.vinci.pae.services;

import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.ItemDTO;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.services.utils.DALBackendServices;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.ElementNotFoundException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import jakarta.inject.Inject;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * ItemDAO implementation.
 */
public class ItemDAOImpl implements ItemDAO {

  @Inject
  private DALBackendServices dalBackendServices;
  @Inject
  private DomainFactory domainFactory;

  /**
   * Get in the database an item matching the given id.
   *
   * @param id the item's id
   * @return an itemDTO; null if no item found in the database.
   */
  @Override
  public ItemDTO getOneItemWithUserFromItemId(int id) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND i.item_id = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, id);
      return getItemDTOFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database all the items as well as the user who proposed the item.
   *
   * @return a list of itemDTO; null if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllItemsWithUser() {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database all the items as well as the user who proposed the item.
   *
   * @param id the user's id
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllItemsWithUserFromUserId(int id) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i, pae.users u, pae.photos p, pae.item_types t, pae.photos p2,
        pae.availabilities a
        WHERE u.user_id = i.offering_member AND i.type = t.type_id
        AND u.profile_picture = p2.photo_id
        AND i.meeting_date = a.availability_id AND i.photo = p.photo_id
        AND u.user_id = ?
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setInt(1, id);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database all proposed items.
   *
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllOfferedItemsWithUser() {
    String sql = """
        SELECT i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, i.version, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND i.state = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, "proposed");
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database all items of the given state.
   *
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllStartingItemsWithUser(String state1, String state2, String state3) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND (i.state = ? OR i.state = ? OR i.state = ?)
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, state1);
      ps.setString(2, state2);
      ps.setString(3, state3);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get in the database all items of the given type.
   *
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllItemsOfTypeWithUser(String type) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND t.name = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, type);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Change in the database, the description and type of item.
   *
   * @param id          item's id.
   * @param description description to set for the item.
   * @param itemType    type to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateInformation(int id, String description, String itemType, int version) {
    String sql = """
        UPDATE pae.items
                SET description = ?, type= (SELECT type_id FROM pae.item_types WHERE name = ?),
                version = version + 1
                WHERE item_id = ? AND version = ?;
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, description);
      ps.setString(2, itemType);
      ps.setInt(3, id);
      ps.setInt(4, version);

      int rowsModified = ps.executeUpdate();
      if (rowsModified == 0) {
        if (getOneItemWithUserFromItemId(id) == null) {
          throw new IllegalBusinessException(
              new ElementNotFoundException("This item doesn't exist"));
        } else {
          throw new IllegalBusinessException(new ConflictException(
              "Item version in database doesn't match item version received"));
        }
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }


  /**
   * Change in the database, the state of an item.
   *
   * @param id    item's id.
   * @param state state to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateState(int id, String state) {
    String sql = """
        UPDATE pae.items
        SET state = ?
        WHERE item_id = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, state);
      ps.setInt(2, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to denied with a reason of the refuse.
   *
   * @param id              item's id.
   * @param state           state to set for the item.
   * @param reasonOfRefusal reason of refusal for the offered item.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateState(int id, String state, String reasonOfRefusal) {
    String sql = """
        UPDATE pae.items
        SET state = ?, reason_of_refusal = ?
        WHERE item_id = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, state);
      ps.setString(2, reasonOfRefusal);
      ps.setInt(3, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to 'confirmed'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateConfirmed(int id) {
    String sql = """
        UPDATE pae.items
        SET state = 'confirmed', decision_date = ?
        WHERE item_id = ?
        """;
    try {
      updateItemStateFromSQL(id, sql);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to 'in_store'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateInStore(int id) {
    String sql = """
        UPDATE pae.items
        SET state = 'in_store', store_deposit_date = ?
        WHERE item_id = ?
        """;
    try {
      updateItemStateFromSQL(id, sql);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to 'for_sale' and its price.
   *
   * @param id    item's id.
   * @param price price to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateForSale(int id, double price) {
    String sql = """
        UPDATE pae.items
        SET state = 'for_sale', price = ?
        WHERE item_id = ?
        """;
    try {
      try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
        ps.setDouble(1, price);
        ps.setInt(2, id);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to 'sold'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateSold(int id) {
    String sql = """
        UPDATE pae.items
        SET state = 'sold', selling_date = ?
        WHERE item_id = ?
        """;
    try {
      updateItemStateFromSQL(id, sql);
    } catch (SQLException e) {
      throw new FatalException(e);
    }

    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change in the database, the state of an item to 'removed'.
   *
   * @param id item's id.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateRemoved(int id) {
    String sql = """
        UPDATE pae.items
        SET state = 'removed', market_withdrawal_date = ?
        WHERE item_id = ?
        """;
    try {
      updateItemStateFromSQL(id, sql);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }

  /**
   * Change the photo of an item.
   *
   * @param id      item's id.
   * @param photoId id of the new photo.
   * @return the updated ItemDTO.
   */
  public ItemDTO changePhoto(int id, int photoId) {
    String sql = """
        UPDATE pae.items
        SET photo = ?
        WHERE item_id = ?
        """;

    try {
      PreparedStatement ps = dalBackendServices.getPreparedStatement(sql);
      ps.setInt(1, photoId);
      ps.setInt(2, id);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }

  /**
   * get ItemDTO from a prepared statement.
   *
   * @param ps the prepared statement
   * @return a ItemDTO containing all information about an item; null if no item found;
   */
  private ItemDTO getItemDTOFromPreparedStatement(PreparedStatement ps) {

    try (ResultSet rs = ps.executeQuery()) {
      if (!rs.next()) {
        return null;
      }

      ItemDTO item = domainFactory.getItemDTO();

      do {
        UserDTO user = domainFactory.getUserDTO();

        setItemAndUserFromResultSet(rs, user, item);

      } while (rs.next());

      return item;

    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * update an item's state with a prepared statement.
   *
   * @param id  the item's id that we would like to update
   * @param sql the SQL request in String
   * @throws SQLException if an error occurs while updating the data from the resultset
   */
  private void updateItemStateFromSQL(int id, String sql) throws SQLException {
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setDate(1, Date.valueOf(LocalDate.now()));
      ps.setInt(2, id);
      ps.executeUpdate();
    }
  }

  /**
   * get an ItemDTO list from a prepared statement.
   *
   * @param ps the prepared statement
   * @return an ItemDTO list containing all information about items; null if no item found;
   * @throws SQLException if an error occurs while getting the data from the resultset
   */
  private List<ItemDTO> getItemDTOListFromPreparedStatement(PreparedStatement ps)
      throws SQLException {
    List<ItemDTO> itemDTOList = new ArrayList<>();
    try (ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        UserDTO user = domainFactory.getUserDTO();
        ItemDTO item = domainFactory.getItemDTO();

        setItemAndUserFromResultSet(rs, user, item);

        itemDTOList.add(item);
      }
      return itemDTOList;
    }
  }

  /**
   * set the userDTO and itemDTO attributes with the resultset query.
   *
   * @param rs   a Resultset
   * @param user a UserDTO
   * @param item an ItemDTO
   * @throws SQLException if an error occurs while getting the data from the resultset
   */

  private void setItemAndUserFromResultSet(ResultSet rs, UserDTO user, ItemDTO item)
      throws SQLException {
    boolean isRegistered = true;
    if (rs.getString("item_phone_number") != null) {
      item.setPhoneNumber(rs.getString("item_phone_number"));
      isRegistered = false;
    }

    item.setVersion(rs.getInt("version"));
    item.setId(rs.getInt("item_id"));
    item.setPrice(rs.getDouble("price"));
    item.setName(rs.getString("name"));
    item.setType(rs.getString("type"));
    item.setDescription(rs.getString("description"));
    PhotoDTO photo = domainFactory.getPhotoDTO();
    photo.setId(rs.getInt("item_photo_id"));
    photo.setAccessPath(rs.getString("access_path"));
    item.setPhoto(photo);
    item.setState(rs.getString("state"));
    item.setReasonOfRefusal(rs.getString("reason_of_refusal"));

    if (isRegistered) {

      PhotoDTO profilePhoto = domainFactory.getPhotoDTO();
      profilePhoto.setId(rs.getInt("profile_photo_id"));
      profilePhoto.setAccessPath(rs.getString("profile_photo_name"));
      profilePhoto.setType(rs.getString("profile_photo_type"));

      user.setId(rs.getInt("user_id"));
      user.setFirstname(rs.getString("first_name"));
      user.setLastname(rs.getString("last_name"));
      user.setEmail(rs.getString("email"));
      user.setPassword(rs.getString("password"));
      user.setPhoneNumber(rs.getString("phone_number"));
      user.setProfilePhoto(profilePhoto);
      user.setRole(rs.getString("role"));
      user.setRegisterDate(LocalDate.parse(rs.getString("registration_date")));
      item.setOfferingMember(user);
    } else {
      item.setOfferingMember(null);
    }
    AvailabilityDTO availabilityDTO = domainFactory.getAvailabilityDTO();
    availabilityDTO.setDate(LocalDate.parse(rs.getString("availability_date")));
    item.setMeetingDate(availabilityDTO);
    item.setTimeSlot(rs.getString("time_slot"));
    if (rs.getString("decision_date") != null) {
      item.setDecisionDate(LocalDate.parse(rs.getString("decision_date")));
    }
    if (rs.getString("store_deposit_date") != null) {
      item.setStoreDepositDate(LocalDate.parse(rs.getString("store_deposit_date")));
    }
    if (rs.getString("market_withdrawal_date") != null) {
      item.setMarketWithdrawalDate(LocalDate.parse(rs.getString("market_withdrawal_date")));
    }
    if (rs.getString("selling_date") != null) {
      item.setSellingDate(LocalDate.parse(rs.getString("selling_date")));
    }
  }

  /**
   * Get in the database all types of item.
   *
   * @return a list of String; null if no items found in database
   */
  @Override
  public List<String> getAllItemTypes() {
    String sql = """
            SELECT type_id, name
            FROM pae.item_types
        """;
    List<String> typesList = new ArrayList<>();
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          typesList.add(rs.getString("name"));
        }
      }
      return typesList;
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Get all items in database that contains the word in their name.
   *
   * @param word a string
   * @return a list of itemDTO; null if no items found in database
   */
  public List<ItemDTO> getAllItemsThatContainsInTheirName(String word) {
    String sql = """
        SELECT i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, i.version, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND lOWER(i.name) LIKE ? AND
        (i.state = 'in_store' OR i.state = 'for_sale' OR i.state = 'sold')
        """;
    String infix = "%" + word.toLowerCase() + "%";
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, infix);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  /**
   * Add one offer item in database.
   *
   * @param item an itemDTO
   * @return an itemDTO; null if no items found in database
   */
  @Override
  public ItemDTO addOneOfferItem(ItemDTO item) {
    String sql;
    boolean isAuthentificated = false;
    if (item.getOfferingMember() == null) {
      sql = """
          INSERT INTO pae.items VALUES (DEFAULT, ?, ?, ?, ?, NULL, ?, NULL, NULL,
          ?, ?, ?, NULL, NULL, NULL, NULL) RETURNING item_id
          """;
    } else {
      isAuthentificated = true;
      sql = """
          INSERT INTO pae.items VALUES (DEFAULT, ?, ?, ?, ?, NULL, ?, NULL,
          ?, NULL, ?, ?, NULL, NULL, NULL, NULL) RETURNING item_id
          """;
    }
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, item.getName());
      ps.setInt(2, getIdOfItemType(item.getType()));
      ps.setString(3, item.getDescription());
      ps.setInt(4, item.getPhoto().getId());
      ps.setString(5, "proposed");
      if (isAuthentificated) {
        ps.setInt(6, item.getOfferingMember().getId());
        ps.setInt(7, item.getMeetingDate().getId());
        ps.setString(8, item.getTimeSlot());
      } else {
        ps.setString(6, item.getPhoneNumber());
        ps.setInt(7, item.getMeetingDate().getId());
        ps.setString(8, item.getTimeSlot());
      }

      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return null;
        }
        return getOneItemWithUserFromItemId(rs.getInt("item_id"));
      }

    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

  private int getIdOfItemType(String nameType) {
    String sql = """
        SELECT type_id, name
        FROM pae.item_types
        WHERE name = ?
        """;
    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, nameType);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          return -1;
        }
        return rs.getInt("type_id");
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Change in the database, the state of an item to 'sold' and set its price.
   *
   * @param id    item's id.
   * @param price price to set for the item.
   * @return an itemDTO; null if no item in the database.
   */
  public ItemDTO updateStateSoldAndSetPrice(int id, double price) {
    String sql = """
        UPDATE pae.items
        SET state = 'sold', price = ?
        WHERE item_id = ?
        """;
    try {
      try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
        ps.setDouble(1, price);
        ps.setInt(2, id);
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new FatalException(e);
    }
    return getOneItemWithUserFromItemId(id);
  }


  /**
   * Get in the database all the items of a certain type as well as the user.
   *
   * @param type the item's type
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllItemsOfATypeWithUser(String type) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND t.name = ? AND
        (i.state = 'in_store' OR i.state = 'for_sale' OR i.state = 'sold');
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, type);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }


  /**
   * Get in the database all the items in a state as well as the user.
   *
   * @param state the item's state
   * @return a list of itemDTO; an empty list if no item in the database.
   */
  @Override
  public List<ItemDTO> getAllItemsInAStateWithUser(String state) {
    String sql = """
        SELECT i.version, i.item_id, i.name, t.name AS "type",
        i.description, p.access_path, p.photo_id AS "item_photo_id", i.price, i.state,
        i.reason_of_refusal, u.user_id, u.first_name, u.last_name, u.email, u.password,
        u.phone_number, p2.photo_id AS "profile_photo_id", p2.access_path AS "profile_photo_name",
        p2.type AS "profile_photo_type", u.role, u.registration_date,
        i.phone_number AS "item_phone_number", a.availability_date, a.availability_id, i.time_slot,
        i.decision_date, i.store_deposit_date, i.market_withdrawal_date, i.selling_date
        FROM pae.items i
        LEFT OUTER JOIN pae.users u on u.user_id = i.offering_member
        LEFT OUTER JOIN pae.photos p2 on u.profile_picture = p2.photo_id,
        pae.item_types t, pae.photos p, pae.availabilities a
        WHERE i.type = t.type_id AND i.photo = p.photo_id AND i.meeting_date = a.availability_id
        AND i.state = ?;
        """;

    try (PreparedStatement ps = dalBackendServices.getPreparedStatement(sql)) {
      ps.setString(1, state);
      return getItemDTOListFromPreparedStatement(ps);
    } catch (SQLException e) {
      throw new FatalException(e);
    }
  }

}
