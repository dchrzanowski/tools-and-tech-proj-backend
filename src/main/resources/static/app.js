const API = 'http://localhost:8080';

const AREA_TYPES = {
  STAGE:         'Stage / Performance',
  FOOD_BEVERAGE: 'Food & Beverage',
  ENTRY_EXIT:    'Entry / Exit',
  PARKING:       'Parking',
  MEDICAL:       'Medical / First Aid',
  VIP:           'VIP / Hospitality',
  VENDOR:        'Vendor / Market',
  FACILITIES:    'Facilities',
  SECURITY:      'Security',
  OTHER:         'Other',
};

let areasData  = [];
let alertsData = [];
let activityFeed = [];
let countdownVal = 10;
let countdownTimer = null;

/* ════════════════════════════════
   FETCH ALL
════════════════════════════════ */
async function fetchAll() {
  resetCountdown();
  await Promise.all([fetchAreas(), fetchAlerts(), fetchReports()]);
  renderAll();
  renderFeed();
}

async function fetchAreas() {
  try {
    const res = await fetch(`${API}/areas`, { signal: AbortSignal.timeout(4000) });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    areasData = data.map(({ area, latestReport }) => ({
      id:          area.id,
      name:        area.name,
      type:        area.type,
      location:    area.location,
      description: area.description,
      crowdLevel:  latestReport?.level  || 'LOW',
      note:        latestReport?.note   || '',
      lastUpdated: latestReport?.submittedAt || null,
    }));
  } catch {
    // Backend not available — keep existing data
  }
}

async function fetchAlerts() {
  try {
    const res = await fetch(`${API}/alerts`, { signal: AbortSignal.timeout(4000) });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    alertsData = await res.json();
  } catch {
    // Backend not available — keep existing data
  }
}

async function fetchReports() {
  try {
    const res = await fetch(`${API}/reports`, { signal: AbortSignal.timeout(4000) });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const data = await res.json();
    activityFeed = data;
  } catch {
    // Backend not available — keep existing data
  }
}

/* ════════════════════════════════
   RENDER
════════════════════════════════ */
function renderAll() {
  renderBanner();
  renderAlerts();
  renderGrid();
  renderDropdown();
}

function renderBanner() {
  const total  = areasData.length;
  const alerts = alertsData.filter(a => a.status === 'ACTIVE').length;

  document.getElementById('total-areas-val').textContent   = total;
  document.getElementById('active-alerts-val').textContent = alerts;

  const dot  = document.getElementById('status-dot');
  const text = document.getElementById('system-status-text');
  if (alerts > 0) {
    dot.classList.add('critical');
    text.textContent = 'CRITICAL';
    text.style.color = 'var(--critical)';
  } else {
    dot.classList.remove('critical');
    text.textContent = 'HEALTHY';
    text.style.color = 'var(--healthy)';
  }
}

function renderAlerts() {
  const list  = document.getElementById('alerts-list');
  const badge = document.getElementById('alerts-badge');
  const active = alertsData.filter(a => a.status === 'ACTIVE');

  badge.textContent = active.length;

  if (active.length === 0) {
    list.innerHTML = `
      <div class="no-alerts">
        <span class="check">✓</span>
        <p>All clear!<br>No areas at full capacity.</p>
      </div>`;
    return;
  }

  list.innerHTML = '';
  active.forEach(alert => {
    const card = document.createElement('div');
    card.className = 'alert-card';
    card.innerHTML = `
      <div class="area-name">${alert.area.name}</div>
      <span class="full-chip">FULL</span>
      <div class="alert-note">"${alert.message}"</div>
      <div class="alert-time">Last update: ${formatTime(alert.createdAt)}</div>
    `;
    list.appendChild(card);
  });
}

function renderGrid() {
  const grid = document.getElementById('area-grid');
  grid.innerHTML = '';
  areasData.forEach(area => {
    const card = document.createElement('div');
    card.className = `area-card ${area.crowdLevel}`;
    card.dataset.id = area.id;
    const typeLabel = AREA_TYPES[area.type] || area.type || '';
    card.innerHTML = `
      <div class="area-card-name">${area.name}</div>
      <div class="area-card-type">${typeLabel}</div>
      <span class="level-badge">${area.crowdLevel}</span>
    `;
    card.addEventListener('mouseenter', (e) => showTooltip(e, area));
    card.addEventListener('mousemove',  (e) => moveTooltip(e));
    card.addEventListener('mouseleave', hideTooltip);
    card.addEventListener('click',      () => openAreaReportsModal(area));
    grid.appendChild(card);
  });
}

