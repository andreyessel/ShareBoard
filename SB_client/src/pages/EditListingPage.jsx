import { useState, useEffect } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function EditListingPage() {
  const { user } = useAuth();
  const { listingId } = useParams();
  const navigate = useNavigate();
  const [listing, setListing] = useState(null);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState([]);
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const [listingRes, categoriesRes] = await Promise.all([
        fetch(`http://localhost:8080/api/listings/${listingId}`),
        fetch("http://localhost:8080/api/categories"),
      ]);
      if (listingRes.ok) {
        setListing(await listingRes.json());
      } else {
        setListing(null);
      }
      if (categoriesRes.ok) setCategories(await categoriesRes.json());
      setLoading(false);
    };
    fetchData();
  }, [listingId]);

  function handleChange(event) {
    setListing({ ...listing, [event.target.name]: event.target.value });
  }

  const isNeed = listing?.type === "NEED";

  async function handleSubmit(event) {
    event.preventDefault();
    setSubmitting(true);
    setErrors([]);

    const payload = {
      categoryId: Number(listing.categoryId),
      type: listing.type,
      quantity: Number(listing.quantity),
      urgency: isNeed ? listing.urgency : null,
      neededBy: isNeed ? listing.neededBy : null,
      availableUntil: isNeed ? null : listing.availableUntil,
      status: listing.status,
    };

    const response = await fetch(
      `http://localhost:8080/api/listings/${listingId}?requesterId=${user.userId}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      }
    );

    if (response.ok) {
      navigate(`/listings/${listingId}`);
    } else if (response.status === 400) {
      setErrors(await response.json());
      setSubmitting(false);
    } else {
      setErrors(["Something went wrong. Please try again."]);
      setSubmitting(false);
    }
  }

  if (loading) return <p className="text-muted py-4">Loading…</p>;
  if (!listing) {
    return (
      <div className="py-4">
        <p className="text-muted">Listing not found.</p>
        <Link to="/">Back to feed</Link>
      </div>
    );
  }

  return (
    <div className="py-4" style={{ maxWidth: "480px", margin: "0 auto" }}>
      <Link to="/" className="d-inline-block mb-3 text-decoration-none">
        &larr; Back
      </Link>
      <h1 className="h3 mb-4">Edit Listing</h1>
      <p className="text-muted small">Type: {listing.type} (cannot be changed)</p>
      <form onSubmit={handleSubmit}>
        <div className="mb-3">
          <label className="form-label" htmlFor="categoryId">Category</label>
          <select
            className="form-select"
            id="categoryId"
            name="categoryId"
            value={listing.categoryId}
            onChange={handleChange}
            required
          >
            {categories.map((c) => (
              <option key={c.categoryId} value={c.categoryId}>{c.name}</option>
            ))}
          </select>
        </div>
        <div className="mb-3">
          <label className="form-label" htmlFor="quantity">Quantity</label>
          <input
            type="number"
            className="form-control"
            id="quantity"
            name="quantity"
            min="1"
            value={listing.quantity}
            onChange={handleChange}
            required
          />
        </div>
        {isNeed && (
          <div className="mb-3">
            <label className="form-label d-block">Urgency</label>
            <select className="form-select" name="urgency" value={listing.urgency} onChange={handleChange}>
              <option value="LOW">Low</option>
              <option value="MEDIUM">Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
        )}
        <div className="mb-4">
          <label className="form-label" htmlFor="deadline">
            {isNeed ? "Needed by" : "Available until"}
          </label>
          <input
            type="date"
            className="form-control"
            id="deadline"
            name={isNeed ? "neededBy" : "availableUntil"}
            value={isNeed ? (listing.neededBy ?? "") : (listing.availableUntil ?? "")}
            onChange={handleChange}
            required
          />
        </div>
        {errors.length > 0 && (
          <div className="alert alert-danger">
            <ul className="mb-0">{errors.map((err) => <li key={err}>{err}</li>)}</ul>
          </div>
        )}
        <button type="submit" className="btn btn-primary w-100" disabled={submitting}>
          {submitting ? "Saving…" : "Save changes"}
        </button>
      </form>
    </div>
  );
}

export default EditListingPage;