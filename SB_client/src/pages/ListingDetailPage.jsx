import { API_URL, WS_URL } from "../config";
import { useEffect, useState,  useRef  } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import StatusBadge from "../components/feed/StatusBadge";
import UrgencyBadge from "../components/feed/UrgencyBadge";
import { useAuth } from "../context/useAuth"; 
import { Client } from "@stomp/stompjs";

function formatDate(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

function formatDateTime(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleString("en-US", {
    month: "short",
    day: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

function ListingDetailPage() {
   const { user } = useAuth(); 
  const { listingId } = useParams();
  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showConfirm, setShowConfirm] = useState(false);
  const [matchHistory, setMatchHistory] = useState([]);
  const [historyLoading, setHistoryLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchListing = async () => {
      setLoading(true);
const response = await fetch(`${API_URL}/api/listings/${listingId}`);      if (response.ok) {
        setListing(await response.json());
      } else {
        setListing(null);
      }
      setLoading(false);
    };
    fetchListing();
  }, [listingId]);

  useEffect(() => {
    const fetchHistory = async () => {
      setHistoryLoading(true);
      try {
const response = await fetch(`${API_URL}/api/listings/${listingId}/matches`);        setMatchHistory(response.ok ? await response.json() : []);
      } finally {
        setHistoryLoading(false);
      }
    };
    fetchHistory();
  }, [listingId]);

  const stompClientRef = useRef(null);

useEffect(() => {
  if (!listing) return;

  const client = new Client({
brokerURL: WS_URL,    reconnectDelay: 5000,
  });

    client.onConnect = () => {
      client.subscribe(`/topic/listings/${listing.categoryId}`, (message) => {
        const updated = JSON.parse(message.body);
        const match = updated.find((l) => l.listingId === Number(listingId));
        if (match) {
          setListing(match);
fetch(`${API_URL}/api/listings/${listingId}/matches`)
            .then((res) => (res.ok ? res.json() : []))
            .then(setMatchHistory)
            .catch(() => {});
        }
      });
    };

    client.activate();
    stompClientRef.current = client;

    return () => {
      client.deactivate();
    };
  }, [listing?.categoryId, listingId]);
  
async function handleWithdraw() {
    const response = await fetch(
`${API_URL}/api/listings/${listingId}?requesterId=${user.userId}`,      { method: "DELETE" }
    );
  if (response.ok) {
    navigate("/");
  }
}

  if (loading) {
    return <p className="text-muted py-4">Loading…</p>;
  }

  if (!listing) {
    return (
      <div className="py-4">
        <p className="text-muted">Listing not found.</p>
        <Link to="/">Back to feed</Link>
      </div>
    );
  }

  const isNeed = listing.type === "NEED";
  const isDone = listing.status === "MATCHED";
  const deadline = isNeed ? listing.neededBy : listing.availableUntil;
  const deadlineLabel = isNeed ? "Needed by" : "Available until";
  const canEdit = listing.status === "OPEN" && user && user.userId === listing.userId;
  const canOfferSurplus =
    isNeed &&
    (listing.status === "OPEN" || listing.status === "PARTIALLY_MATCHED") &&
    user;

  function handleOfferSurplus() {
    navigate("/new-listing", {
      state: {
        prefill: {
          categoryId: listing.categoryId,
          categoryName: listing.categoryName,
          suggestedQuantity: listing.quantityRemaining,
        },
      },
    });
  }
  return (
    <div className="sb-detail-page-wrap">
      <div className={`sb-page-bar ${isDone ? "sb-page-bar--detail-surplus" : isNeed ? "sb-page-bar--detail-need" : "sb-page-bar--detail-surplus"}`} />
      <div className="sb-detail-page">
      <Link to="/" className="d-inline-block mb-3 text-decoration-none sb-back-link">
        &larr; Back to feed
      </Link>

      <div className="sb-detail-grid">
        <div className={`sb-detail-card ${isDone ? "sb-detail-card--done" : isNeed ? "sb-detail-card--need" : "sb-detail-card--surplus"}`}>
          <div className="sb-detail-header">
            {isDone ? (
              <span className="sb-detail-type sb-detail-type--done">Done</span>
            ) : (
              <span className={`sb-detail-type ${isNeed ? "sb-detail-type--need" : "sb-detail-type--surplus"}`}>
                {isNeed ? "Need" : "Surplus"}
              </span>
            )}
            {isNeed && !isDone && <UrgencyBadge urgency={listing.urgency} />}
            <span className="ms-auto">
              <StatusBadge status={listing.status} />
            </span>
          </div>

          <div className="sb-detail-body">
            <h1 className="sb-detail-qty">
              {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
            </h1>
            <p className="sb-detail-meta">
              {listing.categoryName} · Posted by {listing.ownerUsername}
              {listing.ownerIsOrganization && (
                <span className="sb-verified-badge" title="Verified organization">✓ Org</span>
              )}
            </p>

            <div className="sb-progress-row">
              <div className="sb-remaining-track sb-remaining-track--lg" aria-hidden="true">
                <div
                  className={`sb-remaining-fill ${isDone ? "sb-remaining-fill--done" : isNeed ? "sb-remaining-fill--need" : "sb-remaining-fill--surplus"}`}
                  style={{ width: `${listing.quantity > 0 ? Math.round(((listing.quantity - listing.quantityRemaining) / listing.quantity) * 100) : 0}%` }}
                />
              </div>
              <span className="sb-progress-label">
                {listing.quantity > 0 ? Math.round(((listing.quantity - listing.quantityRemaining) / listing.quantity) * 100) : 0}% matched
              </span>
            </div>
            <p className="sb-detail-remaining">{listing.quantityRemaining} {isNeed ? "still needed" : "still available"}</p>

            <div className="sb-detail-facts">
              <div className="sb-detail-fact">
                <span className="sb-detail-fact-label">{deadlineLabel}</span>
                <span className="sb-detail-fact-value">{formatDate(deadline) ?? "—"}</span>
              </div>
            </div>

            {canOfferSurplus && (
              <button className="btn btn-primary sb-btn-brand mt-4 w-100" onClick={handleOfferSurplus}>
                Offer surplus for this
              </button>
            )}

            {canEdit && (
              <div className="d-flex gap-2 mt-4">
                <button className="btn btn-outline-primary" onClick={() => navigate(`/listings/${listingId}/edit`)}>Edit</button>
                <button className="btn btn-outline-danger" onClick={() => setShowConfirm(true)}>Withdraw</button>
              </div>
            )}
          </div>

          {showConfirm && (
            <>
              <div className="modal d-block" tabIndex="-1" role="dialog">
                <div className="modal-dialog ">
                  <div className="modal-content">
                    <div className="modal-header">
                      <h5 className="modal-title">Withdraw listing?</h5>
                      <button type="button" className="btn-close" onClick={() => setShowConfirm(false)}></button>
                    </div>
                    <div className="modal-body">
                      <p>This will withdraw the listing and it can't be undone. Are you sure?</p>
                    </div>
                    <div className="modal-footer">
                      <button className="btn btn-outline-secondary" onClick={() => setShowConfirm(false)}>Cancel</button>
                      <button className="btn btn-danger" onClick={handleWithdraw}>Withdraw</button>
                    </div>
                  </div>
                </div>
              </div>
              <div className="modal-backdrop show"></div>
            </>
          )}
        </div>

        <div className="sb-match-history-card">
          <h2 className="sb-match-history-title">Match history</h2>
          {historyLoading ? (
            <p className="text-muted small">Loading…</p>
          ) : matchHistory.length === 0 ? (
            <p className="text-muted small sb-match-history-empty">
              No matches yet — this listing hasn't crossed with anything on the other side.
            </p>
          ) : (
            <ul className="sb-match-history-list">
              {matchHistory.map((m) => (
                <li key={m.matchLineId} className="sb-match-history-item">
                  <span className="sb-match-history-qty">{m.quantityMatched}</span>
                  <span className="sb-match-history-text">
                    matched with <strong>{m.counterpartUsername}</strong>
                    {m.counterpartIsOrganization && (
                      <span className="sb-verified-badge" title="Verified organization">✓ Org</span>
                    )}
                    <span className="text-muted"> ({m.counterpartRole === "NEED" ? "need" : "surplus"})</span>
                  </span>
                  <span className="sb-match-history-time text-muted">{formatDateTime(m.matchedAt)}</span>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
      </div>
    </div>
  );
}

export default ListingDetailPage;