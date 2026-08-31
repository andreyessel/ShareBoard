import { useState, useEffect } from "react"

function ListingFeedPage() {
  const [listings, setListings] = useState([])

  useEffect(() => {
    const fetchListings = async () => {
       const response = await fetch("http://localhost:8080/api/listings");
        if (response.ok) {
            setListings(await response.json());
       } else {
            setListings([]);
        }
    };

    fetchListings()
  }, [])

  return (
    <>
      <h1>Listings</h1>
      <ul>
        {listings.map(listing => (
          <li key={listing.listingId}>{listing.type} ~ quantity {listing.quantity} : remaining {listing.quantityRemaining}   {listing.status}</li>
        ))}
      </ul>
    </>
  )
}

export default ListingFeedPage