const TONE = {
  OPEN: { label: "Open", text: "#1B3A66", bg: "linear-gradient(180deg,#EAF1FB,#DDE9F8)", ring: "rgba(120,160,220,0.45)" },
  PARTIALLY_MATCHED: { label: "Partially Matched", text: "#8A5A0E", bg: "linear-gradient(180deg,#FCF1DD,#F8E7C6)", ring: "rgba(234,201,136,0.5)" },
  MATCHED: { label: "Matched", text: "#0A5C49", bg: "linear-gradient(180deg,#E4F2EE,#D6EBE4)", ring: "rgba(14,124,97,0.35)" },
  WITHDRAWN: { label: "Withdrawn", text: "#5B6478", bg: "linear-gradient(180deg,#F1F1F0,#E6E6E4)", ring: "rgba(91,100,120,0.3)" },
};

function StatusBadge({ status }) {
  const t = TONE[status] ?? TONE.OPEN;
  return (
    <span
      className="sb-status-badge"
      style={{ background: t.bg, color: t.text, boxShadow: `inset 0 0 0 1px ${t.ring}` }}
    >
      {t.label}
    </span>
  );
}

export default StatusBadge;