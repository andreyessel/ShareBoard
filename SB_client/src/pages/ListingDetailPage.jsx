import { useEffect, useState } from "react"
import { useParams } from "react-router-dom"

function ListingDetailPage() {
  const { listingId } = useParams()
  const [listing, setListing] = useState(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const fetchListing = async () => {
      const response = await fetch(`http://localhost:8080/api/listings/${listingId}`)
      if (response.ok) {
        setListing(await response.json())
      } else {
        setListing(null)
      }
      setLoading(false)
    }
    fetchListing()
  }, [listingId])

  if (loading) {
    return <p>Loading...</p>
  }

  if (!listing) {
    return <p>Listing not found.</p>
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