function formatPrice(price) {
  return price.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
}

function isBottom() {
  return (window.innerHeight + window.scrollY) >= document.body.offsetHeight
      - 500 && !isLoading
}
