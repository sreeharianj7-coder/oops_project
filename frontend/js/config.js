/**
 * ============================================================================
 * SMART HOSTEL ATTENDANCE - FRONTEND CONFIGURATION & CLIENT-SIDE UTILITIES
 * ============================================================================
 */

const CONFIG = {
  // Spring Boot Backend Base URL
  API_BASE_URL: 'http://localhost:8080/api',
  
  // Default Campus Details (Adi Shankara Institute of Science and Technology)
  COLLEGE: {
    name: 'Adi Shankara Institute of Science and Technology',
    program: 'B.Tech Computer Science and Engineering',
    department: 'Computer Science and Engineering',
    location: 'Mattoor, Kalady, Ernakulam, Kerala - 683574',
    academicYear: '2023 - 2027'
  },

  // Default Campus Hostel (Kalady Campus)
  DEFAULT_HOSTEL: {
    id: 1,
    name: 'ASIET Main College Hostel',
    latitude: 10.16983000,
    longitude: 76.43574000,
    allowedRadius: 100, // in meters
    description: 'Main Campus Hostel - Block A, Adi Shankara Institute'
  },

  // Storage Keys
  STORAGE_KEYS: {
    AUTH_TOKEN: 'smarthostel_token',
    STUDENT_DATA: 'smarthostel_student',
    OFFLINE_ATTENDANCE: 'smarthostel_attendance_records',
    OFFLINE_STUDENTS: 'smarthostel_students_list'
  }
};

/**
 * Toast Notification Utility
 */
function showToast(message, type = 'info', duration = 4000) {
  let container = document.getElementById('toast-container');
  if (!container) {
    container = document.createElement('div');
    container.id = 'toast-container';
    document.body.appendChild(container);
  }

  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  
  let icon = 'ℹ️';
  if (type === 'success') icon = '✅';
  if (type === 'error') icon = '❌';
  if (type === 'warning') icon = '⚠️';

  toast.innerHTML = `
    <div style="font-size: 1.2rem; line-height: 1;">${icon}</div>
    <div style="flex-grow: 1;">${message}</div>
    <button style="background: none; border: none; color: #94a3b8; cursor: pointer; font-size: 1.1rem; line-height: 1;" onclick="this.parentElement.remove()">✕</button>
  `;

  container.appendChild(toast);

  setTimeout(() => {
    toast.style.opacity = '0';
    toast.style.transform = 'translateX(100%)';
    toast.style.transition = 'all 0.3s ease';
    setTimeout(() => toast.remove(), 300);
  }, duration);
}

/**
 * Date/Time Formatting Utilities
 */
function formatDate(dateStr) {
  if (!dateStr) return 'N/A';
  const d = new Date(dateStr);
  return d.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
}

function formatTime(timeStr) {
  if (!timeStr) return 'N/A';
  if (timeStr.includes(':')) {
    const parts = timeStr.split(':');
    let h = parseInt(parts[0], 10);
    const m = parts[1];
    const ampm = h >= 12 ? 'PM' : 'AM';
    h = h % 12 || 12;
    return `${h.toString().padStart(2, '0')}:${m} ${ampm}`;
  }
  return timeStr;
}

/**
 * Client-side Haversine Formula Utility
 * (Mirrors Java DistanceCalculator.java for offline validation / simulation fallback)
 */
function calculateHaversineDistance(lat1, lon1, lat2, lon2) {
  const R = 6371000; // Earth radius in meters
  const dLat = (lat2 - lat1) * Math.PI / 180;
  const dLon = (lon2 - lon1) * Math.PI / 180;
  const a = 
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(lat1 * Math.PI / 180) * Math.cos(lat2 * Math.PI / 180) * 
    Math.sin(dLon / 2) * Math.sin(dLon / 2);
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  const distance = R * c;
  return Math.round(distance * 100) / 100;
}
