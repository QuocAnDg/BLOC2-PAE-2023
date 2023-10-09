function itemStateFrenchTranslation(state) {
  switch (state) {
    case 'proposed':
      return 'Proposé';
    case 'confirmed':
      return 'Accepté';
    case 'denied':
      return 'Refusé';
    case 'in_workshop':
      return 'En atelier';
    case 'in_store':
      return 'En magasin';
    case 'for_sale':
      return 'En vente';
    case 'sold':
      return 'Vendu';
    case 'removed':
      return 'Retiré';
    default:
      return state;
  }
}

function itemTimeSlotFrenchTranslation(word) {
  if (word === "daytime") {
    return "Matinée";
  }  
  return "Après-midi"
}

function userRoleFrenchTranslation(word){
  if (word === "user") {
    return "Utilisateur";
  }
  if (word === "admin") return "Responsable";
  return "Aidant";
}

export { itemStateFrenchTranslation, itemTimeSlotFrenchTranslation, userRoleFrenchTranslation }