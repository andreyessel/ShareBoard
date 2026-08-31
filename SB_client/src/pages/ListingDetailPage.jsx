import { useEffect, useState } from "react";
import { useParams } from "react-router-dom"

function ListingDetailPage() {
  const {listingId} = useParams();
  const [listing, setListing] = useState(null);

  useEffect(() => {
    const fetchListings = async () => {
      const response = await fetch(`http://localhost:8080/api/listings/${listingId}`)
      if (response.ok) {
        setListing(await response.json())
      } else {
        setListing(null)
      }
    }

    fetchListings() }, [listingId])
  
    if (!listing) {
      return <p>Loading...</p>
    }


  return (
    <div>
      <h1>{listing.type}</h1>
      <p>Quantity: {listing.quantity}</p>
      <p>Status: {listing.status}</p>
    </div>
  )
}
export default ListingDetailPage
