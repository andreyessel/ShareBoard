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
      const response = await fetch(`http://localhost:8080/api/listings/${listingId}` );
      if (response.ok) {
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
        const response = await fetch(`http://localhost:8080/api/listings/${listingId}/matches`);
        setMatchHistory(response.ok ? await response.json() : []);
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
    brokerURL: "ws://localhost:8080/ws",
    reconnectDelay: 5000,
  });

    client.onConnect = () => {
      client.subscribe(`/topic/listings/${listing.categoryId}`, (message) => {
        const updated = JSON.parse(message.body);
        const match = updated.find((l) => l.listingId === Number(listingId));
        if (match) {
          setListing(match);
          fetch(`http://localhost:8080/api/listings/${listingId}/matches`)
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
  
  //for deleting
async function handleWithdraw() {
    const response = await fetch(
      `http://localhost:8080/api/listings/${listingId}?requesterId=${user.userId}`, // CHANGED
      { method: "DELETE" }
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
  const deadline = isNeed ? listing.neededBy : listing.availableUntil;
  const deadlineLabel = isNeed ? "Needed by" : "Available until";
  const canEdit = listing.status === "OPEN" && user && user.userId === listing.userId;
  return (
    <div className="sb-detail-page">
      <Link to="/" className="d-inline-block mb-3 text-decoration-none sb-back-link">
        &larr; Back </Link>

      <div className={`card shadow-sm sb-detail-card ${isNeed ? "sb-listing-card--need" : "sb-listing-card--surplus"}`}>
        <div className="card-body">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <span
              className={`badge ${ isNeed
                  ? "text-bg-danger-subtle text-danger-emphasis"
                  : "text-bg-success-subtle text-success-emphasis"
              }`}
            >  {isNeed ? "Need" : "Surplus"} </span>
            {isNeed && <UrgencyBadge urgency={listing.urgency} />}
          </div>

          <h1 className="h4 mb-3">
            {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
          </h1>

          <p className="text-muted mb-3">
             {listing.categoryName} · Posted by {listing.ownerUsername}
          </p>

          <dl className="row mb-3">
            <dt className="col-5 col-sm-4 text-muted fw-normal">Quantity</dt>
            <dd className="col-7 col-sm-8">{listing.quantity}</dd>

            <dt className="col-5 col-sm-4 text-muted fw-normal">Remaining</dt>
            <dd className="col-7 col-sm-8">{listing.quantityRemaining}</dd>

            <dt className="col-5 col-sm-4 text-muted fw-normal">
              {deadlineLabel}
            </dt>
            <dd className="col-7 col-sm-8">{formatDate(deadline) ?? "—"}</dd>

            <dt className="col-5 col-sm-4 text-muted fw-normal">Status</dt>
            <dd className="col-7 col-sm-8">
              <StatusBadge status={listing.status} />
            </dd>
          </dl>

          {canEdit && (
            <div className="d-flex gap-2">
          <button className="btn btn-outline-primary" onClick={() => navigate(`/listings/${listingId}/edit`)}>Edit</button>        
        <button className="btn btn-outline-danger" onClick={() => setShowConfirm(true)}>Withdraw</button>
            </div>
          )}

          {showConfirm && (
            <>
              <div className="modal d-block" tabIndex="-1" role="dialog">
                <div className="modal-dialog">
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
                  <span className="text-muted"> ({m.counterpartRole === "NEED" ? "need" : "surplus"})</span>
                </span>
                <span className="sb-match-history-time text-muted">{formatDateTime(m.matchedAt)}</span>
              </li>
            ))}
          </ul>
        )}
      </div>
    </div>
  );
}

export default ListingDetailPage;