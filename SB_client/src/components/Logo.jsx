function Logo({ size = 32 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" role="img" aria-label="ShareBoard logo">
      <defs>
        <clipPath id="sb-logo-right"><rect x="12" y="0" width="12" height="24" /></clipPath>
      </defs>
      <path
        d="M12 21s-8-4.5-8-11a4.5 4.5 0 0 1 8-3 4.5 4.5 0 0 1 8 3c0 6.5-8 11-8 11z"
        fill="#0E7C61"
      />
      <path
        d="M12 21s-8-4.5-8-11a4.5 4.5 0 0 1 8-3 4.5 4.5 0 0 1 8 3c0 6.5-8 11-8 11z"
        fill="#F2A93B"
        clipPath="url(#sb-logo-right)"
      />
    </svg>
  );
}

export default Logo;