function renderDropdown() {
  const sel = document.getElementById('area-select');
  const cur = sel.value;
  sel.innerHTML = '<option value="">— Select area —</option>';
  areasData.forEach(a => {
    const opt = document.createElement('option');
    opt.value = a.id;
    opt.textContent = a.name;
    sel.appendChild(opt);
  });
  if (cur) sel.value = cur;
}

function renderFeed() {
  const list = document.getElementById('feed-list');
  if (activityFeed.length === 0) {
    list.innerHTML = '<div class="feed-empty">No reports yet. Be the first to submit!</div>';
    return;
  }
  list.innerHTML = '';
  activityFeed.slice(0, 5).forEach(item => {
    const row = document.createElement('div');
    row.className = 'feed-item';
    row.innerHTML = `
      <span class="feed-area">${item.area.name}</span>
      <span class="feed-note">${item.note || '—'}</span>
      <span class="feed-level ${item.level}">${item.level}</span>
      <span class="feed-time">${formatTime(item.submittedAt)}</span>
    `;
    list.appendChild(row);
  });
}

/* ════════════════════════════════
   TOOLTIP
════════════════════════════════ */
const tooltip = document.getElementById('area-tooltip');
let tooltipPinned = false;

function showTooltip(e, area, pin = false) {
  document.getElementById('tt-name').textContent     = area.name;
  document.getElementById('tt-type').textContent     = AREA_TYPES[area.type] || area.type || '';
  document.getElementById('tt-location').textContent = area.location || '';
  document.getElementById('tt-desc').textContent     = area.description || '';
  document.getElementById('tt-note').textContent     = area.note ? `"${area.note}"` : '';
  document.getElementById('tt-time').textContent     = area.lastUpdated ? `Last update: ${formatTime(area.lastUpdated)}` : '';
  tooltip.className = 'area-tooltip visible';
  if (pin) tooltipPinned = true;
  moveTooltip(e);
}
function moveTooltip(e) {
  if (tooltipPinned) return;
  const x = Math.min(e.clientX + 16, window.innerWidth - 240);
  const y = Math.min(e.clientY + 12, window.innerHeight - 120);
  tooltip.style.left = x + 'px';
  tooltip.style.top  = y + 'px';
}
function hideTooltip() {
  if (!tooltipPinned) tooltip.className = 'area-tooltip';
}
document.addEventListener('click', (e) => {
  if (!e.target.closest('.area-card') && !e.target.closest('#area-tooltip')) {
    tooltipPinned = false;
    tooltip.className = 'area-tooltip';
  }
});

/* ════════════════════════════════
   SUBMIT REPORT
════════════════════════════════ */
function checkFormValid() {
  const areaId = document.getElementById('area-select').value;
  const level  = document.querySelector('input[name="crowd-level"]:checked')?.value;
  const note   = document.getElementById('note-input').value.trim();
  document.getElementById('submit-btn').disabled = !(areaId && level && note);
}

async function submitReport(e) {
  e.preventDefault();
  const areaId     = parseInt(document.getElementById('area-select').value, 10);
  const level      = document.querySelector('input[name="crowd-level"]:checked')?.value;
  const note       = document.getElementById('note-input').value.trim();

  const errEl = document.getElementById('form-error');
  const okEl  = document.getElementById('form-success');
  errEl.classList.remove('visible');
  okEl.classList.remove('visible');

  const btn = document.getElementById('submit-btn');
  btn.disabled = true;
  btn.textContent = 'Submitting…';

  try {
    const res = await fetch(`${API}/reports`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ areaId, level, note }),
      signal: AbortSignal.timeout(5000),
    });

    if (!res.ok) {
      const body = await res.json().catch(() => ({}));
      throw new Error(body.message || `Server error (${res.status})`);
    }

    await Promise.all([fetchAreas(), fetchAlerts(), fetchReports()]);
    renderAll();
    renderFeed();
    closeReportModal();

    document.getElementById('report-form').reset();
    btn.textContent = 'Submit Report';
    btn.disabled = true;

  } catch (err) {
    errEl.textContent = `⚠ ${err.message}`;
    errEl.classList.add('visible');
    btn.textContent = 'Submit Report';
    checkFormValid();
  }
}

