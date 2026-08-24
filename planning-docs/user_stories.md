Capstone: ShareBoard (Resource-Matching Platform)
- MVP - logged out:
    - Users can sign up for an account and log in
    - Users can view open needs and open surplus listings, with the posting user/org's name
    - Users can click a listing to view its detail (category, quantity, urgency, needed-by/available-until date, status)
 - MVP - logged in:
    - Users can post a need (category, quantity, urgency, needed-by date)
    - Users can post surplus (category, quantity, available-until date)
    - Users can edit or withdraw a listing they posted, as long as it hasn't been matched yet
    - The system auto-matches listings the moment they cross eligibility, ordered by urgency then wait time
    - Users can view their own listings and a log of their past matches
    - Live updates: the feed and any open listing's detail page update instantly when a match happens, with no page refresh (via WebSocket)
 - Post-MVP:
    - Filter/search listings by category, urgency, or deadline
    - Pagination for the feed
    - A "verified organization" badge
    - Notification when one of your listings gets matched
    