package be.vinci.pae.api;

import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.Logged;
import be.vinci.pae.domain.AvailabilityDTO;
import be.vinci.pae.domain.AvailabilityUCC;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.ItemDTO;
import be.vinci.pae.domain.ItemUCC;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.PhotoUCC;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.InternalServerException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * ItemResource class.
 */
@Singleton
@Path("/items")
public class ItemResource {

  @Inject
  private ItemUCC myItemUCC;
  @Inject
  private PhotoUCC myPhotoUCC;
  @Inject
  private AvailabilityUCC availabilityUCC;
  @Inject
  private DomainFactory myDomainFactory;

  /**
   * get all item form a user resource path.
   *
   * @param id item's id
   * @return a list of all items as a JSON
   */
  @GET
  @Logged
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public ItemDTO getOne(@PathParam("id") int id) {
    ItemDTO item;
    try {
      item = myItemUCC.getItemInformation(id);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return item;
  }

  /**
   * get all items resource path.
   *
   * @return a list of all items as a JSON
   */
  @GET
  @Logged
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin", "helper"})
  public List<ItemDTO> getAll() {
    List<ItemDTO> itemList;
    try {
      itemList = myItemUCC.viewAllItems();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemList;
  }

  /**
   * get all items from a user resource path.
   *
   * @param id user's id
   * @return a list of all items as a JSON
   */
  @GET
  @Logged
  @Path("/all/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<ItemDTO> getAll(@PathParam("id") int id) {
    List<ItemDTO> itemList;
    try {
      itemList = myItemUCC.viewAllItemsFromUser(id);
    } catch (FatalException e) {
      throw e;
    }
    return itemList;
  }


  /**
   * get all offered item resource path.
   *
   * @return a list of all offered items as a JSON
   */
  @GET
  @Logged
  @Path("/allOffered")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public List<ItemDTO> getOfferedItems() {
    List<ItemDTO> itemList = null;
    try {
      itemList = myItemUCC.viewOfferedItems();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemList;
  }

  /**
   * get all items resource path.
   *
   * @return a list of all items in the state as a JSON
   */
  @GET
  @Logged
  @Path("/startingitems")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getAllInState() {
    List<ItemDTO> itemList = null;
    try {
      itemList = myItemUCC.viewAllStartingItems();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemList;
  }

  /**
   * get all items of a type resource path.
   *
   * @param type item's type
   * @return a list of all items in the state as a JSON
   */
  @GET
  @Logged
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getAllOfType(@DefaultValue("") @QueryParam("type") String type) {
    List<ItemDTO> itemList = null;
    if (!type.equals("")) {
      try {
        itemList = myItemUCC.viewAllItemsOfType(type);
      } catch (FatalException | IllegalBusinessException e) {
        throw e;
      }
    }
    return itemList;
  }

  /**
   * search all items that contains the word(name) in their name.
   *
   * @param name the word name
   * @return a list of all items in the state as a JSON
   */
  @GET
  @Logged
  @Path("/search")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> searchByName(@DefaultValue("") @QueryParam("name") String name) {
    List<ItemDTO> itemList = null;
    try {
      itemList = myItemUCC.searchItemsByName(name);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemList;
  }


  /**
   * update the photo of the item.
   *
   * @param id              the item id to edit.
   * @param file            an InputStream.
   * @param fileDisposition information about the file.
   * @return the edited item as a JSON.
   */
  @PATCH
  @Logged
  @Path("/updatePhoto/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"helper", "admin"})
  public ItemDTO updatePhoto(@PathParam("id") int id, @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition) {

    ItemDTO editedItem;
    PhotoDTO photoCreated = null;

    try {
      if (file != null && fileDisposition != null) {
        String fileName = fileDisposition.getFileName();
        photoCreated = myPhotoUCC.addPhoto(file, fileName, "item_photo");
      }
    } catch (IllegalBusinessException e) {
      throw new InternalServerException("Error in I/O operations");
    }
    try {
      editedItem = myItemUCC.setPhoto(id, photoCreated);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return editedItem;
  }


  /**
   * update the description and type of the object.
   *
   * @param json a Json node containing the description and type.
   * @param id   item's id
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/updateInformation/{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"helper", "admin"})
  public ItemDTO updateInformation(JsonNode json, @PathParam("id") int id) {
    if (!json.hasNonNull("description") || !json.hasNonNull("type")) {
      throw new WebApplicationException("Description and type required", Status.BAD_REQUEST);
    }

    ItemDTO itemToUpdate;
    String description = json.get("description").asText();
    String type = json.get("type").asText();
    int version = json.get("version").asInt();
    try {
      itemToUpdate = myItemUCC.setItemInformation(id, description, type, version);
    } catch (FatalException e) {
      throw e;
    } catch (IllegalBusinessException e) {
      if (e.getCause().getClass().equals(ConflictException.class)) {
        throw new WebApplicationException("Item has been modified", Status.CONFLICT);
      } else if (e.getCause().getClass().equals(NotFoundException.class)) {
        throw new WebApplicationException("Item not found", Status.NOT_FOUND);
      }
      throw e;

    }
    return itemToUpdate;
  }

  /**
   * update the state of the item.
   *
   * @param state new state to set for the item
   * @param id    item's id
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/updateState/{id}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"helper", "admin"})
  public ItemDTO updateState(String state, @PathParam("id") int id) {
    ItemDTO itemToUpdate;
    try {
      switch (state) {
        case "in_workshop" -> {
          try {
            itemToUpdate = myItemUCC.setItemStateInWorkshop(id);
          } catch (FatalException e) {
            throw e;
          }
        }
        case "in_store" -> {
          try {
            itemToUpdate = myItemUCC.setItemStateInStore(id);
          } catch (FatalException e) {
            throw e;
          }
        }
        case "sold" -> {
          try {
            itemToUpdate = myItemUCC.setItemStateSold(id);
          } catch (FatalException e) {
            throw e;
          }
        }
        case "removed" -> {
          try {
            itemToUpdate = myItemUCC.setItemStateRemoved(id);
          } catch (FatalException e) {
            throw e;
          }
        }
        default -> throw new WebApplicationException("Unexpected value: " + state,
            Status.INTERNAL_SERVER_ERROR);
      }
    } catch (IllegalBusinessException e) {
      throw e;
    }
    return itemToUpdate;
  }

  /**
   * update the state of the item.
   *
   * @param id    item's id
   * @param price item's price
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/updateState/{id}/{price}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"helper", "admin"})
  public ItemDTO updateState(@PathParam("id") int id, @PathParam("price") double price) {
    ItemDTO itemToUpdate;
    try {
      itemToUpdate = myItemUCC.setItemStateForSale(id, price);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemToUpdate;
  }

  /**
   * update the state of the item to 'denied'.
   *
   * @param id              item's id
   * @param reasonOfRefusal reason of refusal for the offered item
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/deny/{id}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public ItemDTO denyItem(@PathParam("id") int id, String reasonOfRefusal) {
    ItemDTO itemToUpdate;
    try {
      itemToUpdate = myItemUCC.setItemStateDenied(id, reasonOfRefusal);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemToUpdate;
  }

  /**
   * update the state of the item to 'confirmed'.
   *
   * @param id item's id
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/confirm/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public ItemDTO confirmItem(@PathParam("id") int id) {
    ItemDTO itemToUpdate;
    try {
      itemToUpdate = myItemUCC.setItemStateConfirmed(id);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemToUpdate;
  }

  /**
   * get all item types resource path.
   *
   * @return a list of all String
   */
  @GET
  @Logged
  @Path("/allTypes")
  @Produces(MediaType.APPLICATION_JSON)
  public List<String> getAllItemTypes() {
    List<String> itemTypesList;
    try {
      itemTypesList = myItemUCC.viewAllItemTypes();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemTypesList;
  }

  /**
   * add offered item.
   *
   * @param file            an InputStream
   * @param fileDisposition information about the file.
   * @param name            the name field in the form.
   * @param type            the item's type field in the form.
   * @param description     the description field in the form.
   * @param phoneNumber     the phone field in the form.
   * @param date            the availability date field in the form.
   * @param timeSlot        the timeSLot field in the form.
   * @return a list of all String
   */
  @POST
  @Logged
  @Path("/offerItem")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public ItemDTO offerAnItem(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("name") String name, @FormDataParam("type") String type,
      @FormDataParam("description") String description,
      @FormDataParam("phoneNumber") String phoneNumber, @FormDataParam("meetingDate") String date,
      @FormDataParam("timeSlot") String timeSlot) {
    verifyAttributesOfferItem(file, fileDisposition, name, type, description, date, timeSlot);
    ItemDTO itemOffered;
    ItemDTO itemToOffer = myDomainFactory.getItemDTO();
    AvailabilityDTO availabilityDate = myDomainFactory.getAvailabilityDTO();
    availabilityDate.setDate(LocalDate.parse(date));
    availabilityDate.setId(availabilityUCC.getIdOfOneAvailabilityDate(LocalDate.parse(date)));
    setPhotoToItem(itemToOffer, name, type, description, fileDisposition, file);
    itemToOffer.setPhoneNumber(phoneNumber);
    itemToOffer.setMeetingDate(availabilityDate);
    itemToOffer.setTimeSlot(timeSlot);
    try {
      itemOffered = myItemUCC.addOneOfferedItem(itemToOffer);
    } catch (FatalException e) {
      throw e;
    }
    return itemOffered;
  }

  /**
   * add offered item.
   *
   * @param request         the request
   * @param file            an InputStream
   * @param fileDisposition information about the file.
   * @param name            the name field in the form.
   * @param type            the item's type field in the form.
   * @param description     the description field in the form.
   * @param date            the availability date field in the form.
   * @param timeSlot        the timeSLot field in the form.
   * @return a list of all String
   */
  @POST
  @Logged
  @Path("/offerItemUser")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public ItemDTO offerAnItemConnected(@Context ContainerRequest request,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @FormDataParam("name") String name, @FormDataParam("type") String type,
      @FormDataParam("description") String description, @FormDataParam("meetingDate") String date,
      @FormDataParam("timeSlot") String timeSlot) {
    verifyAttributesOfferItem(file, fileDisposition, name, type, description, date, timeSlot);
    ItemDTO itemOffered;
    ItemDTO itemToOffer = myDomainFactory.getItemDTO();
    AvailabilityDTO availabilityDate = myDomainFactory.getAvailabilityDTO();
    availabilityDate.setDate(LocalDate.parse(date));
    availabilityDate.setId(availabilityUCC.getIdOfOneAvailabilityDate(LocalDate.parse(date)));
    setPhotoToItem(itemToOffer, name, type, description, fileDisposition, file);
    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
    itemToOffer.setOfferingMember(authenticatedUser);
    itemToOffer.setMeetingDate(availabilityDate);
    itemToOffer.setTimeSlot(timeSlot);
    try {
      itemOffered = myItemUCC.addOneOfferedItem(itemToOffer);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemOffered;
  }

  private void verifyAttributesOfferItem(InputStream file,
      FormDataContentDisposition fileDisposition, String name, String type, String description,
      String date, String timeSlot) {
    if (file == null || fileDisposition == null || name == null || type == null
        || description == null || date == null || timeSlot == null || name.isBlank()
        || type.isBlank() || description.isBlank() || date.isBlank() || timeSlot.isBlank()) {
      throw new WebApplicationException("bad request", Status.BAD_REQUEST);
    }
  }

  private void setPhotoToItem(ItemDTO itemToOffer, String name, String type, String description,
      FormDataContentDisposition fileDisposition, InputStream file) {
    PhotoDTO photoCreated;
    itemToOffer.setName(name);
    itemToOffer.setType(type);
    itemToOffer.setDescription(description);
    String fileName = fileDisposition.getFileName();
    // convert the JSON string to ObjectNode
    try {
      photoCreated = myPhotoUCC.addPhoto(file, fileName, "item_photo");
    } catch (IllegalBusinessException e) {
      throw e;
    }
    itemToOffer.setPhoto(photoCreated);
  }

  /**
   * Route to update the state of the item to 'sold' and set price of the item.
   *
   * @param id    item's id
   * @param price price to set for this item
   * @return an item as a JSON
   */
  @PATCH
  @Logged
  @Path("/sellFromStore/{id}/{price}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public ItemDTO sellItem(@PathParam("id") int id, @PathParam("price") double price) {
    ItemDTO itemToUpdate;
    try {
      itemToUpdate = myItemUCC.setItemStateSoldWithPrice(id, price);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return itemToUpdate;
  }

  /**
   * Get all items of a type.
   *
   * @param type the item's type
   * @return a list of all items in the state as a JSON
   */
  @GET
  @Logged
  @Path("/typesearch")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getAllByType(@DefaultValue("") @QueryParam("type") String type) {
    List<ItemDTO> itemList = null;
    if (!type.equals("")) {
      try {
        itemList = myItemUCC.getAllItemsOfAType(type);
      } catch (FatalException | IllegalBusinessException e) {
        throw e;
      }
    }
    return itemList;
  }

  /**
   * Get all items of a type.
   *
   * @param state the item's state
   * @return a list of all items in the state as a JSON
   */
  @GET
  @Logged
  @Path("/statesearch")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public List<ItemDTO> getAllInState(@DefaultValue("") @QueryParam("state") String state) {
    List<ItemDTO> itemList = null;
    if (!state.equals("")) {
      try {
        itemList = myItemUCC.getAllItemsOfAState(state);
      } catch (FatalException | IllegalBusinessException e) {
        throw e;
      }
    }
    return itemList;
  }

}


