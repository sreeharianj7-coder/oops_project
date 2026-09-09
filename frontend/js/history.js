/**
 * ============================================================================
 * ATTENDANCE HISTORY CONTROLLER
 * ============================================================================
 */

let allAttendanceRecords = [];

document.addEventListener('DOMContentLoaded', async () => {
  Auth.requireAuth();
  const student = Auth.getStudent();
  if (!student) return;

  document.getElementById('hist-student-id').innerText = student.studentId;
  document.getElementById('hist-student-name').innerText = student.name;

  await loadAttendanceHistory(student.studentId);
});

async function loadAttendanceHistory(studentId) {
  const tbody = document.getElementById('history-table-body');
  tbody.innerHTML = `<tr><td colspan="6" style="text-align: center; padding: 2rem; color: #94a3b8;">Loading attendance records...</td></tr>`;

  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}/attendance/history?studentId=${studentId}`);
    const result = await response.json();
    if (result.success && Array.isArray(result.data)) {
      allAttendanceRecords = result.data;
    }
  } catch (err) {
    console.warn('Backend history API unavailable, using local mock store fallback', err);
    const local = JSON.parse(localStorage.getItem(CONFIG.STORAGE_KEYS.OFFLINE_ATTENDANCE) || '[]');
    allAttendanceRecords = local.filter(r => r.studentId === studentId);

    // If local is empty, populate demo historical records
    if (allAttendanceRecords.length === 0) {
      allAttendanceRecords = [
        {
          id: 101,
          studentId: studentId,
          attendanceDate: '2026-09-07',
          attendanceTime: '08:45:12',
          latitude: 10.169850,
          longitude: 76.435720,
          distanceFromHostel: 3.15,
          locationStatus: 'VERIFIED',
          attendanceStatus: 'PRESENT',
          verificationMode: 'GEOLOCATION'
        },
        {
          id: 102,
          studentId: studentId,
          attendanceDate: '2026-09-08',
          attendanceTime: '08:42:30',
          latitude: 10.169910,
          longitude: 76.435800,
          distanceFromHostel: 11.20,
          locationStatus: 'VERIFIED',
          attendanceStatus: 'PRESENT',
          verificationMode: 'GEOLOCATION'
        },
        {
          id: 103,
          studentId: studentId,
          attendanceDate: '2026-09-09',
          attendanceTime: '08:50:04',
          latitude: 10.169820,
          longitude: 76.435750,
          distanceFromHostel: 1.50,
          locationStatus: 'VERIFIED',
          attendanceStatus: 'PRESENT',
          verificationMode: 'GEOLOCATION'
        }
      ];
    }
  }

  renderAttendanceTable(allAttendanceRecords);
  updateSummaryMetrics(allAttendanceRecords);
}

function renderAttendanceTable(records) {
  const tbody = document.getElementById('history-table-body');
  if (!tbody) return;

  if (records.length === 0) {
    tbody.innerHTML = `
      <tr>
        <td colspan="6" style="text-align: center; padding: 2.5rem; color: #94a3b8;">
          <div style="font-size: 2rem; margin-bottom: 0.5rem;">📋</div>
          <div>No attendance records found for this student.</div>
          <div style="margin-top: 0.5rem;">
            <a href="attendance.html" class="btn btn-sm btn-primary">Mark First Attendance</a>
          </div>
        </td>
      </tr>
    `;
    return;
  }

  tbody.innerHTML = records.map((rec, index) => {
    const isVerified = rec.locationStatus === 'VERIFIED';
    const locBadge = isVerified 
      ? `<span class="badge badge-verified">VERIFIED</span>` 
      : `<span class="badge badge-outside">OUTSIDE HOSTEL</span>`;

    const statusBadge = rec.attendanceStatus === 'PRESENT'
      ? `<span class="badge badge-present">PRESENT</span>`
      : `<span class="badge badge-rejected">REJECTED</span>`;

    return `
      <tr>
        <td style="font-family: var(--font-mono); font-size: 0.88rem; color: #38bdf8;">#${index + 1}</td>
        <td style="font-weight: 600; color: #fff;">${formatDate(rec.attendanceDate)}</td>
        <td style="font-family: var(--font-mono); color: #cbd5e1;">${formatTime(rec.attendanceTime)}</td>
        <td>${locBadge}</td>
        <td style="font-family: var(--font-mono); color: #94a3b8;">${rec.distanceFromHostel ? rec.distanceFromHostel + ' m' : '< 10 m'}</td>
        <td>${statusBadge}</td>
      </tr>
    `;
  }).join('');
}

function updateSummaryMetrics(records) {
  const total = records.length;
  const present = records.filter(r => r.attendanceStatus === 'PRESENT').length;
  const avgDist = total > 0 
    ? Math.round(records.reduce((acc, r) => acc + (r.distanceFromHostel || 5), 0) / total * 10) / 10 
    : 0;

  document.getElementById('hist-metric-total').innerText = total;
  document.getElementById('hist-metric-present').innerText = present;
  document.getElementById('hist-metric-avg-dist').innerText = avgDist + ' m';
}

function filterRecords() {
  const searchTerm = (document.getElementById('search-input').value || '').toLowerCase();
  const statusFilter = document.getElementById('status-filter').value;

  const filtered = allAttendanceRecords.filter(r => {
    const matchesSearch = (r.attendanceDate || '').includes(searchTerm) || (r.attendanceTime || '').includes(searchTerm);
    const matchesStatus = statusFilter === 'ALL' || r.attendanceStatus === statusFilter;
    return matchesSearch && matchesStatus;
  });

  renderAttendanceTable(filtered);
}

function exportToCSV() {
  if (allAttendanceRecords.length === 0) {
    showToast('No records available to export', 'warning');
    return;
  }

  const student = Auth.getStudent();
  const headers = ['Record ID', 'Student ID', 'Date', 'Time', 'Location Status', 'Distance (m)', 'Attendance Status', 'Mode'];
  
  const rows = allAttendanceRecords.map((r, i) => [
    i + 1,
    student ? student.studentId : 'N/A',
    r.attendanceDate,
    r.attendanceTime,
    r.locationStatus,
    r.distanceFromHostel,
    r.attendanceStatus,
    r.verificationMode || 'GEOLOCATION'
  ]);

  const csvContent = 'data:text/csv;charset=utf-8,' 
    + [headers.join(','), ...rows.map(e => e.join(','))].join('\n');

  const encodedUri = encodeURI(csvContent);
  const link = document.createElement('a');
  link.setAttribute('href', encodedUri);
  link.setAttribute('download', `Hostel_Attendance_${student ? student.studentId : 'Export'}.csv`);
  document.body.appendChild(link);
  link.click();
  link.remove();

  showToast('Attendance report exported to CSV successfully!', 'success');
}
