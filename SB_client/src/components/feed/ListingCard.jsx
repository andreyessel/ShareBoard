import { Link } from "react-router-dom";
import UrgencyBadge from "./UrgencyBadge";
import StatusBadge from "./StatusBadge";

function formatDate(dateString) {
  if (!dateString) return null;
  return new Date(dateString).toLocaleDateString("en-US", {
    month: "short",
    day: "numeric",
  });
}

export default function ListingCard({ listing, justMatched}) {
  const isNeed = listing.type === "NEED";
  const deadline = isNeed ? listing.neededBy : listing.availableUntil;
  const deadlineLabel = isNeed ? "Needed by" : "Available thru";
  const pctRemaining = listing.quantity > 0
    ? Math.round((listing.quantityRemaining / listing.quantity) * 100)
    : 0;

  return (
    <Link
      to={`/listings/${listing.listingId}`}
      className="text-decoration-none text-reset"
    >
      <div
        className={`card h-100 shadow-sm sb-listing-card ${
          isNeed ? "sb-listing-card--need" : "sb-listing-card--surplus"
        } ${justMatched ? "match-flash" : ""}`}
      >
        <div className="card-body d-flex flex-column">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <span
              className={`badge ${ isNeed
                  ? "text-bg-danger-subtle text-danger-emphasis"
                  : "text-bg-success-subtle text-success-emphasis"
              }`} > {isNeed ? "Need" : "Surplus"}
            </span> {isNeed && <UrgencyBadge urgency={listing.urgency} />}
          </div>

             <p className="text-muted small mb-1">
                {listing.categoryName} · {listing.ownerUsername}
             </p>

          <h2 className="h5 mb-1">
            {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
          </h2>

          <div className="sb-remaining-track" aria-hidden="true">
            <div
              className={`sb-remaining-fill ${isNeed ? "sb-remaining-fill--need" : "sb-remaining-fill--surplus"}`}
              style={{ width: `${pctRemaining}%` }}
            />
          </div>
          <p className="text-muted small mb-3">{listing.quantityRemaining} remaining</p>

          <div className="mt-auto d-flex justify-content-between align-items-center">
            <span className="small text-muted">
              {deadlineLabel} {formatDate(deadline) ?? "—"}
            </span>
            <StatusBadge status={listing.status} />
          </div>
        </div>
      </div>
    </Link>
  );
}