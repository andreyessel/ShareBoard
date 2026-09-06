const TONE = {
  HIGH: { label: "Urgent", dot: "#C1483A", text: "#8C2D22", bg: "linear-gradient(180deg,#FDEDEA,#FAE1DC)", ring: "rgba(232,169,156,0.5)" },
  MEDIUM: { label: "Moderate", dot: "#B07A24", text: "#7A5115", bg: "linear-gradient(180deg,#FCF1DD,#F8E7C6)", ring: "rgba(234,201,136,0.5)" },
  LOW: { label: "Low urgency", dot: "#2E7A63", text: "#1F5443", bg: "linear-gradient(180deg,#E9F4EF,#DCEEE6)", ring: "rgba(156,212,190,0.5)" },
};

function UrgencyBadge({ urgency }) {
  if (!urgency) return null;
  const t = TONE[urgency] ?? TONE.LOW;

  return (
    <span
      className="sb-urgency-badge"
      style={{
        background: t.bg,
        color: t.text,
        boxShadow: `inset 0 0 0 1px ${t.ring}`,
      }}
    >
      <span className="sb-urgency-dot" style={{ background: t.dot }} />
      {t.label}
    </span>
  );
}

export default UrgencyBadge;