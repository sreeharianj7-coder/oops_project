/**
 * ============================================================================
 * STUDENT & COLLEGE PROFILE CONTROLLER
 * ============================================================================
 */

document.addEventListener('DOMContentLoaded', async () => {
  Auth.requireAuth();
  const student = Auth.getStudent();
  if (!student) return;

  await loadStudentProfile(student.studentId);
});

async function loadStudentProfile(studentId) {
  let profileData = null;

  try {
    const response = await fetch(`${CONFIG.API_BASE_URL}/student/profile?studentId=${studentId}`);
    const result = await response.json();
    if (result.success && result.data) {
      profileData = result.data;
    }
  } catch (err) {
    console.warn('Backend profile API unavailable, using cached student data', err);
    profileData = {
      student: Auth.getStudent(),
      college: CONFIG.COLLEGE
    };
  }

  if (!profileData || !profileData.student) return;

  const s = profileData.student;
  const c = profileData.college || CONFIG.COLLEGE;

  // Student Details
  document.getElementById('prof-name').innerText = s.name;
  document.getElementById('prof-avatar-char').innerText = s.name ? s.name.charAt(0).toUpperCase() : 'S';
  document.getElementById('prof-student-id').innerText = s.studentId;
  document.getElementById('prof-email').innerText = s.email;
  document.getElementById('prof-phone').innerText = s.phone || '+91 98765 43210';
  document.getElementById('prof-course').innerText = s.course || c.program;
  document.getElementById('prof-department').innerText = s.department || c.department;
  document.getElementById('prof-academic-year').innerText = s.academicYear || c.academicYear;
  document.getElementById('prof-hostel').innerText = (s.hostel && s.hostel.hostelName) || CONFIG.DEFAULT_HOSTEL.name;
  document.getElementById('prof-room').innerText = s.roomNumber || 'A-204';

  // College Details
  document.getElementById('col-name').innerText = c.collegeName || c.name || CONFIG.COLLEGE.name;
  document.getElementById('col-program').innerText = c.program || CONFIG.COLLEGE.program;
  document.getElementById('col-department').innerText = c.department || CONFIG.COLLEGE.department;
  document.getElementById('col-location').innerText = c.location || CONFIG.COLLEGE.location;
}
