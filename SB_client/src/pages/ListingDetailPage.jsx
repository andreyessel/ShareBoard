import { useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import StatusBadge from "../components/feed/StatusBadge";
import UrgencyBadge from "../components/feed/UrgencyBadge";

function formatDate(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

function ListingDetailPage() {
  const { listingId } = useParams();
  const [listing, setListing] = useState(null);
  const [loading, setLoading] = useState(true);
  const [showConfirm, setShowConfirm] = useState(false);
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
  
  //for deleting
async function handleWithdraw() {
  const response = await fetch( `http://localhost:8080/api/listings/${listingId}?requesterId=${listing.userId}`,
    { method: "DELETE" }
  );
  if (response.ok) {
    navigate("/");
  }
}

  //   //for updating
  // async function handleEdit() {

  //   const response = await fetch(`http://localhost:8080/api/listings/${listingId}?requesterId=${listing.userId}`,

  //   );

  //   if (response.ok) {
  //     navigate("/");
  //   }
  // }

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
  const canEdit = listing.status === "OPEN";

  return (
    <div className="py-4" style={{ maxWidth: "560px", margin: "0 auto" }}>
      <Link to="/" className="d-inline-block mb-3 text-decoration-none">
        &larr; Back </Link>

      <div className="card shadow-sm">
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
              <button className="roman-btn" onClick={() => navigate(`/listings/${listingId}/edit`)}>Edit</button>
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
    </div>
  );
}

export default ListingDetailPage;