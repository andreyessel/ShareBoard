import { useState, useEffect, useRef, useCallback } from "react";
import { Client } from "@stomp/stompjs";
import ListingCard from "../components/feed/ListingCard";
// import "./LandingPage.css";

function ListingFeedPage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState(null); 
  const stompClientRef = useRef(null);
  const subscribedCategories = useRef(new Set());
  const [justMatched, setJustMatched] = useState(new Set());

  // Function to subscribe to STOMP topics per category
  const subscribeToCategory = useCallback((client, categoryId) => {
  if (!client || !client.connected) return;
  if (subscribedCategories.current.has(categoryId)) return;

  subscribedCategories.current.add(categoryId);

  client.subscribe(`/topic/listings/${categoryId}`, (message) => {
    const updated = JSON.parse(message.body);

    setListings((prev) => {
      const map = new Map(prev.map((l) => [l.listingId, l]));
      updated.forEach((l) => map.set(l.listingId, l));
      return Array.from(map.values());
    });

    // flag these listings as "just matched" for the visual flash
    const updatedIds = updated.map((l) => l.listingId);
    setJustMatched((prev) => new Set([...prev, ...updatedIds]));
    setTimeout(() => {
      setJustMatched((prev) => {
        const next = new Set(prev);
        updatedIds.forEach((id) => next.delete(id));
        return next;
      });
    }, 1500);
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
  const partiallyMatched = listings.filter((l) => l.status === "PARTIALLY_MATCHED").length;
  const withdrawn = listings.filter((l) => l.status === "WITHDRAWN").length;

  const filters = [
    { key: "need", label: "Open Needs", count: openNeeds, cls: "sb-pill-need", test: (l) => l.type === "NEED" && l.status === "OPEN" },
    { key: "surplus", label: "Open Surplus", count: openSurplus, cls: "sb-pill-surplus", test: (l) => l.type === "SURPLUS" && l.status === "OPEN" },
    { key: "matched", label: "Matched", count: matched, cls: "sb-pill-matched", test: (l) => l.status === "MATCHED" },
    { key: "partial", label: "Partially Matched", count: partiallyMatched, cls: "sb-pill-partial", test: (l) => l.status === "PARTIALLY_MATCHED" },
    { key: "withdrawn", label: "Withdrawn", count: withdrawn, cls: "sb-pill-withdrawn", test: (l) => l.status === "WITHDRAWN" },
  ];

  function toggleFilter(key) {
    setActiveFilter((prev) => (prev === key ? null : key));
  }

   const activeDef = filters.find((f) => f.key === activeFilter);
  const visibleListings = activeDef
    ? listings.filter(activeDef.test)
    : listings.filter((l) => l.status !== "WITHDRAWN");

 return (
    <div className="sb-feed-page">
      <div className="sb-feed-inner">
        <h1 className="sb-feed-title">Listings</h1>
        <p className="sb-feed-subtitle">
          Live needs and surplus, matched the instant they cross.
        </p>
 
        <div className="sb-filter-bar">
          <button
            className={`sb-pill sb-pill-all ${activeFilter === null ? "active" : ""}`}
            onClick={() => setActiveFilter(null)}
          >
            All
          </button>
          {filters.map((f) => (
            <button
              key={f.key}
              className={`sb-pill ${f.cls} ${activeFilter === f.key ? "active" : ""}`}
              onClick={() => toggleFilter(f.key)}
              aria-pressed={activeFilter === f.key}
            >
              {f.label} <span className="sb-pill-count">{f.count}</span>
            </button>
          ))}
        </div>
 
        {loading ? (
          <p className="text-muted">Loading listings...</p>
        ) : visibleListings.length === 0 ? (
          <p className="text-muted">
            {activeDef ? `No listings match "${activeDef.label}" right now.` : "No listings yet. Be the first to post one."}
          </p>
        ) : (
          <div className="row row-cols-1 row-cols-md-2 row-cols-lg-3 g-4 sb-feed-grid">
            {visibleListings.map((listing) => (
              <div className="col sb-card-in" key={listing.listingId}>
                <ListingCard listing={listing} justMatched={justMatched.has(listing.listingId)} />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}
 
export default ListingFeedPage;