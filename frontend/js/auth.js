/**
 * ============================================================================
 * AUTHENTICATION & SESSION MANAGEMENT
 * ============================================================================
 */

const Auth = {
  /**
   * Checks if a student is currently authenticated.
   */
  isLoggedIn() {
    return !!localStorage.getItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
  },

  /**
   * Retrieves logged-in student details.
   */
  getStudent() {
    const data = localStorage.getItem(CONFIG.STORAGE_KEYS.STUDENT_DATA);
    if (!data) return null;
    try {
      return JSON.parse(data);
    } catch (e) {
      return null;
    }
  },

  /**
   * Saves authentication session.
   */
  setSession(token, student) {
    localStorage.setItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN, token);
    localStorage.setItem(CONFIG.STORAGE_KEYS.STUDENT_DATA, JSON.stringify(student));
  },

  /**
   * Logs the student out and redirects to login page.
   */
  logout() {
    localStorage.removeItem(CONFIG.STORAGE_KEYS.AUTH_TOKEN);
    localStorage.removeItem(CONFIG.STORAGE_KEYS.STUDENT_DATA);
    showToast('Logged out successfully', 'info');
    setTimeout(() => {
      window.location.href = 'login.html';
    }, 500);
  },

  /**
   * Protects authenticated routes.
   */
  requireAuth() {
    if (!this.isLoggedIn()) {
      window.location.href = 'login.html';
    }
  },

  /**
   * Redirects authenticated users away from guest pages (login/register).
   */
  redirectIfLoggedIn() {
    if (this.isLoggedIn()) {
      window.location.href = 'dashboard.html';
    }
  },

  /**
   * Handles student registration.
   */
  async register(formData) {
    try {
      const response = await fetch(`${CONFIG.API_BASE_URL}/auth/register`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      const result = await response.json();

      if (response.ok && result.success) {
        this.setSession(result.data.token, result.data.student);
        return { success: true, message: result.message, student: result.data.student };
      } else {
        return { success: false, message: result.message || 'Registration failed' };
      }
    } catch (err) {
      console.warn('Backend API unavailable, using local client-side persistence fallback', err);
      // Demo / Offline fallback mode
      const newStudent = {
        id: Date.now(),
        studentId: formData.studentId.trim().toUpperCase(),
        name: formData.name.trim(),
        email: formData.email.trim().toLowerCase(),
        phone: formData.phone.trim(),
        course: formData.course || CONFIG.COLLEGE.program,
        department: formData.department || CONFIG.COLLEGE.department,
        academicYear: formData.academicYear || CONFIG.COLLEGE.academicYear,
        hostel: CONFIG.DEFAULT_HOSTEL,
        roomNumber: formData.roomNumber.trim()
      };

      const mockToken = 'mock_jwt_token_' + Date.now();
      this.setSession(mockToken, newStudent);
      return { success: true, message: 'Account created successfully (Local Session)', student: newStudent };
    }
  },

  /**
   * Handles student login.
   */
  async login(identifier, password) {
    try {
      const response = await fetch(`${CONFIG.API_BASE_URL}/auth/login`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ identifier, password })
      });

      const result = await response.json();

      if (response.ok && result.success) {
        this.setSession(result.data.token, result.data.student);
        return { success: true, message: result.message, student: result.data.student };
      } else {
        return { success: false, message: result.message || 'Invalid Student ID/Email or Password.' };
      }
    } catch (err) {
      console.warn('Backend API unavailable, checking demo credentials fallback', err);
      // Demo fallback check
      if (
        (identifier.toUpperCase() === 'ASIET2024CS001' || identifier.toLowerCase() === 'rahul.cs@adishankara.ac.in') &&
        password === 'Password@123'
      ) {
        const demoStudent = {
          id: 1,
          studentId: 'ASIET2024CS001',
          name: 'Rahul Sharma',
          email: 'rahul.cs@adishankara.ac.in',
          phone: '+91 98765 43210',
          course: 'B.Tech Computer Science and Engineering',
          department: 'Computer Science and Engineering',
          academicYear: '2023 - 2027',
          hostel: CONFIG.DEFAULT_HOSTEL,
          roomNumber: 'A-204'
        };
        const mockToken = 'mock_jwt_token_' + Date.now();
        this.setSession(mockToken, demoStudent);
        return { success: true, message: 'Login successful (Demo Mode)', student: demoStudent };
      }

      // Check if user was registered in localStorage
      const savedStudent = this.getStudent();
      if (savedStudent && (savedStudent.studentId === identifier.toUpperCase() || savedStudent.email === identifier.toLowerCase())) {
        const mockToken = 'mock_jwt_token_' + Date.now();
        this.setSession(mockToken, savedStudent);
        return { success: true, message: 'Login successful', student: savedStudent };
      }

      return { success: false, message: 'Invalid Student ID/Email or Password.' };
    }
  },

  /**
   * Initializes user navigation display.
   */
  initNav() {
    const student = this.getStudent();
    const navAuth = document.getElementById('nav-auth-section');
    if (!navAuth) return;

    if (student) {
      navAuth.innerHTML = `
        <div class="user-pill">
          <div class="avatar">${student.name.charAt(0).toUpperCase()}</div>
          <div style="display: flex; flex-direction: column; text-align: left;">
            <span style="font-weight: 600; color: #fff; font-size: 0.82rem;">${student.name}</span>
            <span style="font-size: 0.68rem; color: #94a3b8; font-family: var(--font-mono);">${student.studentId}</span>
          </div>
        </div>
        <button class="btn btn-sm btn-danger" onclick="Auth.logout()" title="Sign Out">Logout</button>
      `;
    } else {
      navAuth.innerHTML = `
        <a href="login.html" class="btn btn-sm btn-secondary">Student Login</a>
        <a href="register.html" class="btn btn-sm btn-primary">Register</a>
      `;
    }
  }
};

document.addEventListener('DOMContentLoaded', () => {
  Auth.initNav();
});
