// Маппинги значений фильтров -> значения для API и отображения
const STATUS_MAP = {
    'in-processing': 'В обработке',
    'in-progress': 'В работе',
    'closed': 'Закрыта',
    'canceled': 'Отмена'
};

const TYPE_MAP = {
    'repair': 'Ремонт',
    'production': 'Изготовление'
};

// Перевод полей для модалки (учтены синонимы из БД)
const FIELD_LABELS = {
    id: 'ID',
    ext_number: 'Внешний номер',
    number_journal: 'Номер журнала',
    type: 'Тип',
    priority: 'Приоритет',
    name_type: 'Наименование типа',
    location_repair: 'Место ремонта',
    lokation_repair: 'Место ремонта',
    count: 'Количество',
    drawing_number: 'Номер чертежа',
    page_count: 'Кол-во страниц',
    full_name: 'ФИО',
    division: 'Подразделение',
    tg_id: 'Telegram ID',
    code_users: 'Код пользователя',
    email: 'Email',
    phone: 'Телефон',
    status: 'Статус',
    comment: 'Комментарий',
    application_date: 'Дата заявки',
    comment_ready: 'Комментарий о готовности',
    date_ready: 'Дата готовности',
    comments_closing: 'Комментарий о закрытии',
    closing_date: 'Дата закрытия',
    photo_1: 'Фото 1',
    photo_2: 'Фото 2',
    photo_3: 'Фото 3',
    comment_shift: 'Комментарий смены',
    plan_complete_date: 'План. дата выполнения',
    plan_complet_date: 'План. дата выполнения',
    category_change: 'Категория изменения',
    amos_order_number: 'Номер заказа AMOS',
    department_ogm: 'Отдел ОГМ'
};

// DOM элементы
const controlsForm = document.getElementById('controls');
const requestsContainer = document.getElementById('requests');
const typeBadgeEl = document.getElementById('type-badge');
const statusBadgeEl = document.getElementById('status-badge');
const toggleFiltersBtn = document.getElementById('toggle-filters');

// Модал
const modal = document.getElementById('modal');
const modalBackdrop = document.getElementById('modal-backdrop');
const modalClose = document.getElementById('modal-close');
const modalTitleExt = document.getElementById('modal-title-ext');
const modalBody = document.getElementById('modal-body');

function getFilters() {
    // Приоритеты (чекбоксы)
    const priorityValues = Array.from(controlsForm.querySelectorAll('input[name="priority"]:checked')).map(i => i.value);

    // Статус (одно значение -> русское)
    const statusKey = controlsForm.querySelector('input[name="status_type"]:checked')?.value;
    const statusValue = statusKey ? STATUS_MAP[statusKey] : null;

    // Тип работ (одно значение -> русское)
    const typeKey = controlsForm.querySelector('input[name="work_type"]:checked')?.value;
    const typeValue = typeKey ? TYPE_MAP[typeKey] : null;

    // Даты
    const dateStart = document.getElementById('date_start')?.value || null;
    const dateEnd = document.getElementById('date_end')?.value || null;

    // План дата тип
    const planDateType = controlsForm.querySelector('input[name="plan_date_type"]:checked')?.value || 'all';

    // Лимит
    const limit = document.getElementById('limit')?.value || '100';

    // Сортировка
    const sortAsc = controlsForm.querySelector('input[name="sort_asc"]:checked')?.value || 'false';

    return {
        priority: priorityValues,
        status: statusValue,
        type: typeValue,
        date_start: dateStart,
        date_end: dateEnd,
        plan_date_type: planDateType,
        limit,
        sort_asc: sortAsc
    };
}

function buildQuery(filters) {
    const params = new URLSearchParams();
    if (filters.priority?.length) params.set('priority', filters.priority.join(','));
    if (filters.status) params.set('status', filters.status);
    if (filters.type) params.set('type', filters.type);
    if (filters.date_start) params.set('date_start', filters.date_start);
    if (filters.date_end) params.set('date_end', filters.date_end);
    if (filters.plan_date_type && filters.plan_date_type !== 'all') params.set('plan_date_type', filters.plan_date_type);
    if (filters.limit) params.set('limit', filters.limit);
    if (filters.sort_asc) params.set('sort_asc', filters.sort_asc);

    return `/api/task/all?${params.toString()}`;
}

function setTopBadges(filters) {
    if (typeBadgeEl) typeBadgeEl.textContent = filters.type || 'Все типы';
    if (statusBadgeEl) statusBadgeEl.textContent = filters.status || 'Все статусы';
}

