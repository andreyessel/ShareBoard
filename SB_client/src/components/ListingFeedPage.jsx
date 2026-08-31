import { useState, useEffect } from "react"

function ListingFeedPage() {
  const [listings, setListings] = useState([])

  useEffect(() => {
    async function fetchListings() {
      const response = await fetch("http://localhost:8080/api/listings")
      const data = await response.json()
      setListings(data)
    }

    fetchListings()
  }, [])

  return (
    <div>
      <h1>Listings</h1>
      <ul>
        {listings.map(listing => (
          <li key={listing.listingId}>{listing.type} — quantity {listing.quantity}</li>
        ))}
      </ul>
    </div>
  )
}

export default ListingFeedPage