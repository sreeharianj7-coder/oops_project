/**
 * ============================================================================
 * STUDENT DASHBOARD CONTROLLER
 * ============================================================================
 */

document.addEventListener('DOMContentLoaded', async () => {
  Auth.requireAuth();
  const student = Auth.getStudent();
  if (!student) return;

  // Render Student Identity Info
  document.getElementById('dash-welcome-name').innerText = student.name;
  document.getElementById('dash-student-name').innerText = student.name;
  document.getElementById('dash-student-id').innerText = student.studentId;
  document.getElementById('dash-course').innerText = student.course || CONFIG.COLLEGE.program;
  document.getElementById('dash-department').innerText = student.department || CONFIG.COLLEGE.department;
  document.getElementById('dash-hostel').innerText = (student.hostel && student.hostel.hostelName) || CONFIG.DEFAULT_HOSTEL.name;
  document.getElementById('dash-room').innerText = student.roomNumber || 'A-204';

  // Render Hostel Configuration Info
  const hostel = student.hostel || CONFIG.DEFAULT_HOSTEL;
  document.getElementById('dash-cfg-hostel-name').innerText = hostel.hostelName || hostel.name || 'ASIET Main Hostel';
  document.getElementById('dash-cfg-lat').innerText = hostel.latitude ? hostel.latitude.toFixed(6) + '° N' : '10.169830° N';
  document.getElementById('dash-cfg-lon').innerText = hostel.longitude ? hostel.longitude.toFixed(6) + '° E' : '76.435740° E';
  document.getElementById('dash-cfg-radius').innerText = (hostel.allowedRadius || 100) + ' meters';

  // Load Live Attendance Stats
  await loadDashboardStats(student.studentId);
});

async function loadDashboardStats(studentId) {
  let stats = null;

  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}/attendance/stats?studentId=${studentId}`);
    const result = await response.json();
    if (result.success && result.data) {
      stats = result.data;
    }
  } catch (err) {
    console.warn('Backend stats API unavailable, using local calculation fallback', err);
    // Fallback calculation from local storage
    const localRecords = JSON.parse(localStorage.getItem(CONFIG.STORAGE_KEYS.OFFLINE_ATTENDANCE) || '[]');
    const studentRecords = localRecords.filter(r => r.studentId === studentId);
    const todayStr = new Date().toISOString().split('T')[0];
    const todayRecord = studentRecords.find(r => r.attendanceDate === todayStr);

    const total = studentRecords.length > 0 ? studentRecords.length : 12;
    const present = studentRecords.length > 0 ? studentRecords.filter(r => r.attendanceStatus === 'PRESENT').length : 11;
    const pct = Math.round((present / total) * 1000) / 10;

    stats = {
      totalDays: total,
      presentDays: present,
      attendancePercentage: pct,
      todayMarked: !!todayRecord,
      todayAttendance: todayRecord,
      todayStatusBadge: todayRecord ? 'ATTENDANCE MARKED' : 'NOT MARKED'
    };
  }

  if (!stats) return;

  // Render Statistics
  document.getElementById('stat-total-days').innerText = stats.totalDays;
  document.getElementById('stat-present-days').innerText = stats.presentDays;
  document.getElementById('stat-percentage').innerText = stats.attendancePercentage + '%';
  document.getElementById('stat-progress-bar').style.width = Math.min(stats.attendancePercentage, 100) + '%';

  // Render Today's Status Badge
  const badgeEl = document.getElementById('dash-today-status-badge');
  const actionBtn = document.getElementById('dash-attendance-action-btn');

  if (stats.todayMarked) {
    badgeEl.className = 'badge badge-present';
    badgeEl.innerText = 'ATTENDANCE MARKED';
    if (actionBtn) {
      actionBtn.innerText = 'View Today\'s Attendance';
      actionBtn.className = 'btn btn-secondary';
      actionBtn.href = 'history.html';
    }
  } else {
    badgeEl.className = 'badge badge-not-marked';
    badgeEl.innerText = 'NOT MARKED';
    if (actionBtn) {
      actionBtn.innerText = 'Mark Attendance Now';
      actionBtn.className = 'btn btn-primary';
      actionBtn.href = 'attendance.html';
    }
  }
}
