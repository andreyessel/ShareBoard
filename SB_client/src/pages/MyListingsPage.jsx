import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import ListingCard from "../components/feed/ListingCard";
import { useAuth } from "../context/useAuth";

function MyListingsPage() {
  const { user } = useAuth();
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchMyListings = async () => {
      setLoading(true);
      const response = await fetch( `http://localhost:8080/api/listings/myListings?userId=${user.userId}`
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
    <div className="sb-mylistings-page-wrap">
      <div className="sb-page-bar sb-page-bar--mylistings" />
      <div className="sb-mylistings-page">
      <h1 className="sb-mylistings-title">My Listings</h1>
      <p className="sb-mylistings-subtitle">Everything you've posted, need or surplus, in one place.</p>

      {loading ? (
        <p className="text-muted">Loading…</p>
      ) : listings.length === 0 ? (
        <div className="sb-empty-state">
          <p className="text-muted mb-3">
            You haven't posted anything yet. Head to the feed to post a need or surplus.
          </p>
          <Link to="/new-listing" className="btn btn-primary sb-btn-brand">+ Post a listing</Link>
        </div>
      ) : (
        <div className="sb-listings-grid">
          {listings.map((listing) => (
            <ListingCard listing={listing} key={listing.listingId} />
          ))}
        </div>
      )}
      </div>
    </div>
  );
}

export default MyListingsPage;