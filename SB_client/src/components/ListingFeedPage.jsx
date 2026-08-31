import { useState, useEffect } from "react"
import ListingCard from "./ListingCard";

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
          <ListingCard key={listing.listingId} listing={listing} />
        ))}
      </ul>
    </>
  )
}

export default ListingFeedPage