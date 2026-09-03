import { useState, useEffect } from "react";
import ListingCard from "../components/feed/ListingCard";


function ListingFeedPage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchListings = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/listings");
        if (response.ok) {
          setListings(await response.json());
        } else {
          setListings([]);
        }
      } finally {
        setLoading(false);
      }
    };
    fetchListings();
  }, []);

  const openNeeds = listings.filter(
    (l) => l.type === "NEED" && l.status === "OPEN").length;

  const openSurplus = listings.filter(
    (l) => l.type === "SURPLUS" && l.status === "OPEN").length;

  const matched = listings.filter((l) => l.status === "MATCHED").length;

  const partiallyMatch = listings.filter((l) => l.status === "PARTIALLY_MATCHED").length;

  const withdrawn = listings.filter((l) => l.status === "WITHDRAWN").length;
  

  return (
    <div className="py-4">
      <h1 className="h3 mb-3">Listings</h1>

      <div className="d-flex gap-2 mb-4 flex-wrap">
        <span className="badge text-bg-light border fs-6 fw-normal py-2 px-3">
          Open needs: <strong>{openNeeds}</strong>
        </span>
        <span className="badge text-bg-light border fs-6 fw-normal py-2 px-3">
          Open surplus: <strong>{openSurplus}</strong>
        </span>
        <span className="badge text-bg-light border fs-6 fw-normal py-2 px-3">
          Matched: <strong>{matched}</strong>
        </span>
           <span className="badge text-bg-light border fs-6 fw-normal py-2 px-3">
          Partially Matched: <strong>{partiallyMatch}</strong>
        </span>
         <span className="badge text-bg-light border fs-6 fw-normal py-2 px-3">
          Withdrawn: <strong>{withdrawn}</strong>
        </span>
      </div>

      {loading ? (
        <p className="text-muted">Loading listings…</p>
      ) : listings.length === 0 ? (
        <p className="text-muted">No listings yet. Be the first to post one.</p>
      ) : (
        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          {listings.map((listing) => (
            <div className="col" key={listing.listingId}>
              <ListingCard listing={listing} />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ListingFeedPage;