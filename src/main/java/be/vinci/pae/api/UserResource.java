package be.vinci.pae.api;


import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.filters.Logged;
import be.vinci.pae.domain.DomainFactory;
import be.vinci.pae.domain.PhotoDTO;
import be.vinci.pae.domain.PhotoUCC;
import be.vinci.pae.domain.UserDTO;
import be.vinci.pae.domain.UserUCC;
import be.vinci.pae.utils.Config;
import be.vinci.pae.utils.exceptions.ConflictException;
import be.vinci.pae.utils.exceptions.FatalException;
import be.vinci.pae.utils.exceptions.IllegalBusinessException;
import be.vinci.pae.utils.exceptions.InternalServerException;
import be.vinci.pae.utils.exceptions.UnauthorizedException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response.Status;
import java.io.InputStream;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.server.ContainerRequest;

/**
 * UserResource class.
 */
@Singleton
@Path("/user")
public class UserResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));

  private final ObjectMapper jsonMapper = new ObjectMapper()
      .registerModule(new JavaTimeModule());
  @Inject
  private UserUCC myUserUCC;
  @Inject
  private PhotoUCC myPhotoUCC;
  @Inject
  private DomainFactory myDomainFactory;

  /**
   * Login path.
   *
   * @param json object containing login and password
   * @return a User and their token as a JsonNode
   */
  @POST
  @Logged
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public ObjectNode login(JsonNode json) {
    UserDTO user;
    // Get and check credentials
    if (!json.hasNonNull("email") || !json.hasNonNull("password")) {
      throw new WebApplicationException("Email or password required", Status.UNAUTHORIZED);
    }
    String email = json.get("email").asText();
    String password = json.get("password").asText();

    // Try to log in
    try {
      user = myUserUCC.login(email, password);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    String token;
    try {
      if (user == null) {
        throw new UnauthorizedException("Email or password incorrect");
      }
      token = JWT.create().withIssuer("auth0")
          .withExpiresAt(Date.from(Instant.now().plus(30, ChronoUnit.DAYS)))
          .withClaim("user", user.getId())
          .sign(this.jwtAlgorithm);
      ObjectNode publicUser = jsonMapper.createObjectNode()
          .put("token", token)
          .putPOJO("user", user);
      return publicUser;

    } catch (Exception e) {
      throw new UnauthorizedException("Unable to create token");
    }
  }


  /**
   * Register path.
   *
   * @param file            an InputStream
   * @param fileDisposition information about the file.
   * @param firstname       the firstname field in the form.
   * @param lastname        the lastname field in the form.
   * @param email           the email field in the form.
   * @param phone           the phone field in the form.
   * @param password        the password field in the form.
   * @param avatarPath      the choosen avatar.
   * @return the UserDTO created.
   */
  @POST
  @Logged
  @Path("register")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  public UserDTO register(@FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @DefaultValue("") @FormDataParam("firstname") String firstname,
      @DefaultValue("") @FormDataParam("lastname") String lastname,
      @DefaultValue("") @FormDataParam("email") String email,
      @DefaultValue("") @FormDataParam("phone") String phone,
      @DefaultValue("") @FormDataParam("password") String password,
      @FormDataParam("avatarPath") String avatarPath
  ) {
    if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || phone.isBlank()
        || password.isBlank()) {
      throw new WebApplicationException("Email or password required", Status.UNAUTHORIZED);
    }
    if (!email.matches("^(.+)@(.+)\\.(.+)$")
        || !phone.matches("^[0-9.\\/( )]*$")) {
      throw new WebApplicationException("Some fields are incorrectly completed",
          Status.BAD_REQUEST);
    }
    UserDTO userCreated;
    PhotoDTO photoCreated;
    UserDTO userToCreate = myDomainFactory.getUserDTO();
    userToCreate.setFirstname(firstname);
    userToCreate.setLastname(lastname);
    userToCreate.setEmail(email);
    userToCreate.setPhoneNumber(phone);
    userToCreate.setPassword(password);

    try {
      if (file != null && fileDisposition != null) {
        String fileName = fileDisposition.getFileName();
        photoCreated = myPhotoUCC.addPhoto(file, fileName, "profile_picture");
        userToCreate.setProfilePhoto(photoCreated);
      } else {
        photoCreated = myPhotoUCC.copyAvatar(avatarPath);
        userToCreate.setProfilePhoto(photoCreated);
      }
    } catch (IllegalBusinessException e) {
      throw new InternalServerException("Error in I/O operations");
    }
    userToCreate.setProfilePhoto(photoCreated);
    try {
      userCreated = myUserUCC.register(userToCreate);
    } catch (FatalException | IllegalBusinessException e) {
      throw new WebApplicationException("Register failed", Status.UNAUTHORIZED);
    }

    return userCreated;
  }

  /**
   * Auth route.
   *
   * @param request a ContainerRequest object
   * @return return a userDTO
   */

  @GET
  @Logged
  @Path("auth")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public UserDTO auth(@Context ContainerRequest request) {

    UserDTO authenticatedUser = (UserDTO) request.getProperty("user");
    return authenticatedUser;
  }

  /**
   * Get all users.
   *
   * @return a list containing all the users.
   */
  @GET
  @Logged
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public List<UserDTO> getAll() {
    List<UserDTO> usersDTO;
    try {
      usersDTO = myUserUCC.findAll();
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return usersDTO;
  }

  /**
   * Update the role of a user from "user" to "helper" or "admin".
   *
   * @param id   the user's id.
   * @param json the json sent.
   * @return the User with the updated role.
   */
  @PATCH
  @Logged
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize(roles = {"admin"})
  public UserDTO updateRole(@PathParam("id") int id, JsonNode json) {
    UserDTO updatedUser;
    if (id <= 0 || !json.get("newRole").asText().equals("helper") && !json.get("newRole").asText()
        .equals("admin")) {
      throw new WebApplicationException("Incorrect user id or role provided", Status.BAD_REQUEST);
    }
    String newRole = json.get("newRole").asText();
    try {
      updatedUser = myUserUCC.updateRole(id, newRole);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return updatedUser;
  }

  /**
   * Edit user route.
   *
   * @param id              the user id to edit.
   * @param file            an InputStream
   * @param fileDisposition information about the file.
   * @param firstname       the firstname field in the form.
   * @param lastname        the lastname field in the form.
   * @param email           the email field in the form.
   * @param phone           the phone field in the form.
   * @param avatarPath      the choosen avatar.
   * @param version         the version of the user.
   * @return the edited UserDTO.
   */
  @PATCH
  @Logged
  @Path("/editUser/{id}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public UserDTO editUser(@PathParam("id") int id,
      @FormDataParam("file") InputStream file,
      @FormDataParam("file") FormDataContentDisposition fileDisposition,
      @DefaultValue("") @FormDataParam("firstname") String firstname,
      @DefaultValue("") @FormDataParam("lastname") String lastname,
      @DefaultValue("") @FormDataParam("email") String email,
      @DefaultValue("") @FormDataParam("phone") String phone,
      @DefaultValue("") @FormDataParam("avatarPath") String avatarPath,
      @FormDataParam("version") int version) {

    if (id <= 0 || firstname.isBlank() && lastname.isBlank() && email.isBlank()
        && phone.isBlank()) {
      throw new WebApplicationException("Lacks of mandatory info", Status.BAD_REQUEST);
    }

    if (!email.matches("^(.+)@(.+)\\.(.+)$")
        || !phone.matches("^[0-9.\\/( )]*$")) {
      throw new WebApplicationException("Some fields are incorrectly completed",
          Status.BAD_REQUEST);
    }

    UserDTO editedUser;
    PhotoDTO photoCreated = null;
    UserDTO userToEdit = myDomainFactory.getUserDTO();
    userToEdit.setId(id);
    userToEdit.setFirstname(firstname);
    userToEdit.setLastname(lastname);
    userToEdit.setEmail(email);
    userToEdit.setPhoneNumber(phone);
    userToEdit.setVersion(version);

    try {
      if (file != null && fileDisposition != null) {
        String fileName = fileDisposition.getFileName();
        photoCreated = myPhotoUCC.addPhoto(file, fileName, "profile_picture");
        userToEdit.setProfilePhoto(photoCreated);
      } else if (!avatarPath.isBlank()) {
        photoCreated = myPhotoUCC.copyAvatar(avatarPath);
        userToEdit.setProfilePhoto(photoCreated);
      }
    } catch (IllegalBusinessException e) {
      throw new InternalServerException("Error in I/O operations");
    }
    try {
      editedUser = myUserUCC.editUser(userToEdit);
    } catch (FatalException e) {
      throw e;
    } catch (IllegalBusinessException e) {
      if (e.getCause().getClass().equals(ConflictException.class)) {
        throw new WebApplicationException("User has been modified", Status.CONFLICT);
      }
      throw e;

    }
    return editedUser;
  }

  /**
   * Change password route.
   *
   * @param id   the user's id.
   * @param json object containing user's old and new password.
   * @return the user with his new password.
   */
  @PATCH
  @Path("/changePassword/{id}")
  @Logged
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  public UserDTO changePassword(@PathParam("id") int id, JsonNode json) {

    if (id <= 0) {
      throw new WebApplicationException("Lacks of mandatory info", Status.BAD_REQUEST);
    }
    if (!json.hasNonNull("oldPassword") || !json.hasNonNull("newPassword")) {
      throw new WebApplicationException("Old password and new password required",
          Status.UNAUTHORIZED);
    }
    String oldPassword = json.get("oldPassword").asText();
    String newPassword = json.get("newPassword").asText();
    UserDTO user;

    try {
      user = myUserUCC.changePassword(id, oldPassword, newPassword);
    } catch (FatalException | IllegalBusinessException e) {
      throw e;
    }
    return user;
  }
}
