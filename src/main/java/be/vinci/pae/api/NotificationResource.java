package be.vinci.pae.api;

import be.vinci.pae.domain.NotificationDTO;
import be.vinci.pae.domain.NotificationUCC;
import be.vinci.pae.utils.MyLogger;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.logging.Level;

/**
 * NotificationResource interface containing the REST API methods.
 */
@Singleton
@Path("/notifications")
public class NotificationResource {
  @Inject
  private NotificationUCC notificationUCC;

  /**
   * get all notifications of a user.
   *
   * @param id user's id
   * @return a list of all notifications as a JSON
   */
  @GET
  @Path("/all")
  @Produces(MediaType.APPLICATION_JSON)
  public List<NotificationDTO> getAllNotificationsOfUser(@QueryParam("id") int id) {
    MyLogger.getInstance().getLogger()
        .log(Level.INFO, "GET request /notifications/all/" + id);
    return notificationUCC.viewAllNotificationsOfUser(id);
  }

  /**
   * mark a notification as read.
   *
   * @param userId  user's id
   */
  @PATCH
  @Path("/markAsRead/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void markAsRead(@PathParam("userId") int userId) {
    MyLogger.getInstance().getLogger()
        .log(Level.INFO, "PATCH request /notifications/markAsRead/" + userId);
    notificationUCC.markAsRead(userId);
  }

  /**
   * delete a notification.
   *
   * @param id      notification's id
   * @param userId  user's id
   */
  @DELETE
  @Path("/delete/{id}/{userId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void deleteNotification(@PathParam("id") int id, @PathParam("userId") int userId) {
    MyLogger.getInstance().getLogger()
        .log(Level.INFO, "DELETE request /notifications/delete/" + id + "/" + userId);
    notificationUCC.deleteSingleNotification(id, userId);
  }

  /**
   * delete all notifications of an item.
   *
   * @param id item's id
   */
  @DELETE
  @Path("/deleteAll/{itemId}")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public void deleteAllNotification(@PathParam("itemId") int id) {
    MyLogger.getInstance().getLogger()
        .log(Level.INFO, "DELETE request /notifications/deleteAll/" + id);
    notificationUCC.deleteItemNotifications(id);
  }
}