function formatDateYMDToRu(ymd) {
    if (!ymd) return '—';
    // ожидается формат YYYY-MM-DD
    const [y, m, d] = String(ymd).split('-');
    if (!y || !m || !d) return ymd;
    return `${d.padStart(2, '0')}.${m.padStart(2, '0')}.${y}`;
}

function formatDateTimeToRu(dt) {
    if (!dt) return '—';
    // ожидается ISO: YYYY-MM-DDTHH:mm:ss[.SSS]
    const [date, time] = String(dt).split('T');
    const ruDate = formatDateYMDToRu(date);
    if (!time) return ruDate;
    const [hh, mm] = time.split(':');
    return `${ruDate} ${String(hh ?? '').padStart(2, '0')}:${String(mm ?? '').padStart(2, '0')}`;
}

function escapeHtml(str) {
    return String(str ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

// Создание DOM узла карточки заявки
function createRequestCard(task, isUrgent = false) {
    // Приоритет -> класс
    const priorityClass =
        task.priority === 'Высокий' ? 'priority-high' :
            task.priority === 'Нормальный' ? 'priority-medium' :
                'priority-low';

    // Поля
    const numberJournal = task.number_journal || task.ext_number?.toString() || '—';
    const nameType = task.name_type || '';
    const comment = task.comment && task.comment !== 'Отсутствует' ? ` (${task.comment})` : '';
    const description = `${nameType}${comment}`.trim();
    const location = task.location_repair || '—';
    const fullName = task.full_name || '—';
    const planDate = isUrgent ? 'новая' : formatDateYMDToRu(task.plan_complete_date || null);

    // Карточка
    const card = document.createElement('div');
    card.className = `request-card ${priorityClass}${isUrgent ? ' urgent' : ''}`;
    card.dataset.extNumber = task.ext_number;

    // Номер заявки (левый столбец)
    const colNumber = document.createElement('div');
    colNumber.className = 'field-group left-col';
    colNumber.innerHTML = `
    <div class="field-value">
      <div class="badge badge-none">${escapeHtml(numberJournal)}</div>
    </div>
  `;
    card.appendChild(colNumber);

    // Разделитель
    card.appendChild(createDivider());

    // Описание (2 строки, обрезка)
    const colDesc = document.createElement('div');
    colDesc.className = 'field-group full-lenght';
    colDesc.innerHTML = `<div class="description-text">${escapeHtml(description)}</div>`;
    card.appendChild(colDesc);

    card.appendChild(createDivider());

    // Локация
    const colLoc = document.createElement('div');
    colLoc.className = 'field-group';
    colLoc.innerHTML = `
    <div class="field-value">
      <div class="badge badge-none">${escapeHtml(location)}</div>
    </div>
  `;
    card.appendChild(colLoc);

    card.appendChild(createDivider());

    // ФИО
    const colFio = document.createElement('div');
    colFio.className = 'field-group';
    colFio.innerHTML = `
    <div class="field-value">
      <div class="badge badge-none">${escapeHtml(fullName)}</div>
    </div>
  `;
    card.appendChild(colFio);

    card.appendChild(createDivider());

    // План. дата выполнения
    const colPlan = document.createElement('div');
    colPlan.className = 'field-group';
    colPlan.innerHTML = `
    <div class="field-value">
      <div class="badge ${isUrgent ? 'badge-urgent' : 'badge-none'}">${escapeHtml(planDate)}</div>
    </div>
  `;
    card.appendChild(colPlan);

    return card;
}

function createDivider() {
    const div = document.createElement('div');
    div.className = 'vertical-divider';
    return div;
}

function renderTasks(tasks) {
    requestsContainer.innerHTML = '';

    if (!tasks || !Array.isArray(tasks) || tasks.length === 0) {
        const empty = document.createElement('div');
        empty.className = 'empty-state';
        empty.textContent = 'Заявок не найдено';
        requestsContainer.appendChild(empty);
        return;
    }

    // Выделяем срочные: Высокий + Ремонт + В обработке
    const urgent = [];
    const others = [];
    tasks.forEach(t => {
        if (t.priority === 'Высокий' && t.type === 'Ремонт' && t.status === 'В обработке') {
            urgent.push(t);
        } else {
            others.push(t);
        }
    });

    // Сначала срочные
    urgent.forEach(t => {
        requestsContainer.appendChild(createRequestCard(t, true));
    });

    // Затем остальные
    others.forEach(t => {
        requestsContainer.appendChild(createRequestCard(t, false));
    });
}

async function loadTasks() {
    const filters = getFilters();
    setTopBadges(filters);

    // Плашка загрузки
    requestsContainer.innerHTML = '<div class="info">Загрузка...</div>';

    const url = buildQuery(filters);
    try {
        const res = await fetch(url, { method: 'GET' });
        if (!res.ok) {
            const text = await res.text();
            throw new Error(text || `Ошибка запроса: ${res.status}`);
        }
        const data = await res.json();
        renderTasks(data);
    } catch (e) {
        console.error(e);
        requestsContainer.innerHTML = `<div class="error">Не удалось загрузить заявки: ${escapeHtml(e.message)}</div>`;
    }
}

function initFiltersToggle() {
    toggleFiltersBtn?.addEventListener('click', function () {
        const controls = document.getElementById('controls');
        const body = document.body;

        controls.classList.toggle('hidden');
        body.classList.toggle('controls-open');

        // Меняем текст кнопки
        if (controls.classList.contains('hidden')) {
            this.textContent = '☰ Фильтры';
        } else {
            this.textContent = '✕ Скрыть фильтры';
        }
    });
}

// Делегирование клика по карточке -> модал с деталями
function initRequestsClick() {
    requestsContainer.addEventListener('click', async (e) => {
        const card = e.target.closest('.request-card');
        if (!card || !card.dataset.extNumber) return;

        const ext = card.dataset.extNumber;
        openModal();
        modalTitleExt.textContent = `#${ext}`;
        modalBody.innerHTML = `<div class="info">Загрузка...</div>`;

        try {
            const res = await fetch(`/api/task?ext_number=${encodeURIComponent(ext)}`);
            if (!res.ok) {
                const text = await res.text();
                throw new Error(text || `Ошибка запроса: ${res.status}`);
            }
            const task = await res.json();
            renderModalDetails(task);
        } catch (err) {
            console.error(err);
            modalBody.innerHTML = `<div class="error">Не удалось загрузить детали: ${escapeHtml(err.message)}</div>`;
        }
    });
}

function renderModalDetails(task) {
    const rows = [];

    // Порядок показа: основные поля в начале
    const preferredOrder = [
        'ext_number', 'number_journal', 'status', 'type', 'priority', 'name_type', 'comment',
        'location_repair', 'lokation_repair', 'full_name', 'plan_complete_date', 'plan_complet_date',
        'application_date', 'date_ready', 'closing_date',
        'count', 'drawing_number', 'page_count', 'division', 'tg_id', 'code_users', 'email', 'phone',
        'comment_ready', 'comments_closing', 'comment_shift',
        'category_change', 'amos_order_number', 'department_ogm',
        'photo_1', 'photo_2', 'photo_3', 'id'
    ];

    const seen = new Set();

    function pushField(key) {
        if (seen.has(key)) return;
        if (!(key in task)) return;

        let val = task[key];
        if (val == null || val === '') return;

        const label = FIELD_LABELS[key] || key;

        // форматирование дат
        if (key === 'plan_complete_date' || key === 'plan_complet_date') {
            val = formatDateYMDToRu(val);
        } else if (key === 'application_date' || key === 'date_ready' || key === 'closing_date') {
            val = formatDateTimeToRu(val);
        }

        // фото как ссылку
        if (key.startsWith('photo_') && typeof val === 'string') {
            const url = escapeHtml(val);
            val = `<a href="${url}" target="_blank" rel="noopener noreferrer">${url}</a>`;
        } else {
            val = escapeHtml(val);
        }

        rows.push(`<div class="detail-row"><div class="detail-key">${escapeHtml(label)}</div><div class="detail-value">${val}</div></div>`);
        seen.add(key);
    }

    preferredOrder.forEach(pushField);

    // добить оставшиеся ключи
    Object.keys(task).forEach(k => pushField(k));

    modalBody.innerHTML = `<div class="details-grid">${rows.join('')}</div>`;
}

function openModal() {
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
}
function closeModal() {
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
    modalTitleExt.textContent = '';
    modalBody.innerHTML = '';
}

function initModal() {
    modalBackdrop?.addEventListener('click', closeModal);
    modalClose?.addEventListener('click', closeModal);
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') closeModal();
    });
}

function init() {
    initFiltersToggle();
    initRequestsClick();
    initModal();

    // Сабмит формы фильтров
    controlsForm.addEventListener('submit', (e) => {
        e.preventDefault();
        loadTasks();
    });

    // Начальная загрузка
    loadTasks();

    // Автообновление раз в минуту
    setInterval(loadTasks, 60_000);
}

document.addEventListener('DOMContentLoaded', init);