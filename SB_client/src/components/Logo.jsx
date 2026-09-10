function Logo({ size = 32 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 200 160" role="img" aria-label="ShareBoard logo">
      <path
        d="M10 50 C10 20 40 10 60 25 C75 36 78 55 70 70
           C85 75 95 90 92 110 C88 135 60 150 30 150
           C15 150 10 130 10 110 Z"
        fill="#0E7C61"
      />
      <path
        d="M190 50 C190 20 160 10 140 25 C125 36 122 55 130 70
           C115 75 105 90 108 110 C112 135 140 150 170 150
           C185 150 190 130 190 110 Z"
        fill="#F2A93B"
      />
    </svg>
  );
}
 
export default Logo;