/* ════════════════════════════════
   AUTO-REFRESH COUNTDOWN
════════════════════════════════ */
function resetCountdown() {
  countdownVal = 10;
  document.getElementById('countdown').textContent = '10s';
  clearInterval(countdownTimer);
  countdownTimer = setInterval(() => {
    countdownVal--;
    document.getElementById('countdown').textContent = countdownVal + 's';
    if (countdownVal <= 0) fetchAll();
  }, 1000);
}

/* ════════════════════════════════
   UTILS
════════════════════════════════ */
function formatTime(iso) {
  if (!iso) return '—';
  const d = new Date(iso);
  return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' });
}

/* ════════════════════════════════
   AREA REPORTS MODAL
════════════════════════════════ */
function openAreaReportsModal(area) {
  const typeLabel = AREA_TYPES[area.type] || area.type || '';
  document.getElementById('area-reports-title').textContent = area.name;
  document.getElementById('area-reports-meta').textContent =
    [typeLabel, area.location].filter(Boolean).join(' · ');

  const reports = activityFeed.filter(r => r.area.id === area.id);
  const list = document.getElementById('area-reports-list');

  if (reports.length === 0) {
    list.innerHTML = '<div class="feed-empty">No reports submitted for this area yet.</div>';
  } else {
    list.innerHTML = '';
    reports.forEach(item => {
      const row = document.createElement('div');
      row.className = 'feed-item';
      row.innerHTML = `
        <span class="feed-area">${item.area.name}</span>
        <span class="feed-note">${item.note || '—'}</span>
        <span class="feed-level ${item.level}">${item.level}</span>
        <span class="feed-time">${formatTime(item.submittedAt)}</span>
      `;
      list.appendChild(row);
    });
  }

  document.getElementById('area-reports-backdrop').classList.add('open');
  document.getElementById('area-reports-modal').classList.add('open');
}

function closeAreaReportsModal() {
  document.getElementById('area-reports-backdrop').classList.remove('open');
  document.getElementById('area-reports-modal').classList.remove('open');
}

/* ════════════════════════════════
   SUBMIT REPORT MODAL
════════════════════════════════ */
function openReportModal() {
  document.getElementById('report-form').reset();
  document.getElementById('submit-btn').disabled = true;
  document.getElementById('form-error').classList.remove('visible');
  document.getElementById('form-success').classList.remove('visible');
  document.getElementById('report-modal-backdrop').classList.add('open');
  document.getElementById('submit-report-modal').classList.add('open');
  document.getElementById('area-select').focus();
}

function closeReportModal() {
  document.getElementById('report-modal-backdrop').classList.remove('open');
  document.getElementById('submit-report-modal').classList.remove('open');
}

/* ════════════════════════════════
   ADD AREA MODAL
════════════════════════════════ */
function openAddAreaModal() {
  document.getElementById('add-area-form').reset();
  document.getElementById('add-area-submit-btn').disabled = true;
  document.getElementById('modal-backdrop').classList.add('open');
  document.getElementById('add-area-modal').classList.add('open');
  document.getElementById('new-area-name').focus();
}

function closeAddAreaModal() {
  document.getElementById('modal-backdrop').classList.remove('open');
  document.getElementById('add-area-modal').classList.remove('open');
}

function checkAreaFormValid() {
  const name     = document.getElementById('new-area-name').value.trim();
  const type     = document.getElementById('new-area-type').value;
  const location = document.getElementById('new-area-location').value.trim();
  document.getElementById('add-area-submit-btn').disabled = !(name && type && location);
}

async function submitNewArea(e) {
  e.preventDefault();
  const name        = document.getElementById('new-area-name').value.trim();
  const type        = document.getElementById('new-area-type').value;
  const location    = document.getElementById('new-area-location').value.trim();
  const description = document.getElementById('new-area-desc').value.trim();

  try {
    const res = await fetch(`${API}/areas`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name, type, location, description }),
      signal: AbortSignal.timeout(5000),
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
  } catch {
    // Backend not available — fall back to local add
    areasData.push({
      id:          Date.now(),
      name,
      type,
      location,
      description,
      crowdLevel:  'LOW',
      note:        '',
      lastUpdated: null,
    });
  }

  await fetchAreas();
  renderAll();
  closeAddAreaModal();
}

document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') {
    closeAreaReportsModal();
    closeReportModal();
    closeAddAreaModal();
  }
});

/* ════════════════════════════════
   BOOT
════════════════════════════════ */
fetchAll();
