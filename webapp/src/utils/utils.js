const addZeroInFrontIfUnder2Digits = (myNumberInString) =>
    myNumberInString.toString().length < 2 ? `0${myNumberInString}` : myNumberInString;

const replaceDotWithComma = (number) => {
  const numberAsString = `${number}`;
  return numberAsString.replace(/\./g, ',');
}

export {
  addZeroInFrontIfUnder2Digits,
  replaceDotWithComma,
}