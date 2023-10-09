import Navigate from "../Components/Router/Navigate";

async function updateNotifications(userID) {
  const notifications = await fetch(`${process.env.API_BASE_URL}/notifications/all?id=${userID}`)
      .then(response => response.json());

  const notificationsList = document.getElementById('notificationsList');
  notificationsList.innerHTML = "";

  if (notifications.length === 0) {
    const notificationItem = document.createElement('li');
    notificationItem.classList.add('list-group-item');
    notificationItem.classList.add('text-center');
    notificationItem.innerHTML = `Vous n'avez pas de notifications.`;
    notificationsList.appendChild(notificationItem);
  } else {
    notifications.reverse().forEach(notification => {
      const notificationItem = document.createElement('li');
      notificationItem.classList.add('list-group-item');
      notificationItem.classList.add('notification-item');

      if (notification.type === "decision") {
        notificationItem.innerHTML = `
          <div class="row">
            <div class="col-12 notification-row">
              <div class="row">
                <div class=${notification.isRead ? "col-10" : "col-11"}>
                  <p class="fw-bold can-link">${notification.itemState === "denied" ? "Rejeté" : "Accepté"}</p>
                </div>
                <div class=${notification.isRead ? "col-1" : "d-none"}>
                  <p class="fw-lighter fst-italic text-muted text-end can-link">Lue</p>
                </div>
                <div class="col-1 text-end">
                  <i class="fa fa-close close" data-notification-id=${notification.id}></i>
                </div>
              </div>
              <div class="row can-link">
                <div class="col-12">
                  <p>${"Votre objet ".concat("\"", notification.itemName, "\" a été ", (notification.itemState === "denied" ? "rejeté." : "accepté."))}</p>
                </div>
              </div>
            </div>
          </div>
        `;
      } else {
        notificationItem.innerHTML = `
          <div class="row">
            <div class="col-12 notification-row">
              <div class="row">
                <div class=${notification.isRead ? "col-10" : "col-11"}>
                  <p class="fw-bold can-link">Nouvelle proposition d'objet</p>
                </div>
                <div class=${notification.isRead ? "col-1" : "d-none"}>
                  <p class="fw-lighter fst-italic text-muted text-end can-link">Lue</p>
                </div>
                <div class="col-1 text-end">
                  <i class="fa fa-close close" data-notification-id=${notification.id}></i>
                </div>
              </div>
              <div class="row can-link">
                <div class="col-12">
                  <p>Un nouvel objet du nom "${notification.itemName}" a été proposé.</p>
                </div>
              </div>
            </div>
          </div>
        `;
      }

      notificationItem.querySelectorAll('.can-link').forEach(element => {
        element.addEventListener('click', () => {
          if (notification.type === "decision") {
            Navigate('/profile');
          } else {
            Navigate(`/item?id=${notification.itemID}`);
          }
        });
      });

      notificationItem.querySelector('.close').addEventListener('click', async (event) => {
        await fetch(`${process.env.API_BASE_URL}/notifications/delete/${event.target.dataset.notificationId}/${userID}`, {
          method: 'DELETE',
        });
        updateNotifications(userID);
      });

      notificationsList.appendChild(notificationItem);
    });
  }

  const pill = document.getElementById('notificationPill');
  const numberNotRead = notifications.filter(notification => !notification.isRead).length;
  pill.innerHTML = numberNotRead;
  if (numberNotRead === 0) {
    pill.classList.add('d-none');
  } else {
    pill.classList.remove('d-none');
  }
}

async function markAllNotificationsAsRead(userID) {
  await fetch(`${process.env.API_BASE_URL}/notifications/markAsRead/${userID}`, {
    method: 'PATCH',
  });
}

export {
  markAllNotificationsAsRead,
  updateNotifications
}