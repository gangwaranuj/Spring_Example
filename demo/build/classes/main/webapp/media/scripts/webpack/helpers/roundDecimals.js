export default (num, decimalPlaces) => {
    // http://stackoverflow.com/questions/11832914/round-to-at-most-2-decimal-places-in-javascript
    return Math.round((num + 0.00001) * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
};
