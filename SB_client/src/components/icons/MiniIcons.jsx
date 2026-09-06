export function ClockIcon({ size = 13 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" style={{ flex: "none" }}>
      <circle cx="8" cy="8" r="6.5" stroke="currentColor" strokeWidth="1.3" />
      <path d="M8 4.5V8l2.5 1.5" stroke="currentColor" strokeWidth="1.3" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  );
}

export function PackageIcon({ size = 13 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 16 16" fill="none" style={{ flex: "none" }}>
      <path d="M2 5.2 8 2l6 3.2v5.6L8 14 2 10.8V5.2Z" stroke="currentColor" strokeWidth="1.3" strokeLinejoin="round" />
      <path d="M2 5.2 8 8.4l6-3.2M8 8.4V14" stroke="currentColor" strokeWidth="1.3" strokeLinejoin="round" />
    </svg>
  );
}