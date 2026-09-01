

function ListingDetailPage({listing}) {



  return (
    <div>
      <h1>{listing.type}</h1>
      <p>Quantity: {listing.quantity}</p>
      <p>Status: {listing.status}</p>
    </div>
  )
}
export default ListingDetailPage
