import { useState, useEffect } from "react";
import ListingCard from "../components/feed/ListingCard";
import { useAuth } from "../context/useAuth";

function MyListingsPage() {
  const { user } = useAuth();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMyListings = async () => {
      setLoading(true);
      const response = await fetch(
        `http://localhost:8080/api/listings/myListings?userId=${user.userId}`
      );
      if (response.ok) {
        setListings(await response.json());
      } else {
        setListings([]);
      }
      setLoading(false);
    };
    fetchMyListings();
  }, [user.userId]);

  return (
    <div className="py-4">
      <h1 className="h3 mb-4">My Listings</h1>

      {loading ? (
        <p className="text-muted">Loading…</p>
      ) : listings.length === 0 ? (
        <p className="text-muted">
          You haven't posted anything yet. Head to the feed to post a need or surplus.
        </p>
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

export default MyListingsPage;