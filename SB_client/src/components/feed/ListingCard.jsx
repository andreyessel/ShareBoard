import { Link } from "react-router-dom";
import UrgencyBadge from "./UrgencyBadge";
import StatusBadge from "./StatusBadge";
import { ClockIcon, PackageIcon } from "../icons/MiniIcons";

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
  const deadlineLabel = isNeed ? "by" : "thru";
  const pctRemaining = listing.quantity > 0
    ? Math.round((listing.quantityRemaining / listing.quantity) * 100)
    : 0;

  return (
    <Link
      to={`/listings/${listing.listingId}`}
      className="text-decoration-none text-reset"
    >
      <div
        className={`sb-listing-card ${
          isNeed ? "sb-listing-card--need" : "sb-listing-card--surplus"
        } ${justMatched ? "match-flash" : ""}`}
      >
        <div className="sb-listing-card-body">
          <div className="d-flex justify-content-between align-items-start mb-2">
            <div>
              <h2 className="sb-listing-title">
                {listing.quantity} unit{listing.quantity === 1 ? "" : "s"}
              </h2>
              <p className="sb-listing-owner">
                {listing.ownerUsername}
                {listing.ownerIsOrganization && (
                  <span className="sb-verified-badge" title="Verified organization">✓ Org</span>
                )}
              </p>
            </div>
            {isNeed ? <UrgencyBadge urgency={listing.urgency} /> : <StatusBadge status={listing.status} />}
          </div>

          <p className="sb-listing-category">{listing.categoryName}</p>

          <div className="sb-remaining-track" aria-hidden="true">
            <div
              className={`sb-remaining-fill ${isNeed ? "sb-remaining-fill--need" : "sb-remaining-fill--surplus"}`}
              style={{ width: `${pctRemaining}%` }}
            />
          </div>

          <div className="sb-listing-meta">
            <span className="sb-listing-meta-item">
              <PackageIcon /> {listing.quantityRemaining} remaining
            </span>
            <span className="sb-listing-meta-item">
              <ClockIcon /> {deadlineLabel} {formatDate(deadline) ?? "—"}
            </span>
          </div>

          {isNeed && (
            <div className="sb-listing-footer">
              <StatusBadge status={listing.status} />
            </div>
          )}
        </div>
      </div>
    </Link>
  );
}