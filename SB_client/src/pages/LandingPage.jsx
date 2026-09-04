import { useEffect } from "react";
import { Link } from "react-router-dom";
import "./LandingPage.css";

function LandingPage({ hideAuthLinks = false }) {
  useEffect(() => {
    const sections = document.querySelectorAll("section[id]");
    const railButtons = document.querySelectorAll(".lp-rail button");
    const nav = document.getElementById("lpNav");
    const rail = document.getElementById("lpRail");
    const darkSections = new Set(["lp-need", "lp-bridge", "lp-cta"]);

    const io = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add("lp-in-view");
            const id = entry.target.id;
            railButtons.forEach((b) =>
              b.classList.toggle("active", b.dataset.target === id)
            );
            const dark = darkSections.has(id);
            nav?.classList.toggle("lp-on-dark", dark);
            rail?.classList.toggle("lp-on-dark", dark);
          }
        });
      },
      { threshold: 0.45 }
    );

    sections.forEach((s) => io.observe(s));

    railButtons.forEach((b) => {
      b.addEventListener("click", () => {
        document.getElementById(b.dataset.target)?.scrollIntoView({ behavior: "smooth" });
      });
    });

    return () => io.disconnect();
  }, []);

  return (
    <div className="lp-root">

      <nav id="lpNav" className="lp-nav">
        <div className="lp-brand">
          <svg width="26" height="26" viewBox="0 0 26 26">
            <circle cx="10" cy="13" r="8" fill="#0E7C61" opacity="0.9" />
            <circle cx="17" cy="13" r="8" fill="#F2A93B" opacity="0.85" />
          </svg>
          ShareBoard
        </div>
        {!hideAuthLinks && (
          <div className="lp-nav-actions">
            <Link className="lp-btn lp-btn-ghost" to="/login">Log in</Link>
            <Link className="lp-btn lp-btn-solid" to="/register">Sign up</Link>
          </div>
        )}
      </nav>

      <div className="lp-rail" id="lpRail">
        <button data-target="lp-need" className="active" aria-label="The need"></button>
        <button data-target="lp-gap" aria-label="The gap"></button>
        <button data-target="lp-bridge" aria-label="The bridge"></button>
        <button data-target="lp-payoff" aria-label="The payoff"></button>
        <button data-target="lp-cta" aria-label="Join ShareBoard"></button>
      </div>

      <section id="lp-need" className="lp-on-dark-copy">
        <div className="lp-copy">
          <p className="lp-kicker">Friday, 28°F, the Bronx</p>
          <h2 className="lp-headline">Right now, a shelter is counting blankets it doesn't have.</h2>
          <p className="lp-body-text">Riverside Shelter is short fifty blankets, and it's supposed to hit 28°F on Friday. They've called every donor they know. Nobody's answering fast enough.</p>
        </div>
        <div className="lp-art">
          <svg viewBox="0 0 480 380" role="img" aria-label="A shelter building at night, one window lit, under a cold moon">
            <circle cx="400" cy="70" r="34" fill="#2A3A5E" />
            <circle cx="388" cy="60" r="34" fill="#101B33" />
            <circle cx="120" cy="90" r="2" fill="#556087" />
            <circle cx="200" cy="50" r="1.6" fill="#556087" />
            <circle cx="60" cy="160" r="1.6" fill="#556087" />
            <circle cx="340" cy="200" r="2" fill="#556087" />
            <rect x="70" y="150" width="220" height="190" rx="6" fill="#1B2A4A" />
            <rect x="70" y="150" width="220" height="14" fill="#26375C" />
            <rect x="95" y="185" width="40" height="40" rx="3" fill="#0D1730" />
            <rect x="150" y="185" width="40" height="40" rx="3" fill="#F2A93B" />
            <rect x="205" y="185" width="40" height="40" rx="3" fill="#0D1730" />
            <rect x="95" y="240" width="40" height="40" rx="3" fill="#0D1730" />
            <rect x="150" y="240" width="40" height="40" rx="3" fill="#0D1730" />
            <rect x="205" y="240" width="40" height="40" rx="3" fill="#0D1730" />
            <rect x="150" y="295" width="40" height="45" fill="#0D1730" />
            <rect x="330" y="250" width="70" height="60" rx="8" fill="#1B2A4A" stroke="#2A3A5E" strokeWidth="1" />
            <line x1="365" y1="262" x2="365" y2="280" stroke="#F2A93B" strokeWidth="2" strokeLinecap="round" />
            <line x1="365" y1="280" x2="378" y2="280" stroke="#F2A93B" strokeWidth="2" strokeLinecap="round" />
            <circle cx="365" cy="280" r="24" fill="none" stroke="#2A3A5E" strokeWidth="2" />
            <text x="365" y="325" textAnchor="middle" fontFamily="Inter" fontWeight="600" fontSize="12" fill="#F2A93B">Fri - 28F</text>
          </svg>
        </div>
      </section>

      <section id="lp-gap">
        <div className="lp-copy">
          <p className="lp-kicker" style={{ color: "#8A6A1E" }}>Six blocks away</p>
          <h2 className="lp-headline" style={{ color: "#3E2F0C" }}>Thirty blankets are folded in a stockroom, unsold since October.</h2>
          <p className="lp-body-text" style={{ color: "#7A6535" }}>Corner Market ordered too many for the season. They're sitting there right now ” not lost, not needed by anyone at the store. Just nowhere near the people who actually need them.</p>
        </div>
        <div className="lp-art">
          <svg viewBox="0 0 480 380" role="img" aria-label="A quiet storefront with unsold boxes stacked inside">
            <rect x="60" y="120" width="360" height="220" rx="8" fill="#F5E3C0" />
            <rect x="60" y="120" width="360" height="36" rx="8" fill="#E8C077" />
            <rect x="80" y="90" width="320" height="34" rx="4" fill="#8A5A0E" />
            <text x="240" y="113" textAnchor="middle" fontFamily="Fraunces" fontSize="15" fill="#F5E3C0">Corner Market</text>
            <rect x="90" y="180" width="90" height="120" rx="4" fill="#FBEFD9" stroke="#D8B370" strokeWidth="1.5" />
            <line x1="90" y1="210" x2="180" y2="210" stroke="#D8B370" strokeWidth="1.5" />
            <rect x="120" y="200" width="50" height="60" rx="3" fill="#0E7C61" opacity="0.85" />
            <rect x="120" y="200" width="50" height="60" rx="3" fill="none" stroke="#0A5C49" strokeWidth="1.5" />
            <rect x="130" y="195" width="30" height="10" fill="#0A5C49" opacity="0.6" />
            <rect x="120" y="270" width="50" height="60" rx="3" fill="#0E7C61" opacity="0.7" />
            <rect x="120" y="270" width="50" height="60" rx="3" fill="none" stroke="#0A5C49" strokeWidth="1.5" />
            <rect x="250" y="200" width="130" height="30" rx="4" fill="#fff" stroke="#D8B370" strokeWidth="1.5" />
            <line x1="255" y1="205" x2="375" y2="225" stroke="#C97070" strokeWidth="1.5" />
            <text x="315" y="220" textAnchor="middle" fontFamily="Inter" fontSize="11" fill="#8A6535">30 unsold</text>
          </svg>
        </div>
      </section>

      <section id="lp-bridge">
        <div className="lp-copy">
          <p className="lp-kicker">ShareBoard</p>
          <h2 className="lp-headline">ShareBoard closes that six blocks.</h2>
          <p className="lp-body-text">Post what you need. Post what you have extra. The moment a need and a surplus can actually reach each other in time, ShareBoard finds the match ” automatically, in seconds.</p>
        </div>
        <div className="lp-art">
          <svg viewBox="0 0 480 380" role="img" aria-label="A map showing a shelter and a market connected by a matching line">
            <rect x="30" y="30" width="420" height="320" rx="16" fill="#0A5C49" />
            <line x1="30" y1="110" x2="450" y2="110" stroke="#12805F" strokeWidth="1" />
            <line x1="30" y1="190" x2="450" y2="190" stroke="#12805F" strokeWidth="1" />
            <line x1="30" y1="270" x2="450" y2="270" stroke="#12805F" strokeWidth="1" />
            <line x1="150" y1="30" x2="150" y2="350" stroke="#12805F" strokeWidth="1" />
            <line x1="330" y1="30" x2="330" y2="350" stroke="#12805F" strokeWidth="1" />
            <path d="M110 260 Q 240 150 370 130" fill="none" stroke="#F2A93B" strokeWidth="2.5" strokeDasharray="1 10" strokeLinecap="round" />
            <path d="M108 258 c 0 -14 12 -25 12 -25 c 0 14 -1 25 -12 25z" fill="#fff" />
            <circle cx="110" cy="248" r="6" fill="#0A5C49" />
            <text x="110" y="290" textAnchor="middle" fontFamily="Inter" fontWeight="600" fontSize="11" fill="#fff">Riverside</text>
            <path d="M368 128 c 0 -14 12 -25 12 -25 c 0 14 -1 25 -12 25z" fill="#F2A93B" />
            <circle cx="370" cy="118" r="6" fill="#8A5A0E" />
            <text x="370" y="158" textAnchor="middle" fontFamily="Inter" fontWeight="600" fontSize="11" fill="#fff">Corner Market</text>
            <circle cx="240" cy="195" r="30" fill="#fff" />
            <path d="M226 196 l10 10 l20 -22" fill="none" stroke="#0A5C49" strokeWidth="4" strokeLinecap="round" strokeLinejoin="round" />
            <text x="240" y="245" textAnchor="middle" fontFamily="Fraunces" fontSize="13" fill="#fff">matched</text>
          </svg>
        </div>
      </section>

      <section id="lp-payoff">
        <div className="lp-copy">
          <p className="lp-kicker" style={{ color: "#8C2D3D" }}>Friday night</p>
          <h2 className="lp-headline" style={{ color: "#4A1520" }}>Riverside gets its blankets. Corner Market gets its stockroom back.</h2>
          <p className="lp-body-text" style={{ color: "#7A3B47" }}>Nobody filed a grant proposal. Nobody waited three weeks for a committee meeting. Two people six blocks apart just helped each other, directly.</p>
          <div className="lp-stat-pill">
            <span className="lp-num">1,240</span>
            <span className="lp-lbl">needs matched across New York this year</span>
          </div>
        </div>
        <div className="lp-art">
          <svg viewBox="0 0 480 380" role="img" aria-label="Two hands exchanging a folded blanket, with a small heart above">
            <circle cx="240" cy="190" r="150" fill="#F4C5CE" opacity="0.5" />
            <rect x="170" y="150" width="140" height="90" rx="10" fill="#0E7C61" />
            <rect x="170" y="150" width="140" height="90" rx="10" fill="none" stroke="#0A5C49" strokeWidth="2" />
            <line x1="200" y1="150" x2="200" y2="240" stroke="#0A5C49" strokeWidth="2" />
            <line x1="280" y1="150" x2="280" y2="240" stroke="#0A5C49" strokeWidth="2" />
            <path d="M110 260 q30 -50 90 -34" fill="none" stroke="#3E2530" strokeWidth="16" strokeLinecap="round" />
            <path d="M370 260 q-30 -50 -90 -34" fill="none" stroke="#3E2530" strokeWidth="16" strokeLinecap="round" />
            <path d="M228 95 c-10 -18 -40 -6 -26 16 c8 12 26 24 26 24 c0 0 18 -12 26 -24 c14 -22 -16 -34 -26 -16z" fill="#E85D75" />
          </svg>
        </div>
      </section>

      <section id="lp-cta">
        <h2>Give what's extra. Get what's needed.</h2>
        <p>Join the people and organizations already doing this, block by block, across New York.</p>
        <div className="lp-cta-actions">
          <Link className="lp-btn lp-btn-marigold" to="/register">Sign up</Link>
          <Link className="lp-btn lp-btn-ghost" to="/login" style={{ borderColor: "rgba(255,255,255,0.35)", color: "#fff" }}>Log in</Link>
        </div>
        <div className="lp-preview-card">
          <div className="lp-preview-check">
            <svg width="16" height="16" viewBox="0 0 16 16">
              <path d="M2 8l4 4 8-9" fill="none" stroke="#0A5C49" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
          </div>
          <div>
            <div className="lp-title">Baby formula ” matched just now</div>
            <div className="lp-sub">Hope Nursery  Downtown Pharmacy · 20 units</div>
          </div>
        </div>
      </section>

      <footer className="lp-footer">ShareBoard · New York, NY</footer>
    </div>
  );
}

export default LandingPage;