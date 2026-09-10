import { useState, useEffect, useRef, useCallback } from "react";
import { Client } from "@stomp/stompjs";
import ListingCard from "../components/feed/ListingCard";

function timeAgo(dateString) {
  const diffMs = Date.now() - new Date(dateString).getTime();
  const mins = Math.round(diffMs / 60000);
  if (mins < 1) return "just now";
  if (mins < 60) return `${mins}m ago`;
  const hours = Math.round(mins / 60);
  if (hours < 24) return `${hours}h ago`;
  return `${Math.round(hours / 24)}d ago`;
}

function ListingFeedPage() {
  const [listings, setListings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeFilter, setActiveFilter] = useState(null);
  const [categoryFilter, setCategoryFilter] = useState("");
  const [urgencyFilter, setUrgencyFilter] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [categories, setCategories] = useState([]);
  const [live, setLive] = useState(false);
  const stompClientRef = useRef(null);
  const subscribedCategories = useRef(new Set());
  const [justMatched, setJustMatched] = useState(new Set());
  const [bumpedKeys, setBumpedKeys] = useState(new Set());
  const prevCounts = useRef({});
  const [activity, setActivity] = useState([]);
  const activityRef = useRef(null);
  const [toast, setToast] = useState(null);
  const toastTimeout = useRef(null);

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

    const need = updated.find((l) => l.type === "NEED");
    const surplus = updated.find((l) => l.type === "SURPLUS");
    if (need && surplus) {
      const line = `${need.categoryName} matched — ${surplus.ownerUsername} \u2192 ${need.ownerUsername}`;
      setActivity((prev) => [...prev, { id: `${need.listingId}-${surplus.listingId}-${Date.now()}`, text: line }]);

      clearTimeout(toastTimeout.current);
      setToast({
        id: Date.now(),
        category: need.categoryName,
        from: surplus.ownerUsername,
        to: need.ownerUsername,
      });
      toastTimeout.current = setTimeout(() => setToast(null), 4500);
    }
  });
}, []);

  // Initial load from REST API
  useEffect(() => {
    const fetchCategories = async () => {
      const response = await fetch("http://localhost:8080/api/categories");
      if (response.ok) {
        setCategories(await response.json());
      }
    };
    fetchCategories();
  }, []);

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

  useEffect(() => {
    const fetchRecentMatches = async () => {
      const response = await fetch("http://localhost:8080/api/matches/recent?limit=8");
      if (!response.ok) return;
      const recent = await response.json();

      const historical = [...recent].reverse().map((m) => ({
        id: `history-${m.matchLineId}`,
        text: `${m.categoryName} matched — ${m.surplusUsername} \u2192 ${m.needUsername} (${timeAgo(m.matchedAt)})`,
      }));
      setActivity((prev) => [...historical, ...prev]);
    };
    fetchRecentMatches();
  }, []);

  // Initialize WebSockets / STOMP client
  useEffect(() => {
    const client = new Client({
      brokerURL: "ws://localhost:8080/ws",
      reconnectDelay: 5000,
    });

    client.onConnect = () => {
      stompClientRef.current = client;
      setLive(true);
      // Immediately subscribe to categories of all currently loaded listings
      listings.forEach((l) => subscribeToCategory(client, l.categoryId));
    };

    client.onWebSocketClose = () => setLive(false);
    client.onDisconnect = () => setLive(false);

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  useEffect(() => {
    const client = stompClientRef.current;
    if (!client || !client.connected) return;
    listings.forEach((l) => subscribeToCategory(client, l.categoryId));
  }, [listings, subscribeToCategory]);

  useEffect(() => {
    if (activityRef.current) activityRef.current.scrollTop = activityRef.current.scrollHeight;
  }, [activity]);

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

  useEffect(() => {
    const changed = new Set();
    filters.forEach((f) => {
      const prev = prevCounts.current[f.key];
      if (prev !== undefined && prev !== f.count) {
        changed.add(f.key);
      }
      prevCounts.current[f.key] = f.count;
    });
    if (changed.size > 0) {
      setBumpedKeys(changed);
      const timeout = setTimeout(() => setBumpedKeys(new Set()), 900);
      return () => clearTimeout(timeout);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [openNeeds, openSurplus, matched, partiallyMatched, withdrawn]);

  function toggleFilter(key) {
    setActiveFilter((prev) => (prev === key ? null : key));
  }

  const activeDef = filters.find((f) => f.key === activeFilter);
  const byStatus = activeDef ? listings.filter(activeDef.test) : listings.filter((l) => l.status !== "WITHDRAWN");
  const byCategory = categoryFilter ? byStatus.filter((l) => String(l.categoryId) === categoryFilter) : byStatus;
  const byUrgency = urgencyFilter ? byCategory.filter((l) => l.urgency === urgencyFilter) : byCategory;
  const term = searchTerm.trim().toLowerCase();
  const visibleListings = term
    ? byUrgency.filter(
        (l) =>
          l.categoryName.toLowerCase().includes(term) ||
          l.ownerUsername.toLowerCase().includes(term)
      )
    : byUrgency;

  const anyFilterActive = activeDef || categoryFilter || urgencyFilter || term;

  return (
    <div className="sb-feed-page">
      <div className="sb-page-bar sb-page-bar--feed" />
      <div className="sb-feed-inner">
        <div className="sb-feed-header">
          <div>
            <h1 className="sb-feed-title">The Board</h1>
            <p className="sb-feed-subtitle">Every open need — and what's on its way to filling it.</p>
          </div>
          <span className={`sb-live-pill ${live ? "sb-live-pill--on" : ""}`}>
            <span className={`sb-live-dot ${live ? "sb-live-dot--on" : ""}`} aria-hidden="true" />
            {live ? "Matching engine live" : "Connecting…"}
          </span>
        </div>

        <div className="sb-feed-layout">
          <div>
            <div className="sb-filter-row">
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
                    className={`sb-pill ${f.cls} ${activeFilter === f.key ? "active" : ""} ${bumpedKeys.has(f.key) ? "sb-pill-bump" : ""}`}
                    onClick={() => toggleFilter(f.key)}
                    aria-pressed={activeFilter === f.key}
                  >
                    {f.label} <span className="sb-pill-count">{f.count}</span>
                  </button>
                ))}
              </div>
            </div>

            <div className="sb-search-row">
              <input
                type="search"
                className="form-control sb-search-input"
                placeholder="Search by category or org name…"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                aria-label="Search listings"
              />
              <select
                className="form-select sb-category-select"
                value={categoryFilter}
                onChange={(e) => setCategoryFilter(e.target.value)}
                aria-label="Filter by category"
              >
                <option value="">All categories</option>
                {categories.map((c) => (
                  <option key={c.categoryId} value={String(c.categoryId)}>
                    {c.name}
                  </option>
                ))}
              </select>
              <select
                className="form-select sb-category-select"
                value={urgencyFilter}
                onChange={(e) => setUrgencyFilter(e.target.value)}
                aria-label="Filter by urgency"
              >
                <option value="">All urgencies</option>
                <option value="HIGH">Urgent</option>
                <option value="MEDIUM">Moderate</option>
                <option value="LOW">Low urgency</option>
              </select>
            </div>

            {loading ? (
              <p className="text-muted">Loading listings...</p>
            ) : visibleListings.length === 0 ? (
              <p className="text-muted">
                {anyFilterActive ? "No listings match these filters right now." : "No listings yet. Be the first to post one."}
              </p>
            ) : (
              <div className="sb-feed-grid">
                {visibleListings.map((listing) => (
                  <div className="sb-card-in" key={listing.listingId}>
                    <ListingCard listing={listing} justMatched={justMatched.has(listing.listingId)} />
                  </div>
                ))}
              </div>
            )}
          </div>

          <aside className="sb-feed-sidebar">
            <div className="sb-glance-card">
              <p className="sb-glance-title">At a glance</p>
              <div className="sb-glance-row">
                <span>Open needs</span>
                <strong>{openNeeds}</strong>
              </div>
              <div className="sb-glance-row">
                <span>Open surplus</span>
                <strong>{openSurplus}</strong>
              </div>
              <div className="sb-glance-row">
                <span>Matched</span>
                <strong>{matched}</strong>
              </div>
              <div className="sb-glance-row">
                <span>Partially matched</span>
                <strong>{partiallyMatched}</strong>
              </div>
            </div>

            <div className="sb-activity-card">
              <p className="sb-glance-title">Engine activity</p>
              <div className="sb-activity-list" ref={activityRef}>
                {activity.length === 0 ? (
                  <div className="sb-activity-item">
                    <span className="sb-activity-dot" />
                    Watching for needs and surplus that cross.
                  </div>
                ) : (
                  activity.map((a) => (
                    <div key={a.id} className="sb-activity-item">
                      <span className="sb-activity-dot" />
                      {a.text}
                    </div>
                  ))
                )}
              </div>
            </div>
          </aside>
        </div>
      </div>

      {toast && (
        <div className="sb-match-toast" key={toast.id}>
          <span className="sb-match-toast-icon">✓</span>
          <div>
            <p className="sb-match-toast-title">Match found!</p>
            <p className="sb-match-toast-body">
              {toast.category}  {toast.from}  {toast.to}
            </p>
          </div>
        </div>
      )}
    </div>
  );
}

export default ListingFeedPage;