import { useState, useEffect, useRef, useCallback } from "react";
import { Client } from "@stomp/stompjs";
import ListingCard from "../components/feed/ListingCard";

function ListingFeedPage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const stompClientRef = useRef(null);
  const subscribedCategories = useRef(new Set());

  // Function to subscribe to STOMP topics per category
  const subscribeToCategory = useCallback((client, categoryId) => {
    if (!client || !client.connected) return;
    if (subscribedCategories.current.has(categoryId)) return;

    subscribedCategories.current.add(categoryId);

    client.subscribe(`/topic/listings/${categoryId}`, (message) => {
      const updated = JSON.parse(message.body); // array of Listing objects
      setListings((prev) => {
        const map = new Map(prev.map((l) => [l.listingId, l]));
        updated.forEach((l) => map.set(l.listingId, l)); // upsert listing
        return Array.from(map.values());
      });
    });
  }, []);

  // Initial load from REST API
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

  // Initialize WebSockets / STOMP client
  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8080/ws",
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      stompClientRef.current = client;
      // Immediately subscribe to categories of all currently loaded listings
      listings.forEach((l) => subscribeToCategory(client, l.categoryId));
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  // Dynamic subscription listener: whenever listings update (or when a new listing is fetched),
  // ensure we subscribe to any new category topics instantly
  useEffect(() => {
    const client = stompClientRef.current;
    if (!client || !client.connected) return;
    listings.forEach((l) => subscribeToCategory(client, l.categoryId));
  }, [listings, subscribeToCategory]);

  const openNeeds = listings.filter((l) => l.type === "NEED" && l.status === "OPEN").length;
  const openSurplus = listings.filter((l) => l.type === "SURPLUS" && l.status === "OPEN").length;
  const matched = listings.filter((l) => l.status === "MATCHED").length;
  const partiallyMatch = listings.filter((l) => l.status === "PARTIALLY_MATCHED").length;
  const withdrawn = listings.filter((l) => l.status === "WITHDRAWN").length;

  const visibleListings = listings.filter((l) => l.status !== "WITHDRAWN");

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
      ) : visibleListings.length === 0 ? (
        <p className="text-muted">No listings yet. Be the first to post one.</p>
      ) : (
        <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4">
          {visibleListings.map((listing) => (
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