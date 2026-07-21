# 06 - UI Design

## Dashboard Structure
All logged-in pages inherit the core `DashboardLayout`.

### 1. Navbar (Top)
- **Brand Logo**: Left-aligned, redirects to Dashboard.
- **Search Bar**: Centered, allows searching URLs by original or short code.
- **User Dropdown**: Right-aligned. Shows avatar. Dropdown contains:
  - Profile Settings
  - Dark Mode Toggle
  - Logout

### 2. Sidebar (Left, collapsible on mobile)
- **Navigation Links**:
  - Dashboard (Home)
  - My URLs
  - Analytics
  - Settings
- **Action Button**: Large prominent "+ Create URL" button at the top of the sidebar.

## Key Pages

### 1. Dashboard (Overview)
- **Statistics Cards**: Row of 4 cards at the top:
  - Total Links Created
  - Total Clicks (All time)
  - Clicks Today
  - Top Performing Link
- **Recent URLs Table**:
  - Columns: Original URL, Short Link, Clicks, Date, Actions (Copy, Edit, Delete, QR Code).
  - Pagination controls at the bottom.
- **Analytics Graph (Mini)**: A Chart.js line chart showing clicks over the last 7 days.

### 2. Create URL Modal / Page
- **Inputs**:
  - `Original URL` (Required, Type: URL, Validation: Must start with http/https).
  - `Custom Alias` (Optional, Type: Text, Validation: Alphanumeric and dashes only).
- **Buttons**: `Cancel` (Secondary), `Shorten` (Primary).

### 3. Analytics Page
- **Filter Bar**: Date range selector (Last 7 Days, Last 30 Days, All Time).
- **Charts**:
  - Line Chart: Clicks over time.
  - Pie/Doughnut Chart: Clicks by Device (Desktop, Mobile, Tablet).
  - Bar Chart: Clicks by OS / Browser.

## Design System & Responsive Behavior
- **Colors**: Primary brand color (e.g., Indigo/Blue), Background (Light gray / Dark gray for dark mode), text (dark slate).
- **Responsiveness**: 
  - Mobile: Sidebar becomes a hamburger menu. Data tables become stacked cards.
  - Tablet/Desktop: Full dashboard layout.
- **Validations**: Real-time inline form validation (red text beneath inputs). Toast notifications (`react-hot-toast`) for successful creations/deletions.
